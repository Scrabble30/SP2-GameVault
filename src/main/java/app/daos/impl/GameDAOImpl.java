package app.daos.impl;

import app.daos.IDAO;
import app.entities.Game;
import jakarta.persistence.*;

import java.util.Set;
import java.util.stream.Collectors;

public class GameDAOImpl implements IDAO<Game, Long> {
    private static GameDAOImpl instance;
    private final EntityManagerFactory emf;

    private GameDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static GameDAOImpl getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new GameDAOImpl(emf);
        }

        return instance;
    }

    @Override
    public Game create(Game game) {
        try (EntityManager em = emf.createEntityManager()) {
            Game foundGame = em.find(Game.class, game.getId());

            if (foundGame != null) {
                throw new EntityExistsException(String.format("Game with id %d already exists", game.getId()));
            }

            em.getTransaction().begin();
            em.persist(game);
            em.getTransaction().commit();

            return game;
        }
    }

    @Override
    public Game getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Game foundGame = em.find(Game.class, id);

            if (foundGame == null) {
                throw new EntityNotFoundException(String.format("Game with id %d could not be found", id));
            }

            return foundGame;
        }
    }

    @Override
    public Set<Game> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Game> query = em.createNamedQuery("Game.getAll", Game.class);

            return query.getResultStream().collect(Collectors.toSet());
        }
    }

    @Override
    public Game update(Long id, Game game) {
        try (EntityManager em = emf.createEntityManager()) {
            Game foundGame = em.find(Game.class, id);

            if (foundGame == null) {
                throw new EntityNotFoundException(String.format("Game with id %d does not exist", id));
            }

            em.getTransaction().begin();

            if (game.getTitle() != null)
                foundGame.setTitle(game.getTitle());
            if (game.getReleaseDate() != null)
                foundGame.setReleaseDate(game.getReleaseDate());
            if (game.getBackgroundImageURL() != null)
                foundGame.setBackgroundImageURL(game.getBackgroundImageURL());
            if (game.getMetaCriticScore() != null)
                foundGame.setMetaCriticScore(game.getMetaCriticScore());
            if (game.getPlaytime() != null)
                foundGame.setPlaytime(game.getPlaytime());
            if (game.getDescription() != null)
                foundGame.setDescription(game.getDescription());
            if (game.getPlatformSet() != null)
                foundGame.setPlatformSet(game.getPlatformSet());
            if (game.getGenreSet() != null)
                foundGame.setGenreSet(game.getGenreSet());

            em.getTransaction().commit();

            return foundGame;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Game foundGame = em.find(Game.class, id);

            if (foundGame == null) {
                throw new EntityNotFoundException(String.format("Game with id %d does not exist", id));
            }

            em.getTransaction().begin();
            em.remove(foundGame);
            em.getTransaction().commit();
        }
    }
}
