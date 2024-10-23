package app.daos.impl;

import app.daos.AbstractDAO;
import app.entities.Platform;
import jakarta.persistence.*;

public class PlatformDAOImpl extends AbstractDAO<Platform, Long> {

    private static PlatformDAOImpl instance;

    private PlatformDAOImpl(EntityManagerFactory emf) {
        super(emf, Platform.class);
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
}
