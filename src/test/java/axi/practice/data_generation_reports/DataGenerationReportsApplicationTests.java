package axi.practice.data_generation_reports;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class DataGenerationReportsApplicationTests {

	@Test
	void contextLoads() {
	}

}
