package br.com.saduarte.integrationtests.controllers.withxml;

import br.com.saduarte.config.TestConfigs;
import br.com.saduarte.integrationtests.dto.AccountCredentialsDTO;
import br.com.saduarte.integrationtests.dto.TokenDTO;
import br.com.saduarte.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerXmlTest extends AbstractIntegrationTest {

    private static TokenDTO tokenDTO;
    private static XmlMapper objectMapper;

    @BeforeAll
    static void setUp() {
        objectMapper= new XmlMapper();
        tokenDTO = new TokenDTO();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    @Order(1)
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

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }

    @Test
    @Order(2)
    void refreshToken() throws JsonProcessingException {
        var content = given()
                .basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .pathParam("username", tokenDTO.getUsername())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getRefreshToken())
                .when()
                .put("{username}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        tokenDTO = objectMapper.readValue(content, TokenDTO.class);

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }
}