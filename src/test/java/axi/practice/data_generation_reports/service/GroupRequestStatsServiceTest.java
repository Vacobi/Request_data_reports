package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dao.RequestDao;
import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GetGroupRequestStatsDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GroupRequestStatDto;
import axi.practice.data_generation_reports.dto.header.CreateHeaderRequestDto;
import axi.practice.data_generation_reports.dto.queryparam.CreateQueryParamRequestDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.dto.request.RequestDto;
import axi.practice.data_generation_reports.entity.Request;
import axi.practice.data_generation_reports.util.ClearableTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static axi.practice.data_generation_reports.util.GroupRequestStatAsserts.assertGroupRequestStatsDtoEquals;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class GroupRequestStatsServiceTest extends ClearableTest {

    @Autowired
    private GroupRequestStatsService groupRequestStatsService;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private RequestService requestService;

    @Autowired
    private int dataPageSize;

    private Long currentHeader = 0L;
    private List<CreateHeaderRequestDto> createHeaderRequests(int count) {
        List<CreateHeaderRequestDto> createHeaderRequests = new LinkedList<>();

        for (int i = 0; i < count; i++) {
            CreateHeaderRequestDto createHeaderRequest = CreateHeaderRequestDto.builder()
                    .value("value" + currentHeader)
                    .name("name" + currentHeader)
                    .build();
            createHeaderRequests.add(createHeaderRequest);

            currentHeader++;
        }

        return createHeaderRequests;
    }

    private Long currentQueryParam = 0L;
    private List<CreateQueryParamRequestDto> createQueryParamRequests(int count) {
        List<CreateQueryParamRequestDto> createQueryParamRequests = new LinkedList<>();

        for (int i = 0; i < count; i++) {
            CreateQueryParamRequestDto createHeaderRequest = CreateQueryParamRequestDto.builder()
                    .value("value" + currentQueryParam)
                    .name("name" + currentQueryParam)
                    .build();
            createQueryParamRequests.add(createHeaderRequest);

            currentQueryParam++;
        }

        return createQueryParamRequests;
    }

    @Test
    void getGroupRequestStatsWithAllFilters() {

        String host = "google.com";
        String path = "search";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        requestService.create(requestDto);

        double avgHeaders = 0.0;
        double avgQueryParams = 0.0;
        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .fromDate(LocalDateTime.of(2020, 1, 1, 0, 0))
                .toDate(LocalDateTime.of(2030, 1, 1, 0, 0))
                .host(host)
                .path(path)
                .avgHeaders(avgHeaders)
                .avgQueryParams(avgQueryParams)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(host)
                .path(path)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void getGroupRequestStatsWithEmptyFilters() {

        String host = "google.com";
        String path = "search";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        requestService.create(requestDto);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();

        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);

        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(host)
                .path(path)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void fewRecordsWithSameHostAndPath() {

        String host = "google.com";
        String path = "search";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        requestService.create(requestDto);
        requestService.create(requestDto);
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

        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(host)
                .path(path)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void filterByHost() {

        String matchedHost = "axilink.com";
        String matchedPath = "finder";
        CreateRequestDto matchRequestDto = CreateRequestDto.builder()
                .url(matchedHost)
                .path(matchedPath)
                .build();

        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url("newhost")
                .path("newpath")
                .build();

        requestService.create(matchRequestDto);
        requestService.create(requestDto);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .host(matchedHost)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(matchedHost)
                .path(matchedPath)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void filterByPath() {

        String matchedHost = "axilink.com";
        String matchedPath = "finder";
        CreateRequestDto matchRequestDto = CreateRequestDto.builder()
                .url(matchedHost)
                .path(matchedPath)
                .build();

        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url("newhost")
                .path("newpath")
                .build();

        requestService.create(matchRequestDto);
        requestService.create(requestDto);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .path(matchedPath)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(matchedHost)
                .path(matchedPath)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void filterByAvgHeaders() {

        String host = "google.com";
        String path = "search";
        int headersCount1 = 5;
        List<CreateHeaderRequestDto> headers = createHeaderRequests(headersCount1);
        CreateRequestDto requestDto1 = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .headers(headers)
                .build();
        int headersCount2 = 0;
        CreateRequestDto requestDto2 = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        List<CreateHeaderRequestDto> otherHeaders = createHeaderRequests(50);
        CreateRequestDto otherRequestDto = CreateRequestDto.builder()
                .url("otherhost")
                .path("otherpath")
                .headers(otherHeaders)
                .build();

        requestService.create(requestDto1);
        requestService.create(requestDto2);
        requestService.create(otherRequestDto);

        double avgHeaders = (double) (headersCount1 + headersCount2) / 2;
        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .avgHeaders(avgHeaders)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = (double) (headersCount1 + headersCount2) / 2;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(host)
                .path(path)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void filterByAvgQueryParams() {

        String host = "google.com";
        String path = "search";
        int queryParamsCount1 = 5;
        List<CreateQueryParamRequestDto> queryParams = createQueryParamRequests(queryParamsCount1);
        CreateRequestDto requestDto1 = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .variableParams(queryParams)
                .build();
        int queryParamsCount2 = 0;
        CreateRequestDto requestDto2 = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        List<CreateQueryParamRequestDto> otherQueryParams = createQueryParamRequests(50);
        CreateRequestDto otherRequestDto = CreateRequestDto.builder()
                .url("otherhost")
                .path("otherpath")
                .variableParams(otherQueryParams)
                .build();

        requestService.create(requestDto1);
        requestService.create(requestDto2);
        requestService.create(otherRequestDto);

        double avgQueryParams = (double) (queryParamsCount1 + queryParamsCount2) / 2;
        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .avgQueryParams(avgQueryParams)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = (double) (queryParamsCount1 + queryParamsCount2) / 2;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(host)
                .path(path)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void filterByFromDateSameWithEntity() {

        String host = "google.com";
        String path = "search";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        RequestDto persistedRequest = requestService.create(requestDto);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .fromDate(persistedRequest.getTimestamp())
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(host)
                .path(path)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void filterByFromDateIsAfterPersistedEntity() {

        String host = "google.com";
        String path = "search";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        RequestDto persistedRequest = requestService.create(requestDto);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .fromDate(persistedRequest.getTimestamp().plusSeconds(1))
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(0, actualPage.getTotalElements());
    }

    @Test
    void filterByDateIsBeforePersistedEntity() {

        String host = "google.com";
        String path = "search";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        RequestDto persistedRequest = requestService.create(requestDto);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .fromDate(persistedRequest.getTimestamp().minusSeconds(1))
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(host)
                .path(path)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void filterByToDateSameWithEntity() {

        String host = "google.com";
        String path = "search";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        RequestDto persistedRequest = requestService.create(requestDto);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .toDate(persistedRequest.getTimestamp())
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(host)
                .path(path)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void filterByToDateAfterPersistedEntity() {

        String host = "google.com";
        String path = "search";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        RequestDto persistedRequest = requestService.create(requestDto);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .toDate(persistedRequest.getTimestamp().plusSeconds(1))
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(host)
                .path(path)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void filterByToDateBeforePersistedEntity() {

        String host = "google.com";
        String path = "search";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .path(path)
                .build();

        RequestDto persistedRequest = requestService.create(requestDto);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .toDate(persistedRequest.getTimestamp().minusSeconds(1))
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(0, actualPage.getTotalElements());
    }

    @Test
    void pathInPersistedInNullInFilterNotNull() {

        String host = "google.com";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .build();

        requestService.create(requestDto);

        double avgHeaders = 0.0;
        double avgQueryParams = 0.0;
        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .fromDate(LocalDateTime.of(2020, 1, 1, 0, 0))
                .toDate(LocalDateTime.of(2030, 1, 1, 0, 0))
                .host(host)
                .path("somepath")
                .avgHeaders(avgHeaders)
                .avgQueryParams(avgQueryParams)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(0, actualPage.getTotalElements());
    }

    @Test
    void pathInPersistedAndFilterIsNull() {

        String host = "google.com";
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .url(host)
                .build();

        requestService.create(requestDto);

        double avgHeaders = 0.0;
        double avgQueryParams = 0.0;
        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .fromDate(LocalDateTime.of(2020, 1, 1, 0, 0))
                .toDate(LocalDateTime.of(2030, 1, 1, 0, 0))
                .host(host)
                .avgHeaders(avgHeaders)
                .avgQueryParams(avgQueryParams)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders = 0.0;
        double expectedAvgQueryParams = 0.0;
        GroupRequestStatDto actualDto = actualPage.get().iterator().next();
        GroupRequestStatDto expectedDto = GroupRequestStatDto.builder()
                .id(actualDto.getId())
                .host(host)
                .path(null)
                .avgHeaders(expectedAvgHeaders)
                .avgQueryParams(expectedAvgQueryParams)
                .build();

        assertGroupRequestStatsDtoEquals(expectedDto, actualDto);
    }

    @Test
    void getFewEntities() {

        String host1 = "google.com";
        String path1 = "search";
        CreateRequestDto requestDto1 = CreateRequestDto.builder()
                .url(host1)
                .path(path1)
                .build();
        String host2 = "axihost";
        String path2 = "axipath";
        CreateRequestDto requestDto2 = CreateRequestDto.builder()
                .url(host2)
                .path(path2)
                .build();

        requestService.create(requestDto1);
        requestService.create(requestDto2);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder().build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(2, actualPage.getTotalElements());

        double expectedAvgHeaders1 = 0.0;
        double expectedAvgQueryParams1 = 0.0;
        GroupRequestStatDto actualDto1 = actualPage.getContent().stream()
                .filter(dto -> host1.equals(dto.getHost()))
                .findFirst().get();
        GroupRequestStatDto expectedDto1 = GroupRequestStatDto.builder()
                .id(actualDto1.getId())
                .host(host1)
                .path(path1)
                .avgHeaders(expectedAvgHeaders1)
                .avgQueryParams(expectedAvgQueryParams1)
                .build();
        assertGroupRequestStatsDtoEquals(expectedDto1, actualDto1);

        double expectedAvgHeaders2 = 0.0;
        double expectedAvgQueryParams2 = 0.0;
        GroupRequestStatDto actualDto2 = actualPage.getContent().stream()
                .filter(dto -> host2.equals(dto.getHost()))
                .findFirst().get();
        GroupRequestStatDto expectedDto2 = GroupRequestStatDto.builder()
                .id(actualDto2.getId())
                .host(host2)
                .path(path2)
                .avgHeaders(expectedAvgHeaders2)
                .avgQueryParams(expectedAvgQueryParams2)
                .build();
        assertGroupRequestStatsDtoEquals(expectedDto2, actualDto2);
    }

    @Test
    void recordsHaveSameHostAndDifferentPathsAndFilterByHost() {
        String host1 = "google.com";
        String path1 = "search";
        CreateRequestDto requestDto1 = CreateRequestDto.builder()
                .url(host1)
                .path(path1)
                .build();
        String host2 = "google.com";
        String path2 = "axipath";
        CreateRequestDto requestDto2 = CreateRequestDto.builder()
                .url(host2)
                .path(path2)
                .build();

        requestService.create(requestDto1);
        requestService.create(requestDto2);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .host(host1)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(2, actualPage.getTotalElements());

        double expectedAvgHeaders1 = 0.0;
        double expectedAvgQueryParams1 = 0.0;
        GroupRequestStatDto actualDto1 = actualPage.getContent().stream()
                .filter(dto -> host1.equals(dto.getHost()) && path1.equals(dto.getPath()))
                .findFirst().get();
        GroupRequestStatDto expectedDto1 = GroupRequestStatDto.builder()
                .id(actualDto1.getId())
                .host(host1)
                .path(path1)
                .avgHeaders(expectedAvgHeaders1)
                .avgQueryParams(expectedAvgQueryParams1)
                .build();
        assertGroupRequestStatsDtoEquals(expectedDto1, actualDto1);

        double expectedAvgHeaders2 = 0.0;
        double expectedAvgQueryParams2 = 0.0;
        GroupRequestStatDto actualDto2 = actualPage.getContent().stream()
                .filter(dto -> host2.equals(dto.getHost()) && path2.equals(dto.getPath()))
                .findFirst().get();
        GroupRequestStatDto expectedDto2 = GroupRequestStatDto.builder()
                .id(actualDto2.getId())
                .host(host2)
                .path(path2)
                .avgHeaders(expectedAvgHeaders2)
                .avgQueryParams(expectedAvgQueryParams2)
                .build();
        assertGroupRequestStatsDtoEquals(expectedDto2, actualDto2);
    }

    @Test
    void recordsHaveSameHostAndDifferentHostsAndFilterByPath() {
        String host1 = "google.com";
        String path1 = "search";
        CreateRequestDto requestDto1 = CreateRequestDto.builder()
                .url(host1)
                .path(path1)
                .build();
        String host2 = "google.com";
        String path2 = "axipath";
        CreateRequestDto requestDto2 = CreateRequestDto.builder()
                .url(host2)
                .path(path2)
                .build();

        requestService.create(requestDto1);
        requestService.create(requestDto2);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .path(path1)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders1 = 0.0;
        double expectedAvgQueryParams1 = 0.0;
        GroupRequestStatDto actualDto1 = actualPage.getContent().stream()
                .filter(dto -> host1.equals(dto.getHost()) && path1.equals(dto.getPath()))
                .findFirst().get();
        GroupRequestStatDto expectedDto1 = GroupRequestStatDto.builder()
                .id(actualDto1.getId())
                .host(host1)
                .path(path1)
                .avgHeaders(expectedAvgHeaders1)
                .avgQueryParams(expectedAvgQueryParams1)
                .build();
        assertGroupRequestStatsDtoEquals(expectedDto1, actualDto1);
    }

    @Test
    void getFewEntitiesWithFewRecords() {

        String host1 = "google.com";
        String path1 = "search";
        CreateRequestDto requestDto1 = CreateRequestDto.builder()
                .url(host1)
                .path(path1)
                .build();
        String host2 = "axihost";
        String path2 = "axipath";
        CreateRequestDto requestDto2 = CreateRequestDto.builder()
                .url(host2)
                .path(path2)
                .build();

        requestService.create(requestDto1);
        requestService.create(requestDto2);
        requestService.create(requestDto1);
        requestService.create(requestDto2);
        requestService.create(requestDto1);
        requestService.create(requestDto2);
        requestService.create(requestDto1);
        requestService.create(requestDto2);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder().build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(2, actualPage.getTotalElements());

        double expectedAvgHeaders1 = 0.0;
        double expectedAvgQueryParams1 = 0.0;
        GroupRequestStatDto actualDto1 = actualPage.getContent().stream()
                .filter(dto -> host1.equals(dto.getHost()))
                .findFirst().get();
        GroupRequestStatDto expectedDto1 = GroupRequestStatDto.builder()
                .id(actualDto1.getId())
                .host(host1)
                .path(path1)
                .avgHeaders(expectedAvgHeaders1)
                .avgQueryParams(expectedAvgQueryParams1)
                .build();
        assertGroupRequestStatsDtoEquals(expectedDto1, actualDto1);

        double expectedAvgHeaders2 = 0.0;
        double expectedAvgQueryParams2 = 0.0;
        GroupRequestStatDto actualDto2 = actualPage.getContent().stream()
                .filter(dto -> host2.equals(dto.getHost()))
                .findFirst().get();
        GroupRequestStatDto expectedDto2 = GroupRequestStatDto.builder()
                .id(actualDto2.getId())
                .host(host2)
                .path(path2)
                .avgHeaders(expectedAvgHeaders2)
                .avgQueryParams(expectedAvgQueryParams2)
                .build();
        assertGroupRequestStatsDtoEquals(expectedDto2, actualDto2);
    }

    @Test
    void fewDifferentRecordsAndFilter() {

        String host1 = "google.com";
        String path1 = "search";
        CreateRequestDto requestDto1 = CreateRequestDto.builder()
                .url(host1)
                .path(path1)
                .build();
        String host2 = "axihost";
        String path2 = "axipath";
        CreateRequestDto requestDto2 = CreateRequestDto.builder()
                .url(host2)
                .path(path2)
                .build();

        requestService.create(requestDto1);
        requestService.create(requestDto2);
        requestService.create(requestDto1);
        requestService.create(requestDto2);
        requestService.create(requestDto1);
        requestService.create(requestDto2);
        requestService.create(requestDto1);
        requestService.create(requestDto2);

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .path(path1)
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();


        Page<GroupRequestStatDto> actualPage = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);


        assertEquals(1, actualPage.getTotalElements());

        double expectedAvgHeaders1 = 0.0;
        double expectedAvgQueryParams1 = 0.0;
        GroupRequestStatDto actualDto1 = actualPage.getContent().stream()
                .filter(dto -> host1.equals(dto.getHost()))
                .findFirst().get();
        GroupRequestStatDto expectedDto1 = GroupRequestStatDto.builder()
                .id(actualDto1.getId())
                .host(host1)
                .path(path1)
                .avgHeaders(expectedAvgHeaders1)
                .avgQueryParams(expectedAvgQueryParams1)
                .build();
        assertGroupRequestStatsDtoEquals(expectedDto1, actualDto1);
    }


    private Long currentRequest = 0L;
    private List<CreateRequestDto> createRequestDtos(int count) {

        List<CreateRequestDto> createRequests = new LinkedList<>();

        for (int i = 0; i < count; i++) {
            CreateRequestDto requestDto = CreateRequestDto.builder()
                    .url("google.com" + currentRequest)
                    .path("search" + currentRequest)
                    .build();
            createRequests.add(requestDto);

            currentRequest++;
        }

        return createRequests;
    }

    @Test
    void fewPages() {

        int count = (int) (dataPageSize * 1.5);
        List<CreateRequestDto> createRequests = createRequestDtos(count);

        createRequests.forEach(reqDto -> requestService.create(reqDto));

        CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder()
                .build();

        GetGroupRequestStatsDto getGroupRequestStatsDto1 = GetGroupRequestStatsDto.builder()
                .page(0)
                .requestFilter(filter)
                .build();
        Page<GroupRequestStatDto> actualPage1 = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto1);
        GetGroupRequestStatsDto getGroupRequestStatsDto2 = GetGroupRequestStatsDto.builder()
                .page(1)
                .requestFilter(filter)
                .build();
        Page<GroupRequestStatDto> actualPage2 = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto2);

        assertEquals(count, actualPage1.getTotalElements());
        assertEquals(2, actualPage1.getTotalPages());
        assertEquals(dataPageSize, actualPage1.getNumberOfElements());
        assertEquals(count - dataPageSize, actualPage2.getNumberOfElements());
    }
}
