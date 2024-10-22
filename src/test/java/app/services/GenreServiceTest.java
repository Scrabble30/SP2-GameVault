package app.services;

import app.dtos.GenreDTO;
import app.services.impl.GenreServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

class GenreServiceTest {

    private static GenreService genreService;

    private List<GenreDTO> genreDTOList;

    @BeforeAll
    static void beforeAll() {
        ObjectMapper objectMapper = new ObjectMapper();

        genreService = new GenreServiceImpl(objectMapper);
    }

    @BeforeEach
    void setUp() {
        genreDTOList = List.of(
                new GenreDTO(1L, "Racing"),
                new GenreDTO(2L, "Shooter"),
                new GenreDTO(3L, "Adventure"),
                new GenreDTO(4L, "Action"),
                new GenreDTO(5L, "RPG"),
                new GenreDTO(6L, "Fighting"),
                new GenreDTO(7L, "Puzzle"),
                new GenreDTO(10L, "Strategy"),
                new GenreDTO(11L, "Arcade"),
                new GenreDTO(14L, "Simulation"),
                new GenreDTO(15L, "Sports"),
                new GenreDTO(17L, "Card"),
                new GenreDTO(19L, "Family"),
                new GenreDTO(28L, "Board Games"),
                new GenreDTO(34L, "Educational"),
                new GenreDTO(40L, "Casual"),
                new GenreDTO(51L, "Indie"),
                new GenreDTO(59L, "Massively Multiplayer"),
                new GenreDTO(83L, "Platformer")
        );
    }

    @Test
    void getGenres() {
        Set<GenreDTO> expected = new HashSet<>(genreDTOList);
        Set<GenreDTO> actual = genreService.getGenres();

        assertThat(actual.size(), equalTo(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }
}