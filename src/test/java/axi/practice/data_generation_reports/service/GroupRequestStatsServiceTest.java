package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dao.GroupRequestStatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class GroupRequestStatsServiceTest {

    @Autowired
    private GroupRequestStatsService groupRequestStatsService;

    @Autowired
    private GroupRequestStatDao groupRequestStatDao;



}