package app;

import app.entities.Genre;
import app.entities.Platform;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

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
