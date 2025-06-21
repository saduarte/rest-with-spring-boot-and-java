package br.com.saduarte.file.exporter.contract;

import br.com.saduarte.data.dto.PersonDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface PersonExporter {

    Resource exportPeople(List<PersonDTO> people) throws Exception;
    Resource exportPerson(PersonDTO personDTO) throws Exception;
}
