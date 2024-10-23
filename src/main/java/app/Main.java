package app;

import app.config.AppConfig;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("game_vault");
        AppConfig.startServer(7070, emf);
    }
}