package app;

import app.entities.Genre;
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
