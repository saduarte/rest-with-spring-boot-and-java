package br.com.saduarte.services;

import br.com.saduarte.controllers.PersonController;
import br.com.saduarte.data.dto.PersonDTO;
import br.com.saduarte.exception.BadRequestException;
import br.com.saduarte.exception.FileStorageException;
import br.com.saduarte.exception.RequiredObjectIsNullException;
import br.com.saduarte.exception.ResourceNotFoundException;
import br.com.saduarte.file.exporter.contract.PersonExporter;
import br.com.saduarte.file.exporter.factory.FileExporterFactory;
import br.com.saduarte.file.importer.contract.FileImporter;
import br.com.saduarte.file.importer.factory.FileImporterFactory;
import br.com.saduarte.model.Person;
import br.com.saduarte.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static br.com.saduarte.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {

    private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

    @Autowired
    PagedResourcesAssembler<PersonDTO> assembler;

    @Autowired
    PersonRepository repository;

    @Autowired
    FileImporterFactory importer;

    @Autowired
    FileExporterFactory exporter;

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {

        logger.info("Finding all people!");

        var people = repository.findAll(pageable);

        return buildPagedModel(pageable, people);
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName,Pageable pageable) {

        logger.info("Finding by Name people!");

        var people = repository.findPeopleByName(firstName, pageable);

        return buildPagedModel(pageable, people);
    }

    public PersonDTO findById(Long id){
        logger.info("Finding one Person!");
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        var dto = parseObject(entity, PersonDTO.class);

        addHateoasLinks(dto);

        return dto;
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {

        logger.info("Exporting a people page!");

        var people = repository.findAll(pageable).map(person -> parseObject(person, PersonDTO.class)).getContent();

        try {
            PersonExporter exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportPeople(people);
        } catch (Exception e) {
            throw new RuntimeException("Error during file export",e);
        }
    }

    public PersonDTO create(PersonDTO person){

        if (person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one Person!");
        var entity = parseObject(person, Person.class);
        var dto = parseObject(repository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public PersonDTO upDate(PersonDTO person){

        if (person == null) throw new RequiredObjectIsNullException();

        logger.info("Update one Person!");
        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        entity.setEnabled(person.getEnabled());
        entity.setPhotoUrl(person.getPhotoUrl());
        entity.setProfileUrl(person.getProfileUrl());
        var dto =  parseObject(repository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id){
        logger.info("Deleting one Person!");
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        repository.delete(entity);
    }

    public List<PersonDTO> massCreation(MultipartFile file) {

        logger.info("importando pessoas do arquivo!");

        if (file.isEmpty()) throw  new BadRequestException("Insira um arquivo válido");

        try (InputStream inputStream = file.getInputStream()){

            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("Nome do arquivo não pode ser nulo"));

            FileImporter importer = this.importer.getImporter(fileName);

            List<Person> entities = importer.importFile(inputStream)
                    .stream()
                    .map(dto -> repository.save(parseObject(dto, Person.class)))
                    .toList();

            return entities.stream()
                    .map( entity -> {
                        var dto = parseObject(entity, PersonDTO.class);
                        addHateoasLinks(dto);
                        return dto;
                    })
                    .toList();

        } catch (Exception e) {
            throw  new FileStorageException("Erro ao processar o arquivo");
        }

    }

    @Transactional
    public PersonDTO disablePerson(Long id){
        logger.info("Disabling one Person!");

        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        repository.disablePerson(id);

        var entity = repository.findById(id).get();
        var dto =  parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public Resource exportPerson(Long id, String acceptHeader){
        logger.info("Exporting data of one Person!");

        var person = repository.findById(id)
                .map(entity -> parseObject(entity, PersonDTO.class) )
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        try {
            PersonExporter exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportPerson(person);
        } catch (Exception e) {
            throw new RuntimeException("Erro durring file export!",e);
        }
    }



    private void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(1,12, "asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findByName("", 1, 12, "asc")).withRel("findByName").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).disablePerson(dto.getId())).withRel("diseble").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));

        dto.add(linkTo(methodOn(PersonController.class).exportPage(1,12, "asc", null)).withRel("exportPage").withType("GET"));
    }

    private PagedModel<EntityModel<PersonDTO>> buildPagedModel(Pageable pageable, Page<Person> people) {
        var peopleWithLinks = people.map(person -> {
            var dto = parseObject(person, PersonDTO.class);
            addHateoasLinks(dto);
            return dto;
        });

        Link findAllLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), String.valueOf(pageable.getSort()))).withSelfRel();

        return assembler.toModel(peopleWithLinks, findAllLink);
    }

}
