package app.daos.impl;

import app.daos.IDAO;
import app.entities.Game;
import app.entities.Review;
import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;

import java.util.Set;
import java.util.stream.Collectors;

public class ReviewDAOImpl implements IDAO<Review, Long> {

    private static ReviewDAOImpl instance;
    private final EntityManagerFactory emf;

    private ReviewDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static ReviewDAOImpl getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new ReviewDAOImpl(emf);
        }

        return instance;
    }

    @Override
    public Review create(Review review) {
        try (EntityManager em = emf.createEntityManager()) {
            User foundUser = em.find(User.class, review.getUsername());

            if (foundUser == null) {
                throw new EntityNotFoundException(String.format("User with username %s could not be found", review.getUsername()));
            }

            Game foundGame = em.find(Game.class, review.getGameId());

            if (foundGame == null) {
                throw new EntityNotFoundException(String.format("Game with id %s could not be found", review.getGameId()));
            }

            em.getTransaction().begin();
            em.persist(review);

            TypedQuery<Double> averageQuery = em.createQuery("SELECT AVG(r.rating) FROM Review r WHERE r.gameId = :gameId", Double.class);
            averageQuery.setParameter("gameId", foundGame.getId());

            foundGame.setRating(averageQuery.getSingleResult());

            TypedQuery<Long> countQuery = em.createQuery("SELECT COUNT(r) FROM Review r WHERE r.gameId = :gameId", Long.class);
            countQuery.setParameter("gameId", foundGame.getId());

            foundGame.setRatingCount(countQuery.getSingleResult());

            em.getTransaction().commit();
            return review;
        }
    }

    @Override
    public Review getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Review foundReview = em.find(Review.class, id);

            if (foundReview == null) {
                throw new EntityNotFoundException(String.format("Review with id %s could not be found", id));
            }

            return foundReview;
        }
    }

    @Override
    public Set<Review> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r", Review.class);
            return query.getResultStream().collect(Collectors.toSet());
        }
    }

    @Override
    public Review update(Long id, Review review) {
        try (EntityManager em = emf.createEntityManager()) {
            Review foundReview = em.find(Review.class, id);

            if (foundReview == null) {
                throw new EntityNotFoundException(String.format("Review with id %s could not be found", id));
            }

            em.getTransaction().begin();

            if (review.getRating() != null) {
                foundReview.setRating(review.getRating());

                Game foundGame = em.find(Game.class, review.getGameId());

                if (foundGame == null) {
                    throw new EntityNotFoundException(String.format("Game with id %s could not be found", id));
                }

                TypedQuery<Double> averageQuery = em.createQuery("SELECT AVG(r.rating) FROM Review r WHERE r.gameId = :gameId", Double.class);
                averageQuery.setParameter("gameId", foundGame.getId());

                foundGame.setRating(averageQuery.getSingleResult());
            }
            if (review.getReview() != null) {
                foundReview.setReview(review.getReview());
            }

            em.getTransaction().commit();
            return foundReview;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Review foundReview = em.find(Review.class, id);

            if (foundReview == null) {
                throw new EntityNotFoundException(String.format("Review with id %s could not be found", id));
            }

            em.getTransaction().begin();
            em.remove(foundReview);
            em.getTransaction().commit();
        }
    }
}
