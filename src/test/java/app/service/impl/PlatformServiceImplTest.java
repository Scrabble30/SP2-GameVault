package app.service.impl;

import app.dto.PlatformDTO;
import app.service.PlatformService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class PlatformServiceImplTest {

    private static PlatformService platformService;

    private List<PlatformDTO> platformDTOList;

    @BeforeAll
    static void beforeAll() {
        ObjectMapper objectMapper = new ObjectMapper();

        platformService = new PlatformServiceImpl(objectMapper);
    }

    @BeforeEach
    void setUp() {
        platformDTOList = List.of(
                new PlatformDTO(1L, "PC"),
                new PlatformDTO(2L, "PlayStation"),
                new PlatformDTO(3L, "Xbox"),
                new PlatformDTO(4L, "iOS"),
                new PlatformDTO(5L, "Apple Macintosh"),
                new PlatformDTO(6L, "Linux"),
                new PlatformDTO(7L, "Nintendo"),
                new PlatformDTO(8L, "Android"),
                new PlatformDTO(9L, "Atari"),
                new PlatformDTO(10L, "Commodore / Amiga"),
                new PlatformDTO(11L, "SEGA"),
                new PlatformDTO(12L, "3DO"),
                new PlatformDTO(13L, "Neo Geo"),
                new PlatformDTO(14L, "Web")
        );
    }

    @Test
    void getPlatforms() {
        Set<PlatformDTO> expected = new HashSet<>(platformDTOList);
        Set<PlatformDTO> actual = platformService.getPlatforms();

        assertThat(actual, notNullValue());
        assertThat(actual.size(), is(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }
}