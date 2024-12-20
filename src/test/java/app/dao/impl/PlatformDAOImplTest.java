package app.dao.impl;

import app.PopulatorTestUtil;
import app.config.HibernateConfig;
import app.entity.Platform;
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

class PlatformDAOImplTest {

    private static PopulatorTestUtil populatorTestUtil;
    private static PlatformDAOImpl platformDAO;

    private List<Platform> platforms;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populatorTestUtil = new PopulatorTestUtil(emf);
        platformDAO = PlatformDAOImpl.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        platforms = populatorTestUtil.createPlatforms();
        populatorTestUtil.persist(platforms);
    }

    @AfterEach
    void tearDown() {
        populatorTestUtil.cleanup(Platform.class);
    }

    @Test
    void create() {
        Platform lastPlatform = platforms.stream().max(Comparator.comparing(Platform::getId)).orElseThrow();
        Platform expected = Platform.builder()
                .id(lastPlatform.getId() + 1)
                .name("New Platform")
                .build();

        Platform actual = platformDAO.create(expected);

        assertThat(actual, is(expected));
    }

    @Test
    void getById() {
        Platform expected = platforms.get(0);
        Platform actual = platformDAO.getById(expected.getId());

        assertThat(actual, is(expected));
    }

    @Test
    void getAll() {
        Set<Platform> expected = new HashSet<>(platforms);
        Set<Platform> actual = platformDAO.getAll();

        assertThat(actual, is(expected));
    }

    @Test
    void update() {
        Platform expected = platforms.get(0);
        expected.setName("Updated Platform Name");

        Platform actual = platformDAO.update(expected.getId(), expected);

        assertThat(actual, is(expected));
    }

    @Test
    void delete() {
        Platform platform = platforms.get(0);

        platformDAO.delete(platform.getId());
        assertThrowsExactly(EntityNotFoundException.class, () -> platformDAO.getById(platform.getId()));
    }
}