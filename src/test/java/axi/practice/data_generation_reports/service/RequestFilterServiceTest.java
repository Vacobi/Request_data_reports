package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dao.RequestFilterDao;
import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.filter.RequestFilterDto;
import axi.practice.data_generation_reports.entity.RequestFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.Optional;

import static axi.practice.data_generation_reports.util.RequestFilterTestAsserts.assertRequestFilterDtoEquals;
import static axi.practice.data_generation_reports.util.RequestFilterTestAsserts.assertRequestFilterEquals;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class RequestFilterServiceTest {

    @Autowired
    private RequestFilterDao requestFilterDao;

    @Autowired
    private RequestFilterService requestFilterService;

    @Test
    void createRequestFilter() {
        final LocalDateTime fromDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        final LocalDateTime toDate = LocalDateTime.of(2021, 1, 1, 0, 0);
        final String host = "google.com";
        final String path = "search";
        final Double avgHeaders = 2.0;
        final Double avgQueryParams = 3.0;

        CreateRequestFilterRequestDto createRequestFilterRequestDto = CreateRequestFilterRequestDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .host(host)
                .path(path)
                .avgHeaders(avgHeaders)
                .avgQueryParams(avgQueryParams)
                .build();


        RequestFilterDto actualDto = requestFilterService.createFilter(createRequestFilterRequestDto);
        Optional<RequestFilter> optionalActualPersisted = requestFilterDao.findById(actualDto.getId());


        RequestFilterDto expectedDto = RequestFilterDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .host(host)
                .path(path)
                .avgHeaders(avgHeaders)
                .avgQueryParams(avgQueryParams)
                .build();

        RequestFilter expectedPersisted = RequestFilter.builder()
                .id(actualDto.getId())
                .fromDate(fromDate)
                .toDate(toDate)
                .host(host)
                .path(path)
                .avgHeaders(avgHeaders)
                .avgQueryParams(avgQueryParams)
                .build();


        assertTrue(optionalActualPersisted.isPresent());
        RequestFilter actualPersisted = optionalActualPersisted.get();
        assertRequestFilterDtoEquals(expectedDto, actualDto);
        assertRequestFilterEquals(expectedPersisted, actualPersisted);
    }

    @Test
    void createEmptyRequestFilter() {
        final LocalDateTime fromDate = null;
        final LocalDateTime toDate = null;
        final String host = null;
        final String path = null;
        final Double avgHeaders = null;
        final Double avgQueryParams = null;

        CreateRequestFilterRequestDto createRequestFilterRequestDto = CreateRequestFilterRequestDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .host(host)
                .path(path)
                .avgHeaders(avgHeaders)
                .avgQueryParams(avgQueryParams)
                .build();


        RequestFilterDto actualDto = requestFilterService.createFilter(createRequestFilterRequestDto);
        Optional<RequestFilter> optionalActualPersisted = requestFilterDao.findById(actualDto.getId());


        RequestFilterDto expectedDto = RequestFilterDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .host(host)
                .path(path)
                .avgHeaders(avgHeaders)
                .avgQueryParams(avgQueryParams)
                .build();

        RequestFilter expectedPersisted = RequestFilter.builder()
                .id(actualDto.getId())
                .fromDate(fromDate)
                .toDate(toDate)
                .host(host)
                .path(path)
                .avgHeaders(avgHeaders)
                .avgQueryParams(avgQueryParams)
                .build();


        assertTrue(optionalActualPersisted.isPresent());
        RequestFilter actualPersisted = optionalActualPersisted.get();
        assertRequestFilterDtoEquals(expectedDto, actualDto);
        assertRequestFilterEquals(expectedPersisted, actualPersisted);
    }
}