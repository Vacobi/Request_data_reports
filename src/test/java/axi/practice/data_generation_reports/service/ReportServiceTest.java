package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dao.ReportRowDao;
import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.filter.RequestFilterDto;
import axi.practice.data_generation_reports.dto.report.CreateReportRequestDto;
import axi.practice.data_generation_reports.dto.report.ReportDto;
import axi.practice.data_generation_reports.dto.report_row.ReportRowDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.ReportRow;
import axi.practice.data_generation_reports.entity.RequestFilter;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import axi.practice.data_generation_reports.mapper.ReportMapper;
import axi.practice.data_generation_reports.util.ClearableTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static axi.practice.data_generation_reports.util.ReportAsserts.assertReportDtoEquals;
import static axi.practice.data_generation_reports.util.ReportAsserts.assertReportEquals;
import static axi.practice.data_generation_reports.util.TestAsserts.assertBothNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class ReportServiceTest extends ClearableTest {

    @Autowired
    private int dataPageSize;

    @Autowired
    private ReportService reportService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ReportDao reportDao;
    @Autowired
    private ReportRowDao reportRowDao;

    @Autowired
    private ReportMapper reportMapper;

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
    void getReportWithSeveralRows() {

        int rowsCount = 10;
        CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder().build();
        CreateReportRequestDto requestDto = CreateReportRequestDto.builder()
                .filter(requestFilterRequestDto)
                .build();

        List<CreateRequestDto> createRequests = createRequestDtos(rowsCount);
        createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));


        long countReportsBeforeRequest = reportDao.count();
        long countReportRowsBeforeRequest = reportRowDao.count();

        CompletableFuture<ReportDto> actualFuture = reportService.generateReport(requestDto);
        ReportDto actualDto = actualFuture.join();

        long countReportsAfterRequest = reportDao.count();
        long countReportRowsAfterRequest = reportRowDao.count();

        Optional<Report> optionalActualReport = reportDao.findByIdWithRows(actualDto.getId());
        Report actualReport = optionalActualReport.get();

        List<ReportRowDto> expectedReportRowsDto = new LinkedList<>();
        for (int i = 0; i < rowsCount; i++) {
            ReportRowDto currentRow = ReportRowDto.builder()
                    .id(actualDto.getRows().get(i).getId())
                    .rowUUID(actualDto.getRows().get(i).getRowUUID())
                    .host(createRequests.get(i).getUrl())
                    .path(createRequests.get(i).getPath())
                    .avgHeaders(0.0)
                    .avgQueryParams(0.0)
                    .build();

            expectedReportRowsDto.add(currentRow);
        }
        RequestFilterDto expectedFilterDto = RequestFilterDto.builder()
                .id(actualDto.getFilter().getId())
                .build();
        ReportDto expectedReportDto = ReportDto.builder()
                .id(actualDto.getId())
                .filter(expectedFilterDto)
                .status(ReportStatus.COMPLETED)
                .createdAt(actualDto.getCreatedAt())
                .finishedAt(actualDto.getFinishedAt())
                .rows(expectedReportRowsDto)
                .build();

        List<ReportRow> expectedReportRows = new LinkedList<>();
        for (int i = 0; i < rowsCount; i++) {
            ReportRow currentRow = ReportRow.builder()
                    .id(actualDto.getRows().get(i).getId())
                    .rowUUID(actualDto.getRows().get(i).getRowUUID())
                    .host(createRequests.get(i).getUrl())
                    .path(createRequests.get(i).getPath())
                    .avgHeaders(0.0)
                    .avgQueryParams(0.0)
                    .report(reportMapper.toReport(actualDto))
                    .build();

            expectedReportRows.add(currentRow);
        }
        RequestFilter expectedFilter = RequestFilter.builder()
                .id(actualDto.getFilter().getId())
                .build();
        Report expectedReport = Report.builder()
                .id(actualDto.getId())
                .filter(expectedFilter)
                .status(ReportStatus.COMPLETED)
                .createdAt(actualDto.getCreatedAt())
                .finishedAt(actualDto.getFinishedAt())
                .rows(expectedReportRows)
                .build();


        assertReportDtoEquals(expectedReportDto, actualDto);
        assertBothNotNull(actualDto.getCreatedAt(), actualDto.getFinishedAt());
        assertReportEquals(expectedReport, actualReport);
        assertBothNotNull(actualReport.getCreatedAt(), actualReport.getFinishedAt());
        assertEquals(countReportsBeforeRequest + 1, countReportsAfterRequest);
        assertEquals(countReportRowsBeforeRequest + rowsCount, countReportRowsAfterRequest);
    }

    @Test
    void reportFailed() {

        int rowsCount = 1;
        CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder()
                .fromDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .toDate(LocalDateTime.of(2020, 1, 1, 0, 0))
                .build();
        CreateReportRequestDto requestDto = CreateReportRequestDto.builder()
                .filter(requestFilterRequestDto)
                .build();

        List<CreateRequestDto> createRequests = createRequestDtos(rowsCount);
        createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));


        long countReportsBeforeRequest = reportDao.count();
        long countReportRowsBeforeRequest = reportRowDao.count();

        CompletableFuture<ReportDto> actualFuture = reportService.generateReport(requestDto);
        assertThrows(CompletionException.class, actualFuture::join);

        long countReportsAfterRequest = reportDao.count();
        long countReportRowsAfterRequest = reportRowDao.count();

        Optional<Report> optionalActualReport = reportDao.findLastWithRows();
        Report actualReport = optionalActualReport.get();


        Report expectedReport = Report.builder()
                .id(actualReport.getId())
                .status(ReportStatus.FAILED)
                .createdAt(actualReport.getCreatedAt())
                .finishedAt(actualReport.getFinishedAt())
                .build();


        assertEquals(countReportsBeforeRequest + 1, countReportsAfterRequest);
        assertEquals(countReportRowsBeforeRequest, countReportRowsAfterRequest);
        assertReportEquals(expectedReport, actualReport);
        assertBothNotNull(actualReport.getCreatedAt(), actualReport.getFinishedAt());
    }

    @Test
    void getReportWithSeveralPages() {

        int rowsCount = (int) (dataPageSize * 1.5);
        CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder().build();
        CreateReportRequestDto requestDto = CreateReportRequestDto.builder()
                .filter(requestFilterRequestDto)
                .build();

        List<CreateRequestDto> createRequests = createRequestDtos(rowsCount);
        createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));


        long countReportsBeforeRequest = reportDao.count();
        long countReportRowsBeforeRequest = reportRowDao.count();

        CompletableFuture<ReportDto> actualFuture = reportService.generateReport(requestDto);
        actualFuture.join();

        long countReportsAfterRequest = reportDao.count();
        long countReportRowsAfterRequest = reportRowDao.count();


        assertEquals(countReportsBeforeRequest + 1, countReportsAfterRequest);
        assertEquals(countReportRowsBeforeRequest + rowsCount, countReportRowsAfterRequest);
    }
}