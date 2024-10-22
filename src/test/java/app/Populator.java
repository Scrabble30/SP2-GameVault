package app;

import app.entities.Game;
import app.entities.Genre;
import app.entities.Platform;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class Populator {

    private final EntityManagerFactory emf;

    public Populator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Genre> createGenres() {
        return List.of(
                new Genre(1L, "Racing"),
                new Genre(2L, "Shooter"),
                new Genre(3L, "Adventure"),
                new Genre(4L, "Action"),
                new Genre(5L, "RPG"),
                new Genre(6L, "Fighting"),
                new Genre(7L, "Puzzle"),
                new Genre(10L, "Strategy"),
                new Genre(11L, "Arcade"),
                new Genre(14L, "Simulation"),
                new Genre(15L, "Sports"),
                new Genre(17L, "Card"),
                new Genre(19L, "Family"),
                new Genre(28L, "Board Games"),
                new Genre(34L, "Educational"),
                new Genre(40L, "Casual"),
                new Genre(51L, "Indie"),
                new Genre(59L, "Massively Multiplayer"),
                new Genre(83L, "Platformer")
        );
    }

    public List<Platform> createPlatforms() {
        return List.of(
                new Platform(1L, "PC"),
                new Platform(2L, "PlayStation"),
                new Platform(3L, "Xbox"),
                new Platform(4L, "iOS"),
                new Platform(5L, "Apple Macintosh"),
                new Platform(6L, "Linux"),
                new Platform(7L, "Nintendo"),
                new Platform(8L, "Android"),
                new Platform(9L, "Atari"),
                new Platform(10L, "Commodore / Amiga"),
                new Platform(11L, "SEGA"),
                new Platform(12L, "3DO"),
                new Platform(13L, "Neo Geo"),
                new Platform(14L, "Web")
        );
    }

    public List<Game> createGames(List<Genre> genres, List<Platform> platforms) {
        return List.of(
                new Game(11L, "Game Title 11", LocalDate.of(2023, 10, 22),
                        "https://example.com/image11.jpg", 85.0, 20,
                        "Description for Game 11", Set.of(platforms.get(0)), Set.of(genres.get(0))),
                new Game(12L, "Game Title 12", LocalDate.of(2024, 1, 15),
                        "https://example.com/image12.jpg", 90.0, 15,
                        "Description for Game 12", Set.of(platforms.get(1)), Set.of(genres.get(1))),
                new Game(13L, "Game Title 13", LocalDate.of(2024, 3, 10),
                        "https://example.com/image13.jpg", 78.0, 25,
                        "Description for Game 13", Set.of(platforms.get(2)), Set.of(genres.get(2))),
                new Game(14L, "Game Title 14", LocalDate.of(2024, 5, 5),
                        "https://example.com/image14.jpg", 92.0, 30,
                        "Description for Game 14", Set.of(platforms.get(3)), Set.of(genres.get(3))),
                new Game(15L, "Game Title 15", LocalDate.of(2024, 7, 12),
                        "https://example.com/image15.jpg", 75.0, 18,
                        "Description for Game 15", Set.of(platforms.get(4)), Set.of(genres.get(4)))
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
