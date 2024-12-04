package com.example.lab4;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    @Getter
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create ServiceRegistry from hibernate.cfg.xml
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml") // Load configuration from hibernate.cfg.xml
                    .build();

            // Create MetadataSources, which are used to generate the SessionFactory
            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();

            // Build the SessionFactory from the metadata
            sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch (Throwable ex) {
            // Log the exception and rethrow as an initializer error
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
