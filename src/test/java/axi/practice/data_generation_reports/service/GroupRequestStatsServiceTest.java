package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dao.GroupRequestStatDao;
import axi.practice.data_generation_reports.dao.RequestDao;
import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GetGroupRequestStatsDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GroupRequestStatDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.entity.Request;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class GroupRequestStatsServiceTest {

    @Autowired
    private GroupRequestStatsService groupRequestStatsService;

    @Autowired
    private GroupRequestStatDao groupRequestStatDao;
    @Autowired
    private RequestDao requestDao;

    @Autowired
    private RequestService requestService;

    @Test
    void getGroupRequestStatsWithAllFilters() {

        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url("google.com")
                .path("search")
                .build();

        requestService.create(requestDto);

        Request request = requestDao.findAll().get(0);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .fromDate(LocalDateTime.of(2020, 1, 1, 0, 0))
                .toDate(LocalDateTime.of(2030, 1, 1, 0, 0))
                .host(request.getUrl())
                .path(request.getPath())
                .avgHeaders(0.0)
                .avgQueryParams(0.0)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();

        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);

        assertTrue(actualPage.getTotalElements() > 0);
    }

    @Test
    void getGroupRequestStatsWithEmptyFilters() {

        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url("google.com")
                .path("search")
                .build();

        requestService.create(requestDto);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();

        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);

        assertTrue(actualPage.getTotalElements() > 0);
    }
}
