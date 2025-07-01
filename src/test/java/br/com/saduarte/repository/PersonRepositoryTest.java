package br.com.saduarte.repository;

import br.com.saduarte.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.saduarte.model.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository repository;

    private static Person person;

    @BeforeAll
    static void setUp() {
        person = new Person();
    }

    @Test
    @Order(1)
    void findPeopleByName() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.ASC, "firstName"));

        person = repository.findPeopleByName("lly", pageable).getContent().get(0);

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Claughton", person.getLastName());
        assertEquals("Suite 53", person.getAddress());
        assertEquals("Male", person.getGender());
        assertTrue(person.getEnabled());
    }

    @Test
    @Order(2)
    void disablePerson() {

        Long id = person.getId();
        repository.disablePerson(id);

        var result = repository.findById(id);
        person = result.get();

        assertNotNull(person);
        assertNotNull(person.getId());
        assertEquals("Dilly", person.getFirstName());
        assertEquals("Claughton", person.getLastName());
        assertEquals("Suite 53", person.getAddress());
        assertEquals("Male", person.getGender());
        assertFalse(person.getEnabled());
    }
}