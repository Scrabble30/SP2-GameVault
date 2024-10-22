package app.daos.impl;

import app.daos.IDAO;
import app.entities.Platform;
import jakarta.persistence.*;

import java.util.Set;
import java.util.stream.Collectors;

public class PlatformDAOImpl implements IDAO<Platform, Long> {

    private static PlatformDAOImpl instance;
    private final EntityManagerFactory emf;

    private PlatformDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static PlatformDAOImpl getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new PlatformDAOImpl(emf);
        }

        return instance;
    }

    @Override
    public Platform create(Platform platform) {
        try (EntityManager em = emf.createEntityManager()) {
            Platform foundPlatform = em.find(Platform.class, platform.getId());

            if (foundPlatform != null) {
                throw new EntityExistsException(String.format("Platform with id %d already exists", platform.getId()));
            }

            em.getTransaction().begin();
            em.persist(platform);
            em.getTransaction().commit();
            return platform;
        }
    }

    @Override
    public Platform getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Platform foundPlatform = em.find(Platform.class, id);

            if (foundPlatform == null) {
                throw new EntityNotFoundException(String.format("Platform with id %d could not be found", id));
            }

            return foundPlatform;
        }
    }

    @Override
    public Set<Platform> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Platform> query = em.createQuery("select p from Platform p", Platform.class);
            return query.getResultStream().collect(Collectors.toSet());
        }
    }

    @Override
    public Platform update(Long id, Platform platform) {
        try (EntityManager em = emf.createEntityManager()) {
            Platform foundPlatform = em.find(Platform.class, id);

            if (foundPlatform == null) {
                throw new EntityNotFoundException(String.format("Platform with id %d could not be found", id));
            }

            em.getTransaction().begin();

            if (platform.getName() != null) {
                foundPlatform.setName(platform.getName());
            }

            em.getTransaction().commit();
            return foundPlatform;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Platform foundPlatform = em.find(Platform.class, id);

            if (foundPlatform == null) {
                throw new EntityNotFoundException(String.format("Platform with id %d could not be found", id));
            }

            em.getTransaction().begin();
            em.remove(foundPlatform);
            em.getTransaction().commit();
        }
    }
}
