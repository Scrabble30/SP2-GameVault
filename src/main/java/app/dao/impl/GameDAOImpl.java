package app.dao.impl;

import app.dao.AbstractDAO;
import app.entity.Game;
import app.entity.Genre;
import app.entity.Platform;
import app.filter.GameFilter;
import app.filter.GamePage;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

            if (game.getGenreSet() != null)
                game.setGenreSet(getFoundGenres(game));
            if (game.getPlatformSet() != null)
                game.setPlatformSet(getFoundPlatforms(game));

            em.getTransaction().begin();
            em.persist(game);
            em.getTransaction().commit();

            return game;
        }
    }

    public GamePage getAll(GameFilter gameFilter) {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            Predicate predicate = getPredicate(criteriaBuilder, root, gameFilter);
            criteriaQuery.where(predicate);

            TypedQuery<Game> query = em.createQuery(criteriaQuery);
            query.setFirstResult(gameFilter.getPageNumber() * gameFilter.getPageSize());
            query.setMaxResults(gameFilter.getPageSize());

            long gamesCount = getGamesCount(gameFilter);
            long gamesPageCount = getGamesPageCount(gameFilter.getPageSize(), gamesCount);

            return new GamePage(
                    gameFilter.getPageNumber(),
                    gamesPageCount,
                    gamesCount,
                    query.getResultStream().collect(Collectors.toSet()));
        }
    }

    private long getGamesCount(GameFilter gameFilter) {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<Game> root = criteriaQuery.from(Game.class);

            Predicate predicate = getPredicate(criteriaBuilder, root, gameFilter);
            criteriaQuery.select(criteriaBuilder.count(root)).where(predicate);

            return em.createQuery(criteriaQuery).getSingleResult();
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
            if (game.getGenreSet() != null)
                foundGame.setGenreSet(getFoundGenres(game));
            if (game.getPlatformSet() != null)
                foundGame.setPlatformSet(getFoundPlatforms(game));

            em.getTransaction().commit();

            return foundGame;
        }
    }

    private Set<Genre> getFoundGenres(Game game) {
        try (EntityManager em = emf.createEntityManager()) {
            Set<Genre> foundGenres = new HashSet<>();

            game.getGenreSet().forEach(genre -> {
                Genre foundGenre = em.find(Genre.class, genre.getId());

                if (foundGenre != null) {
                    foundGenres.add(foundGenre);
                } else {
                    throw new EntityNotFoundException(String.format("Genre with id %d does not exist.", genre.getId()));
                }
            });

            return foundGenres;
        }
    }

    private Set<Platform> getFoundPlatforms(Game game) {
        try (EntityManager em = emf.createEntityManager()) {
            Set<Platform> foundPlatforms = new HashSet<>();

            game.getPlatformSet().forEach(platform -> {
                Platform foundPlatform = em.find(Platform.class, platform.getId());

                if (foundPlatform != null) {
                    foundPlatforms.add(foundPlatform);
                } else {
                    throw new EntityNotFoundException(String.format("Platform with id %d does not exist.", platform.getId()));
                }
            });

            return foundPlatforms;
        }
    }

    private Predicate getPredicate(CriteriaBuilder criteriaBuilder, Root<Game> root, GameFilter gameFilter) {
        List<Predicate> predicates = new LinkedList<>();

        if (gameFilter.getTitle() != null)
            predicates.add(criteriaBuilder.like(root.get("title"), String.format("%%%s%%", gameFilter.getTitle())));
        if (gameFilter.getReleaseDateGTE() != null)
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("releaseDate"), gameFilter.getReleaseDateGTE()));
        if (gameFilter.getReleaseDateLTE() != null)
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("releaseDate"), gameFilter.getReleaseDateLTE()));

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    private long getGamesPageCount(Integer pageSize, Long gamesCount) {
        if (pageSize == 0 || gamesCount == 0) {
            return 0;
        }

        return (long) Math.ceil((double) gamesCount / pageSize);
    }
}
