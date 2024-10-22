package app.daos;

import app.Populator;
import app.config.HibernateConfig;
import app.daos.impl.GenreDAOImpl;
import app.entities.Genre;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class GenreDAOImplTest {

    private static Populator populator;
    private static GenreDAOImpl genreDAO;

    private List<Genre> genres;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populator = new Populator(emf);
        genreDAO = GenreDAOImpl.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        genres = populator.createGenres();
        populator.persist(genres);
    }

    @AfterEach
    void tearDown() {
        populator.cleanup(Genre.class);
    }

    @Test
    void create() {
        Genre lastGenre = genres.get(genres.size() - 1);
        Genre expected = Genre.builder()
                .id(lastGenre.getId() + 1)
                .name("New Genre")
                .build();

        Genre actual = genreDAO.create(expected);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void getById() {
        Genre expected = genres.get(0);
        Genre actual = genreDAO.getById(expected.getId());

        assertThat(actual, equalTo(expected));
    }

    @Test
    void getAll() {
        Set<Genre> expected = new HashSet<>(genres);
        Set<Genre> actual = genreDAO.getAll();

        assertThat(actual, equalTo(expected));
    }

    @Test
    void update() {
        Genre expected = genres.get(0);
        expected.setName("New Genre Name");

        Genre actual = genreDAO.update(expected.getId(), expected);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void delete() {
        Genre genre = genres.get(0);

        genreDAO.delete(genre.getId());
        assertThrowsExactly(EntityNotFoundException.class, () -> genreDAO.getById(genre.getId()));
    }
}