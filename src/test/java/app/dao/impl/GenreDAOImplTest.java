package app.dao.impl;

import app.PopulatorTestUtil;
import app.config.HibernateConfig;
import app.entity.Genre;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class GenreDAOImplTest {

    private static PopulatorTestUtil populatorTestUtil;
    private static GenreDAOImpl genreDAO;

    private List<Genre> genres;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populatorTestUtil = new PopulatorTestUtil(emf);
        genreDAO = GenreDAOImpl.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        genres = populatorTestUtil.createGenres();
        populatorTestUtil.persist(genres);
    }

    @AfterEach
    void tearDown() {
        populatorTestUtil.cleanup(Genre.class);
    }

    @Test
    void create() {
        Genre lastGenre = genres.stream().max(Comparator.comparing(Genre::getId)).orElseThrow();
        Genre expected = Genre.builder()
                .id(lastGenre.getId() + 1)
                .name("New Genre")
                .build();

        Genre actual = genreDAO.create(expected);

        assertThat(actual, is(expected));
    }

    @Test
    void getById() {
        Genre expected = genres.get(0);
        Genre actual = genreDAO.getById(expected.getId());

        assertThat(actual, is(expected));
    }

    @Test
    void getAll() {
        Set<Genre> expected = new HashSet<>(genres);
        Set<Genre> actual = genreDAO.getAll();

        assertThat(actual, is(expected));
    }

    @Test
    void update() {
        Genre expected = genres.get(0);
        expected.setName("New Genre Name");

        Genre actual = genreDAO.update(expected.getId(), expected);

        assertThat(actual, is(expected));
    }

    @Test
    void delete() {
        Genre genre = genres.get(0);

        genreDAO.delete(genre.getId());
        assertThrowsExactly(EntityNotFoundException.class, () -> genreDAO.getById(genre.getId()));
    }
}