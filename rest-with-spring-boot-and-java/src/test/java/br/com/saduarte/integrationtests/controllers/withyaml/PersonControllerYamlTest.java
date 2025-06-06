package br.com.saduarte.integrationtests.controllers.withyaml;

import br.com.saduarte.config.TestConfigs;
import br.com.saduarte.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import br.com.saduarte.integrationtests.dto.PersonDTO;
import br.com.saduarte.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;
    private static PersonDTO person;

    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();
        person = new PersonDTO();
    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockPerson();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_MEU_SITE)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        var createdPerson = given().config(
                RestAssuredConfig.config().
                        encoderConfig(EncoderConfig.encoderConfig().
                                encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                    .body(person, objectMapper)
                .when()
                    .post()
                .then()
                    .statusCode(200)
                 .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                        .as(PersonDTO.class, objectMapper);

        person= createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);
        assertTrue(createdPerson.getEnabled());

        assertEquals("Ritinha", createdPerson.getFirstName());
        assertEquals("Duarte", createdPerson.getLastName());
        assertEquals("Belo Horizonte", createdPerson.getAddress());
        assertEquals("Female", createdPerson.getGender());

    }

    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        person.setLastName("Silva");

        var createdPerson =  given().config(
                        RestAssuredConfig.config().
                                encoderConfig(EncoderConfig.encoderConfig().
                                        encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                    .body(person, objectMapper)
                .when()
                    .put()
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                    .as(PersonDTO.class, objectMapper);

        person= createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Ritinha", createdPerson.getFirstName());
        assertEquals("Silva", createdPerson.getLastName());
        assertEquals("Belo Horizonte", createdPerson.getAddress());
        assertEquals("Female", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());

    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {

        var createdPerson =  given().config(
                        RestAssuredConfig.config().
                                encoderConfig(EncoderConfig.encoderConfig().
                                        encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                 .pathParam("id", person.getId())
                .when()
                 .get("{id}")
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                    .as(PersonDTO.class, objectMapper);

        person= createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Ritinha", createdPerson.getFirstName());
        assertEquals("Silva", createdPerson.getLastName());
        assertEquals("Belo Horizonte", createdPerson.getAddress());
        assertEquals("Female", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());
    }

    @Test
    @Order(4)
    void disableTest() throws JsonProcessingException {

        var createdPerson =  given().config(
                        RestAssuredConfig.config().
                                encoderConfig(EncoderConfig.encoderConfig().
                                        encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                     .pathParam("id", person.getId())
                .when()
                    .patch("{id}")
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                    .as(PersonDTO.class, objectMapper);

        person= createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);

        assertEquals("Ritinha", createdPerson.getFirstName());
        assertEquals("Silva", createdPerson.getLastName());
        assertEquals("Belo Horizonte", createdPerson.getAddress());
        assertEquals("Female", createdPerson.getGender());
        assertFalse(createdPerson.getEnabled());
    }

    @Test
    @Order(5)
    void deleteTest() throws JsonProcessingException {

        given(specification)
                 .pathParam("id", person.getId())
            .when()
                .delete("{id}")
            .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    void findAllTest() throws JsonProcessingException{

        var response = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                    .body()
                    .as(PersonDTO[].class, objectMapper);

        List<PersonDTO> people = Arrays.asList(response);

        PersonDTO personOne = people.get(0);

        assertNotNull(personOne.getId());
        assertTrue(personOne.getId() > 0);

        assertEquals("Camilla", personOne.getFirstName());
        assertEquals("Silva", personOne.getLastName());
        assertEquals("Paraiba - Brasil", personOne.getAddress());
        assertEquals("Female", personOne.getGender());
        assertTrue(personOne.getEnabled());

        PersonDTO personFour = people.get(4);

        assertNotNull(personFour.getId());
        assertTrue(personFour.getId() > 0);

        assertEquals("Sabrina", personFour.getFirstName());
        assertEquals("Duarte", personFour.getLastName());
        assertEquals("Belo Horizonte - Brasil", personFour.getAddress());
        assertEquals("Female", personFour.getGender());
        assertTrue(personFour.getEnabled());


    }


    private void mockPerson() {
        person.setFirstName("Ritinha");
        person.setLastName("Duarte");
        person.setAddress("Belo Horizonte");
        person.setGender("Female");
        person.setEnabled(true);
    }
}