package app.dao;

import app.entity.Role;
import app.entity.User;
import app.exception.PasswordValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.Set;

public class SecurityDAO {

    private static SecurityDAO instance;
    private final EntityManagerFactory emf;

    private SecurityDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static SecurityDAO getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new SecurityDAO(emf);
        }

        return instance;
    }

    public User create(String username, String password) {
        try (EntityManager em = emf.createEntityManager()) {
            User foundUser = em.find(User.class, username);

            if (foundUser != null) {
                throw new EntityExistsException(String.format("User with username '%s' already exists.", username));
            }

            Role foundRole = em.find(Role.class, "user");

            if (foundRole == null) {
                foundRole = new Role("user");
                em.persist(foundRole);
            }

            User user = User.builder()
                    .username(username)
                    .password(password)
                    .roles(Set.of(foundRole))
                    .build();

            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
    }

    public User getVerifiedUser(String username, String password) {
        try (EntityManager em = emf.createEntityManager()) {
            User foundUser = em.find(User.class, username);

            if (foundUser == null) {
                throw new EntityNotFoundException(String.format("User with username '%s' does not exist.", username));
            }

            if (!foundUser.verifyPassword(password)) {
                throw new PasswordValidationException("Incorrect password.");
            }

            return foundUser;
        }
    }
}
