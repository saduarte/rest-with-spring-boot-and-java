package br.com.saduarte.services;

import br.com.saduarte.data.dto.BookDTO;
import br.com.saduarte.exception.RequiredObjectIsNullException;
import br.com.saduarte.model.Book;
import br.com.saduarte.repository.BookRepository;
import br.com.saduarte.uniteTests.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {

    MockBook input;

    @InjectMocks
    private BookServices service;

    @Mock
    BookRepository repository;

    @BeforeEach
    void setUp() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void findById() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        var result = service.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                && link.getHref().endsWith("/api/book/v1/1")
                && link.getType().equals("GET")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                && link.getHref().endsWith("/api/book/v1/1")
                && link.getType().equals("GET")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                && link.getHref().endsWith("/api/book/v1")
                && link.getType().equals("POST")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                && link.getHref().endsWith("/api/book/v1")
                && link.getType().equals("PUT")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                && link.getHref().endsWith("/api/book/v1/1")
                && link.getType().equals("DELETE")
                )
        );

        assertEquals("Algum autor1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Algum Titulo1", result.getTitle());
        assertNotNull( result.getLaunchDate());
    }

    @Test
    void create() {
        Book book = input.mockEntity(1);
        Book persisted = book;
        persisted.setId(1L);

        BookDTO dto = input.mockDTO(1);

        when(repository.save(book)).thenReturn(persisted);

        var result = service.create(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("DELETE")
                )
        );

        assertEquals("Algum autor1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Algum Titulo1", result.getTitle());
        assertNotNull( result.getLaunchDate());
    }

    @Test
    void testeCreateWithNullBook(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> {
                service.create(null);
                });

            String expectedMessege = "Não é permitido persistir uma entidade com valor nulo";
            String actualMessege = exception.getMessage();

            assertTrue(actualMessege.contains(expectedMessege));
    }

    @Test
    void upDate() {
        Book book = input.mockEntity(1);
        Book persisted = book;
        persisted.setId(1L);

        BookDTO dto = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(persisted);

        var result = service.upDate(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT")
                )
        );
        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("DELETE")
                )
        );

        assertEquals("Algum autor1", result.getAuthor());
        assertEquals(25D, result.getPrice());
        assertEquals("Algum Titulo1", result.getTitle());
        assertNotNull( result.getLaunchDate());
    }

    @Test
    void testeUpdateWithNullBook(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> {
                    service.upDate(null);
                });

        String expectedMessege = "Não é permitido persistir uma entidade com valor nulo";
        String actualMessege = exception.getMessage();

        assertTrue(actualMessege.contains(expectedMessege));
    }

    @Test
    void delete() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        service.delete(1L);
        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Book.class));
        verifyNoMoreInteractions(repository);
}


@Test
    void findAll(){

    List<Book> list = input.mockEntityList();

    when(repository.findAll()).thenReturn(list);

    List<BookDTO> livros = service.findAll();

    assertNotNull(livros);
    assertEquals(14, livros.size());

    var livroUm = livros.get(1);

    assertNotNull(livroUm);
    assertNotNull(livroUm.getId());
    assertNotNull(livroUm.getLinks());

    assertNotNull(livroUm.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("self")
                    && link.getHref().endsWith("/api/book/v1/1")
                    && link.getType().equals("GET")
            )
    );
    assertNotNull(livroUm.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("findAll")
                    && link.getHref().endsWith("/api/book/v1/1")
                    && link.getType().equals("GET")
            )
    );
    assertNotNull(livroUm.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("create")
                    && link.getHref().endsWith("/api/book/v1")
                    && link.getType().equals("POST")
            )
    );
    assertNotNull(livroUm.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("update")
                    && link.getHref().endsWith("/api/book/v1")
                    && link.getType().equals("PUT")
            )
    );
    assertNotNull(livroUm.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("delete")
                    && link.getHref().endsWith("/api/book/v1/1")
                    && link.getType().equals("DELETE")
            )
    );

    assertEquals("Algum autor1", livroUm.getAuthor());
    assertEquals(25D, livroUm.getPrice());
    assertEquals("Algum Titulo1", livroUm.getTitle());
    assertNotNull( livroUm.getLaunchDate());

    var livroDois = livros.get(4);

    assertNotNull(livroDois);
    assertNotNull(livroDois.getId());
    assertNotNull(livroDois.getLinks());

    assertNotNull(livroDois.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("self")
                    && link.getHref().endsWith("/api/book/v1/1")
                    && link.getType().equals("GET")
            )
    );
    assertNotNull(livroDois.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("findAll")
                    && link.getHref().endsWith("/api/book/v1/1")
                    && link.getType().equals("GET")
            )
    );
    assertNotNull(livroDois.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("create")
                    && link.getHref().endsWith("/api/book/v1")
                    && link.getType().equals("POST")
            )
    );
    assertNotNull(livroDois.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("update")
                    && link.getHref().endsWith("/api/book/v1")
                    && link.getType().equals("PUT")
            )
    );
    assertNotNull(livroDois.getLinks().stream()
            .anyMatch(link -> link.getRel().value().equals("delete")
                    && link.getHref().endsWith("/api/book/v1/1")
                    && link.getType().equals("DELETE")
            )
    );

    assertEquals("Algum autor4", livroDois.getAuthor());
    assertEquals(25D, livroDois.getPrice());
    assertEquals("Algum Titulo4", livroDois.getTitle());
    assertNotNull( livroDois.getLaunchDate());

    }
}