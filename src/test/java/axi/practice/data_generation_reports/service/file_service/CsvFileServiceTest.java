package axi.practice.data_generation_reports.service.file_service;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.entity.enums.MimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class CsvFileServiceTest extends AbstractFileServiceTest {

    @Autowired
    public CsvFileServiceTest(CsvFileService csvFileService) {
        super(csvFileService);
    }

    @Override
    protected MimeType getMimeType() {
        return MimeType.CSV;
    }
}