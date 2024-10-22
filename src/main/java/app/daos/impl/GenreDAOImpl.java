package app.daos.impl;

import app.daos.IDAO;
import app.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.Set;
import java.util.stream.Collectors;

public class GenreDAOImpl implements IDAO<Genre, Long> {

    private static GenreDAOImpl instance;
    private final EntityManagerFactory emf;

    private GenreDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static GenreDAOImpl getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new GenreDAOImpl(emf);
        }

        return instance;
    }

    @Override
    public Genre create(Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(genre);
            em.getTransaction().commit();
            return genre;
        }
    }

    @Override
    public Genre getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Genre foundGenre = em.find(Genre.class, id);

            if (foundGenre == null) {
                throw new EntityNotFoundException(String.format("Genre with id %d could not be found", id));
            }

            return foundGenre;
        }
    }

    @Override
    public Set<Genre> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g", Genre.class);
            return query.getResultStream().collect(Collectors.toSet());
        }
    }

    @Override
    public Genre update(Long id, Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            Genre foundGenre = em.find(Genre.class, id);

            if (foundGenre == null) {
                throw new EntityNotFoundException(String.format("Genre with id %d does not exist", genre.getId()));
            }

            em.getTransaction().begin();

            if (foundGenre.getName() != null) {
                foundGenre.setName(genre.getName());
            }

            em.getTransaction().commit();
            return foundGenre;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Genre foundGenre = em.find(Genre.class, id);

            if (foundGenre == null) {
                throw new EntityNotFoundException(String.format("Genre with id %d does not exist", id));
            }

            em.getTransaction().begin();
            em.remove(foundGenre);
            em.getTransaction().commit();
        }
    }
}
