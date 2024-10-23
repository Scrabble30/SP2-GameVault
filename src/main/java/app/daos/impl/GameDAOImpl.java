package app.daos.impl;

import app.daos.AbstractDAO;
import app.entities.Game;
import app.entities.Genre;
import app.entities.Platform;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

public class GameDAOImpl extends AbstractDAO<Game, Long> {
    private static GameDAOImpl instance;

    private GameDAOImpl(EntityManagerFactory emf) {
        super(emf, Game.class);
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

            if (game.getGenreSet() != null) {
                Set<Genre> genreSet = new HashSet<>();

                game.getGenreSet().forEach(genre -> {
                    Genre foundGenre = em.find(Genre.class, genre.getId());

                    if (foundGenre != null) {
                        genreSet.add(foundGenre);
                    } else {
                        throw new EntityNotFoundException(String.format("Genre with id %d does not exist.", genre.getId()));
                    }
                });

                game.setGenreSet(genreSet);
            }

            if (game.getPlatformSet() != null) {
                Set<Platform> platformSet = new HashSet<>();

                game.getPlatformSet().forEach(platform -> {
                    Platform foundPlatform = em.find(Platform.class, platform.getId());

                    if (foundPlatform != null) {
                        platformSet.add(foundPlatform);
                    } else {
                        throw new EntityNotFoundException(String.format("Platform with id %d does not exist.", platform.getId()));
                    }
                });

                game.setPlatformSet(platformSet);
            }

            em.getTransaction().begin();
            em.persist(game);
            em.getTransaction().commit();

            return game;
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
}
