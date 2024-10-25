package app;

import app.dto.GameDTO;
import app.entity.*;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        List<Role> roles = createRoles();
        persist(roles);

        List<User> users = createUsers(roles);
        persist(users);

        List<Review> reviews = createReviews(users, games);
        persist(reviews);
        persist(games);

        logger.info("Data population complete");
    }

    public List<Review> createReviews(List<User> users, List<Game> games) {
        List<Review> reviews = List.of(
                new Review(
                        null,
                        users.get(0).getUsername(),
                        games.get(0).getId(),
                        8.5,
                        "Awesome game"
                ),
                new Review(
                        null,
                        users.get(0).getUsername(),
                        games.get(1).getId(),
                        6.0,
                        "Meh game"
                ),
                new Review(
                        null,
                        users.get(1).getUsername(),
                        games.get(1).getId(),
                        2.8,
                        "Bad game"
                )
        );

        Map<Long, Game> gameMap = games.stream().collect(Collectors.toMap(Game::getId, game -> game));

        reviews.stream()
                .collect(Collectors.groupingBy(Review::getGameId))
                .forEach((gameId, gameReviews) -> {
                    Game game = gameMap.get(gameId);

                    game.setRating(gameReviews.stream().mapToDouble(Review::getRating).average().orElse(0));
                    game.setRatingCount((long) gameReviews.size());
                });

        return reviews;
    }

    public List<User> createUsers(List<Role> roles) {
        return List.of(
                new User(
                        "User1",
                        "1234",
                        Set.of(roles.get(0))
                ),
                new User(
                        "User2",
                        "1234",
                        Set.of(roles.get(0))
                ),
                new User(
                        "Admin1",
                        "1234",
                        Set.of(roles.get(1))
                )
        );
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
