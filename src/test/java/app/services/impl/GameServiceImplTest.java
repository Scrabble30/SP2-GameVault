package app.services.impl;

import app.dtos.GameDTO;
import app.dtos.GenreDTO;
import app.dtos.ParentPlatFormDTO;
import app.dtos.PlatformDTO;
import app.services.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameServiceImplTest {

    private static GameService gameService;
    private GameDTO gameDTO;

    @BeforeAll
    static void beforeAll() {
        ObjectMapper objectMapper = new ObjectMapper();
        gameService = new GameServiceImpl(objectMapper);
    }

    @BeforeEach
    void setUp() {
        Set<ParentPlatFormDTO> parentPlatFormDTOSet = new HashSet<>();
        parentPlatFormDTOSet.add(new ParentPlatFormDTO(new PlatformDTO(1L, "PC")));
        parentPlatFormDTOSet.add(new ParentPlatFormDTO(new PlatformDTO(2L, "PlayStation")));
        parentPlatFormDTOSet.add(new ParentPlatFormDTO(new PlatformDTO(4L, "iOS")));
        parentPlatFormDTOSet.add(new ParentPlatFormDTO(new PlatformDTO(8L, "Android")));

        Set<GenreDTO> genreDTOSet = new HashSet<>();
        genreDTOSet.add(new GenreDTO(4L, "Action"));
        genreDTOSet.add(new GenreDTO(11L, "Arcade"));
        genreDTOSet.add(new GenreDTO(7L, "Puzzle"));

        String description = "\u003Cp\u003EUse the unique powers of the Angry Birds to destroy the greedy pigs&#39; " +
                "defenses!\u003Cbr /\u003E\nThe survival of the Angry Birds is at stake. " +
                "Dish out revenge on the greedy pigs who stole their eggs. " +
                "Use the unique powers of each bird to destroy the pigs’ defenses. " +
                "Angry Birds features challenging physics-based gameplay and hours of replay value. " +
                "Each level requires logic, skill and force to solve." +
                "\u003Cbr /\u003E\nIf you get stuck in the game, you can purchase the Mighty Eagle! " +
                "Mighty Eagle is a one-time in-app purchase in Angry Birds that gives unlimited use. " +
                "This phenomenal creature will soar from the skies to wreak havoc and smash the pesky pigs into oblivion. " +
                "There’s just one catch: you can only use the aid of Mighty Eagle to pass a level once per hour. " +
                "Mighty Eagle also includes all new gameplay goals and achievements!" +
                "\u003Cbr /\u003E\nIn addition to the Mighty Eagle, Angry Birds now has power-ups! " +
                "Boost your birds’ abilities and three-star levels to unlock secret content! " +
                "Angry Birds now has the following amazing power-ups: " +
                "Sling Scope for laser targeting, King Sling for maximum flinging power, " +
                "Super Seeds to supersize your birds, and Birdquake to shake pigs’ defenses to the ground!" +
                "\u003Cbr /\u003E\nHAVING TROUBLE? Head over to https://support.rovio.com " +
                "where you can browse FAQs or submit a request to our support flock!" +
                "\u003C/p\u003E\n\u003Ch1\u003E1 IPHONE PAID APP in US, UK, Canada, Italy, Germany, " +
                "Russia, Sweden, Denmark, Finland, Singapore, Poland, France, Netherlands, Malta, " +
                "Greece, Austria, Australia, Turkey, UAE, Saudi Arabia, Israel, Belgium, Norway, " +
                "Hungary, Malaysia, Luxembourg, Portugal, Czech Republic, Spain, Ireland, Romania, " +
                "New Zealand, Latvia, Lithuania, Estonia, Nicaragua, Kazakhstan, Argentina, Bulgaria, " +
                "Slovakia, Slovenia, Mauritius, Chile, Hong Kong, Pakistan, Taiwan, Colombia, " +
                "Indonesia, Thailand, India, Kenya, Macedonia, Croatia, Macau, Paraguay, Peru, Armenia, " +
                "Philippines, Vietnam, Jordan and Kuwait.\u003C/h1\u003E\n\u003Ch1\u003E" +
                "1 IPHONE PAID GAME in more countries than we can count!\u003C/h1\u003E\n\u003Cp\u003E" +
                "Terms of Use: http://www.rovio.com/eula\u003Cbr /\u003E\n" +
                "Privacy Policy: http://www.rovio.com/privacy\u003Cbr /\u003E\n" +
                "This application may require internet connectivity and subsequent data transfer charges may apply." +
                "\u003Cbr /\u003E\nImportant Message for Parents\u003Cbr /\u003E\n" +
                "This game may include:\u003Cbr /\u003E\n" +
                "- Direct links to social networking websites that are intended for an audience over the age of 13." +
                "\u003Cbr /\u003E\n- Direct links to the internet that can take players away from the game with the potential to browse any web page." +
                "\u003Cbr /\u003E\n- Advertising of Rovio products and also products from select partners." +
                "\u003Cbr /\u003E\n- The option to make in-app purchases. The bill payer should always be consulted beforehand.\u003C/p\u003E";

        gameDTO = new GameDTO(
                144L,
                "Angry Birds",
                LocalDate.of(2009, 12, 11),
                "https://media.rawg.io/media/games/fc8/fc839beb76bd63c2a5b176c46bdb7681.jpg",
                80,
                4,
                description,
                null,
                null,
                parentPlatFormDTOSet,
                genreDTOSet
        );
    }

    @Test
    void getAllGames() {
        int expectedSize = 25;

        Set<GameDTO> allGames = gameService.getAllGames();

        assertNotNull(allGames);
        assertThat(allGames.size(), is(expectedSize));
    }

    @Test
    void getAllGamesWithDetails() {
        int expectedSize = 25;

        Set<GameDTO> allGames = gameService.getAllGames();
        Set<GameDTO> allGamesWithDetails = gameService.getAllGamesWithDetails(allGames);
        Optional<GameDTO> gameDTOWithDetails = allGamesWithDetails.stream().findFirst();

        assertThat(allGamesWithDetails.size(), is(expectedSize));
        assertThat(gameDTOWithDetails.isPresent(), is(true));
        gameDTOWithDetails.ifPresent(game ->
                assertThat(game.getDescription(), notNullValue())
        );
    }

    @Test
    void getGameById() {
        Long expectedId = gameDTO.getId();
        GameDTO expectedGameDTO = gameDTO;

        GameDTO actualGameDTO = gameService.getGameById(expectedId);

        assertNotNull(actualGameDTO);
        assertThat(actualGameDTO.getId(), is(expectedId));
        assertThat(actualGameDTO, equalTo(expectedGameDTO));
    }
}