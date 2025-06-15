package axi.practice.data_generation_reports.util;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
public class ClearableTest extends TestContainersConfig {
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void setUp() {
        clearTables();
    }

    private void clearTables() {
        try (var conn = dataSource.getConnection()) {
            conn.createStatement().execute("""
                        DO $$ DECLARE
                            r RECORD;
                        BEGIN
                            FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
                                EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' RESTART IDENTITY CASCADE';
                            END LOOP;
                        END $$;
                    """.trim());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}