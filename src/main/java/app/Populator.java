package app;

import app.dao.impl.GameDAOImpl;
import app.dao.impl.GenreDAOImpl;
import app.dao.impl.PlatformDAOImpl;
import app.dto.GameDTO;
import app.dto.GenreDTO;
import app.dto.PlatformDTO;
import app.entity.Game;
import app.entity.Genre;
import app.entity.Platform;
import app.entity.Role;
import app.mapper.GameMapper;
import app.mapper.GenreMapper;
import app.mapper.PlatformMapper;
import app.service.GameService;
import app.service.GenreService;
import app.service.PlatformService;
import app.service.impl.GameServiceImpl;
import app.service.impl.GenreServiceImpl;
import app.service.impl.PlatformServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class Populator {

    private final Logger logger = LoggerFactory.getLogger(Populator.class);

    private final EntityManagerFactory emf;

    private final PlatformService platformService;
    private final GenreService genreService;
    private final GameService gameService;

    private final PlatformDAOImpl platformDAO;
    private final GenreDAOImpl genreDAO;
    private final GameDAOImpl gameDAO;

    private final PlatformMapper platformMapper;
    private final GenreMapper genreMapper;
    private final GameMapper gameMapper;

    public Populator(EntityManagerFactory emf) {
        this.emf = emf;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        platformService = new PlatformServiceImpl(objectMapper);
        genreService = new GenreServiceImpl(objectMapper);
        gameService = new GameServiceImpl(objectMapper);

        platformDAO = PlatformDAOImpl.getInstance(emf);
        genreDAO = GenreDAOImpl.getInstance(emf);
        gameDAO = GameDAOImpl.getInstance(emf);

        platformMapper = new PlatformMapper();
        genreMapper = new GenreMapper();
        gameMapper = new GameMapper();
    }

    public void populateData() {
        logger.info("Populating data...");

        Set<PlatformDTO> platformDTOSet = platformService.getPlatforms();
        Set<Platform> platformSet = platformDTOSet.stream().map(platformMapper::convertToEntity).collect(Collectors.toSet());

        Set<GenreDTO> genreDTOSet = genreService.getGenres();
        Set<Genre> genreSet = genreDTOSet.stream().map(genreMapper::convertToEntity).collect(Collectors.toSet());

        Set<GameDTO> gameDTOSet = gameService.getAllGames();
        Set<GameDTO> gamesWithDetailsDTOSet = gameService.getAllGamesWithDetails(gameDTOSet);
        Set<Game> gameSet = gamesWithDetailsDTOSet.stream().map(gameMapper::convertToEntity).collect(Collectors.toSet());

        Set<Role> roleSet = createRoles();

        platformSet.forEach(platformDAO::create);
        genreSet.forEach(genreDAO::create);
        gameSet.forEach(gameDAO::create);
        roleSet.forEach(this::persist);

        logger.info("Data population complete");
    }

    private Set<Role> createRoles() {
        return Set.of(
                new Role("user"),
                new Role("admin")
        );
    }

    private void persist(Role role) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
        }
    }
}