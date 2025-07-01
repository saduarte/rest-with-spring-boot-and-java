package br.com.saduarte.integrationtests.controllers.withyaml;

import br.com.saduarte.config.TestConfigs;
import br.com.saduarte.integrationtests.controllers.withyaml.mapper.YAMLMapper;
import br.com.saduarte.integrationtests.dto.AccountCredentialsDTO;
import br.com.saduarte.integrationtests.dto.TokenDTO;
import br.com.saduarte.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerYamlTest extends AbstractIntegrationTest {

    private static TokenDTO tokenDTO;
    private static YAMLMapper objectMapper;


    @BeforeAll
    static void setUp() {
        objectMapper = new YAMLMapper();
        tokenDTO = new TokenDTO();
    }

    @Test
    @Order(1)
    void signin() throws JsonProcessingException {
        AccountCredentialsDTO credentials =
                new AccountCredentialsDTO("sabrina", "admin234");

        tokenDTO = given()
                .config(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().
                                                encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .basePath("/auth/signin")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(credentials, objectMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenDTO.class, objectMapper);

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }

    @Test
    @Order(2)
    void refreshToken() throws JsonProcessingException {
        tokenDTO = given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().
                                                encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .basePath("/auth/refresh")
                .port(TestConfigs.SERVER_PORT)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("username", tokenDTO.getUsername())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getRefreshToken())
                .when()
                .put("{username}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenDTO.class, objectMapper);

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }
}