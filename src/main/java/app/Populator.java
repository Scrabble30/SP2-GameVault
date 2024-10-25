package app;

import app.dto.GameDTO;
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

import java.util.List;
import java.util.Set;

public class Populator {

    private final Logger logger = LoggerFactory.getLogger(Populator.class);

    private final EntityManagerFactory emf;

    private final PlatformMapper platformMapper;
    private final GenreMapper genreMapper;
    private final GameMapper gameMapper;

    private final PlatformService platformService;
    private final GenreService genreService;
    private final GameService gameService;

    public Populator(EntityManagerFactory emf) {
        this.emf = emf;

        platformMapper = new PlatformMapper();
        genreMapper = new GenreMapper();
        gameMapper = new GameMapper();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        platformService = new PlatformServiceImpl(objectMapper);
        genreService = new GenreServiceImpl(objectMapper);
        gameService = new GameServiceImpl(objectMapper);
    }

    public void populateData() {
        logger.info("Populating data...");

        List<Platform> platforms = platformService.getPlatforms().stream().map(platformMapper::convertToEntity).toList();
        List<Genre> genres = genreService.getGenres().stream().map(genreMapper::convertToEntity).toList();

        Set<GameDTO> gameDTOList = gameService.getAllGamesWithDetails(gameService.getAllGames());
        List<Game> games = gameDTOList.stream().map(gameMapper::convertToEntity).toList();

        persist(platforms);
        persist(genres);
        persist(games);

        List<Role> roles = createRoles();
        persist(roles);

        logger.info("Data population complete");
    }

    public List<Role> createRoles() {
        return List.of(
                new Role("user"),
                new Role("admin")
        );
    }

    public void persist(List<?> entities) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            entities.forEach(em::persist);
            em.getTransaction().commit();
        }
    }

    public void cleanup(Class<?> entityClass) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM " + entityClass.getSimpleName()).executeUpdate();
            em.getTransaction().commit();
        }
    }
}
