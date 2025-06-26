package axi.practice.data_generation_reports.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainersConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("adddict_testdb")
                    .withUsername("user")
                    .withPassword("password");

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        postgresContainer.start();

        String rawUrl = postgresContainer.getJdbcUrl();
        String log4jdbcUrl = rawUrl.replace("jdbc:", "jdbc:log4jdbc:"); // ✅ if using log4jdbc

        TestPropertyValues.of(
                "spring.datasource.url=" + log4jdbcUrl,
                "spring.datasource.driver-class-name=net.sf.log4jdbc.sql.jdbcapi.DriverSpy",
                "spring.datasource.username=" + postgresContainer.getUsername(),
                "spring.datasource.password=" + postgresContainer.getPassword(),
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect"
        ).applyTo(applicationContext.getEnvironment());
    }
}