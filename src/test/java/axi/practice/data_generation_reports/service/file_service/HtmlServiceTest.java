package axi.practice.data_generation_reports.service.file_service;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.entity.enums.MimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class HtmlServiceTest extends AbstractFileServiceTest {

    @Autowired
    public HtmlServiceTest(HtmlService htmlFileService) {
        super(htmlFileService);
    }

    @Override
    protected MimeType getMimeType() {
        return MimeType.HTML;
    }
}