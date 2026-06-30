package com.luka.userauth.config;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public abstract class TestContainerDatabaseConfig {

    static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("test_db")
                .withUsername("postgres_test")
                .withPassword("root_test")
                .withReuse(true);

        postgres.start();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry){

        registry.add("spring.datasource.url", postgres::getJdbcUrl);

        registry.add("spring.datasource.username", postgres::getUsername);

        registry.add("spring.datasource.password", postgres::getPassword);

    }


}
