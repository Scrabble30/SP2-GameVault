package app.dao.impl;

import app.dao.AbstractDAO;
import app.entity.Genre;
import jakarta.persistence.*;

public class GenreDAOImpl extends AbstractDAO<Genre, Long> {

    private static GenreDAOImpl instance;

    private GenreDAOImpl(EntityManagerFactory emf) {
        super(emf, Genre.class);
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
            Genre foundGenre = em.find(Genre.class, genre.getId());

            if (foundGenre != null) {
                throw new EntityExistsException(String.format("Genre with id %d already exists", genre.getId()));
            }

            em.getTransaction().begin();
            em.persist(genre);
            em.getTransaction().commit();

            return genre;
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

            if (genre.getName() != null) {
                foundGenre.setName(genre.getName());
            }

            em.getTransaction().commit();

            return foundGenre;
        }
    }
}
