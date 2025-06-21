package br.com.saduarte.file.exporter.impl;

import br.com.saduarte.data.dto.PersonDTO;
import br.com.saduarte.file.exporter.contract.PersonExporter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter implements PersonExporter {
    @Override
    public Resource exportPeople(List<PersonDTO> people) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "First Name", "Last Name", "Address","Gender", "Enable")
                .setSkipHeaderRecord(false)
                .get();


        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)){

            for (PersonDTO person : people){
                csvPrinter.printRecord(
                        person.getId(),
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getGender(),
                        person.getEnabled()
                );
            }
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    public Resource exportPerson(PersonDTO personDTO) throws Exception {
        return null;
    }
}
