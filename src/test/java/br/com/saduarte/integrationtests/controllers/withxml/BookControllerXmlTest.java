package br.com.saduarte.integrationtests.controllers.withxml;

import br.com.saduarte.config.TestConfigs;
import br.com.saduarte.integrationtests.dto.AccountCredentialsDTO;
import br.com.saduarte.integrationtests.dto.BookDTO;
import br.com.saduarte.integrationtests.dto.TokenDTO;
import br.com.saduarte.integrationtests.dto.wrappers.xmlandyaml.PagedModelBook;
import br.com.saduarte.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.Date;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;

    private static BookDTO book;
    private static TokenDTO tokenDTO;

    @BeforeAll
    static void setUp() {
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        book = new BookDTO();
        tokenDTO =  new TokenDTO();
    }

    @Test
    @Order(0)
    void signin() throws JsonProcessingException {

        AccountCredentialsDTO credentials = new AccountCredentialsDTO("sabrina", "admin234"); //{pbkdf2}

        var content = given()
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(credentials)
                .when()
                .post()
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        tokenDTO = objectMapper.readValue(content, TokenDTO.class);

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_MEU_SITE)
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getAccessToken())
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }

    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockBook();

        var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
                .body(book)
            .when()
                .post()
            .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
                .body()
                    .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertNotNull(book.getId());
        assertEquals("Docker Deep Dive", book.getTitle());
        assertEquals("Nigel Poulton", book.getAuthor());
        assertEquals(55.99, book.getPrice());
    }
    
    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {

        book.setTitle("Docker Deep Dive - Updated");

        var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
                .body(book)
            .when()
                .put()
            .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
                .body()
                    .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertNotNull(book.getId());
        assertEquals("Docker Deep Dive - Updated", book.getTitle());
        assertEquals("Nigel Poulton", book.getAuthor());
        assertEquals(55.99, book.getPrice());
    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                    .pathParam("id", book.getId())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                    .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                    .body()
                        .asString();

        BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertTrue(createdBook.getId() > 0);

        assertNotNull(createdBook.getId());
        assertNotNull(book.getId());
        assertEquals("Docker Deep Dive - Updated", book.getTitle());
        assertEquals("Nigel Poulton", book.getAuthor());
        assertEquals(55.99, book.getPrice());
    }

    @Test
    @Order(4)
    void deleteTest() throws JsonProcessingException {

        given(specification)
                .pathParam("id", book.getId())
            .when()
                .delete("{id}")
            .then()
                .statusCode(204);
    }


    @Test
    @Order(5)
    void findAllTest() throws JsonProcessingException {

        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .queryParams("page", 0 , "size", 12, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);
        var books = wrapper.getContent();

        BookDTO bookOne = books.get(0);

        assertNotNull(bookOne.getId());
        assertNotNull(bookOne.getTitle());
        assertNotNull(bookOne.getAuthor());
        assertNotNull(bookOne.getPrice());
        assertTrue(bookOne.getId() > 0);
        assertEquals("Agile and Iterative Development: A Manager’s Guide", bookOne.getTitle());
        assertEquals("Craig Larman", bookOne.getAuthor());
        assertEquals(43.82, bookOne.getPrice());

        BookDTO foundBookFive = books.get(4);

        assertNotNull(foundBookFive.getId());
        assertNotNull(foundBookFive.getTitle());
        assertNotNull(foundBookFive.getAuthor());
        assertNotNull(foundBookFive.getPrice());
        assertTrue(foundBookFive.getId() > 0);
        assertEquals("Agile and Iterative Development: A Manager’s Guide", foundBookFive.getTitle());
        assertEquals("Craig Larman", foundBookFive.getAuthor());
        assertEquals(128.07, foundBookFive.getPrice());
    }

    private void mockBook() {
        book.setTitle("Docker Deep Dive");
        book.setAuthor("Nigel Poulton");
        book.setPrice(Double.valueOf(55.99));
        book.setLaunchDate(new Date());
    }
}