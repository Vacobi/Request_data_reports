package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dao.ReportRowDao;
import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.filter.RequestFilterDto;
import axi.practice.data_generation_reports.dto.report.*;
import axi.practice.data_generation_reports.dto.report_row.ReportRowDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.ReportRow;
import axi.practice.data_generation_reports.entity.RequestFilter;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import axi.practice.data_generation_reports.exception.ClientExceptionName;
import axi.practice.data_generation_reports.exception.ReportNotFoundException;
import axi.practice.data_generation_reports.util.ClearableTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

import static axi.practice.data_generation_reports.util.ReportAsserts.*;
import static axi.practice.data_generation_reports.util.TestAsserts.assertBothNotNull;
import static org.awaitility.Awaitility.await;
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

    @Nested
    class generateReport {

        private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

        private final static ExecutorService executors = Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE);


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

            Long actualId = reportService.generateReport(requestDto);
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> reportService.getStatus(actualId) == ReportStatus.COMPLETED);

            long countReportsAfterRequest = reportDao.count();
            long countReportRowsAfterRequest = reportRowDao.count();

            Optional<Report> optionalActualReport = reportDao.findByIdWithFilterAndRows(actualId);
            Report actualReport = optionalActualReport.get();


            List<ReportRow> expectedReportRows = new LinkedList<>();
            for (int i = 0; i < rowsCount; i++) {
                ReportRow currentRow = ReportRow.builder()
                        .id(actualReport.getRows().get(i).getId())
                        .rowUUID(actualReport.getRows().get(i).getRowUUID())
                        .host(createRequests.get(i).getUrl())
                        .path(createRequests.get(i).getPath())
                        .avgHeaders(0.0)
                        .avgQueryParams(0.0)
                        .report(actualReport)
                        .build();

                expectedReportRows.add(currentRow);
            }
            RequestFilter expectedFilter = RequestFilter.builder()
                    .id(actualReport.getFilter().getId())
                    .build();
            Report expectedReport = Report.builder()
                    .id(actualId)
                    .filter(expectedFilter)
                    .status(ReportStatus.COMPLETED)
                    .createdAt(actualReport.getCreatedAt())
                    .finishedAt(actualReport.getFinishedAt())
                    .rows(expectedReportRows)
                    .build();


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

            reportService.generateReport(requestDto);

            long countReportsAfterRequest = reportDao.count();
            long countReportRowsAfterRequest = reportRowDao.count();

            Optional<Report> optionalActualReport = reportDao.findLastWithRows();
            Report processingReport = optionalActualReport.get();
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> reportService.getStatus(processingReport.getId()) == ReportStatus.FAILED);
            Report actualReport = reportDao.findByIdWithFilterAndRows(processingReport.getId()).get();

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

            Long actualId = reportService.generateReport(requestDto);
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> reportService.getStatus(actualId) == ReportStatus.COMPLETED);

            long countReportsAfterRequest = reportDao.count();
            long countReportRowsAfterRequest = reportRowDao.count();


            assertEquals(countReportsBeforeRequest + 1, countReportsAfterRequest);
            assertEquals(countReportRowsBeforeRequest + rowsCount, countReportRowsAfterRequest);
        }

        @Test
        void reportWithFilter() {

            List<CreateRequestDto> createRequests = createRequestDtos(2);

            CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder()
                    .host(createRequests.get(0).getUrl())
                    .build();
            CreateReportRequestDto requestDto = CreateReportRequestDto.builder()
                    .filter(requestFilterRequestDto)
                    .build();
            createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));


            long countReportsBeforeRequest = reportDao.count();
            long countReportRowsBeforeRequest = reportRowDao.count();

            Long actualId = reportService.generateReport(requestDto);
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> reportService.getStatus(actualId) == ReportStatus.COMPLETED);

            long countReportsAfterRequest = reportDao.count();
            long countReportRowsAfterRequest = reportRowDao.count();

            Optional<Report> optionalActualReport = reportDao.findByIdWithFilterAndRows(actualId);
            Report actualReport = optionalActualReport.get();


            List<ReportRow> expectedReportRows = new LinkedList<>();
            ReportRow currentRow = ReportRow.builder()
                    .id(actualReport.getRows().get(0).getId())
                    .rowUUID(actualReport.getRows().get(0).getRowUUID())
                    .host(createRequests.get(0).getUrl())
                    .path(createRequests.get(0).getPath())
                    .avgHeaders(0.0)
                    .avgQueryParams(0.0)
                    .report(actualReport)
                    .build();
            expectedReportRows.add(currentRow);

            RequestFilter expectedFilter = RequestFilter.builder()
                    .id(actualReport.getFilter().getId())
                    .host(createRequests.get(0).getUrl())
                    .build();
            Report expectedReport = Report.builder()
                    .id(actualId)
                    .filter(expectedFilter)
                    .status(ReportStatus.COMPLETED)
                    .createdAt(actualReport.getCreatedAt())
                    .finishedAt(actualReport.getFinishedAt())
                    .rows(expectedReportRows)
                    .build();


            assertReportEquals(expectedReport, actualReport);
            assertBothNotNull(actualReport.getCreatedAt(), actualReport.getFinishedAt());
            assertEquals(countReportsBeforeRequest + 1, countReportsAfterRequest);
            assertEquals(countReportRowsBeforeRequest + 1, countReportRowsAfterRequest);
        }

        @Test
        void twoConcurrentReports() {
            int rowsCount = 2;
            List<CreateRequestDto> createRequests = createRequestDtos(rowsCount);

            CreateRequestFilterRequestDto requestFilterRequestDto1 = CreateRequestFilterRequestDto.builder()
                    .host(createRequests.get(0).getUrl())
                    .build();
            CreateReportRequestDto requestDto1 = CreateReportRequestDto.builder()
                    .filter(requestFilterRequestDto1)
                    .build();
            CreateRequestFilterRequestDto requestFilterRequestDto2 = CreateRequestFilterRequestDto.builder()
                    .host(createRequests.get(1).getUrl())
                    .build();
            CreateReportRequestDto requestDto2 = CreateReportRequestDto.builder()
                    .filter(requestFilterRequestDto2)
                    .build();

            createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));


            long countReportsBeforeRequest = reportDao.count();
            long countReportRowsBeforeRequest = reportRowDao.count();

            CompletableFuture<Long> futureReport1 = CompletableFuture.supplyAsync(
                    () -> reportService.generateReport(requestDto1), executors);

            CompletableFuture<Long> futureReport2 = CompletableFuture.supplyAsync(
                    () -> reportService.generateReport(requestDto2), executors);

            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futureReport1, futureReport2);
            combinedFuture.join();

            Long actualIdReport1 = futureReport1.join();
            Long actualIdReport2 = futureReport2.join();

            await().atMost(10, TimeUnit.SECONDS).until(() ->
                    reportService.getStatus(actualIdReport1) == ReportStatus.COMPLETED &&
                            reportService.getStatus(actualIdReport2) == ReportStatus.COMPLETED
            );

            long countReportsAfterRequest = reportDao.count();
            long countReportRowsAfterRequest = reportRowDao.count();

            Optional<Report> optionalActualReport1 = reportDao.findByIdWithFilterAndRows(actualIdReport1);
            Report actualReport1 = optionalActualReport1.get();
            Optional<Report> optionalActualReport2 = reportDao.findByIdWithFilterAndRows(actualIdReport2);
            Report actualReport2 = optionalActualReport2.get();


            List<ReportRow> expectedReportRows1 = new LinkedList<>();
            ReportRow currentRow1 = ReportRow.builder()
                    .id(actualReport1.getRows().get(0).getId())
                    .rowUUID(actualReport1.getRows().get(0).getRowUUID())
                    .host(createRequests.get(0).getUrl())
                    .path(createRequests.get(0).getPath())
                    .avgHeaders(0.0)
                    .avgQueryParams(0.0)
                    .report(actualReport1)
                    .build();
            expectedReportRows1.add(currentRow1);

            RequestFilter expectedFilter1 = RequestFilter.builder()
                    .id(actualReport1.getFilter().getId())
                    .host(createRequests.get(0).getUrl())
                    .build();
            Report expectedReport1 = Report.builder()
                    .id(actualIdReport1)
                    .filter(expectedFilter1)
                    .status(ReportStatus.COMPLETED)
                    .createdAt(actualReport1.getCreatedAt())
                    .finishedAt(actualReport1.getFinishedAt())
                    .rows(expectedReportRows1)
                    .build();

            List<ReportRow> expectedReportRows2 = new LinkedList<>();
            ReportRow currentRow2 = ReportRow.builder()
                    .id(actualReport2.getRows().get(0).getId())
                    .rowUUID(actualReport2.getRows().get(0).getRowUUID())
                    .host(createRequests.get(1).getUrl())
                    .path(createRequests.get(1).getPath())
                    .avgHeaders(0.0)
                    .avgQueryParams(0.0)
                    .report(actualReport2)
                    .build();
            expectedReportRows2.add(currentRow2);

            RequestFilter expectedFilter2 = RequestFilter.builder()
                    .id(actualReport2.getFilter().getId())
                    .host(createRequests.get(1).getUrl())
                    .build();
            Report expectedReport2 = Report.builder()
                    .id(actualReport2.getId())
                    .filter(expectedFilter2)
                    .status(ReportStatus.COMPLETED)
                    .createdAt(actualReport2.getCreatedAt())
                    .finishedAt(actualReport2.getFinishedAt())
                    .rows(expectedReportRows2)
                    .build();


            assertReportEquals(expectedReport1, actualReport1);
            assertBothNotNull(actualReport1.getCreatedAt(), actualReport1.getFinishedAt());
            assertReportEquals(expectedReport2, actualReport2);
            assertBothNotNull(actualReport2.getCreatedAt(), actualReport2.getFinishedAt());
            assertEquals(countReportsBeforeRequest + 2, countReportsAfterRequest);
            assertEquals(countReportRowsBeforeRequest + 2, countReportRowsAfterRequest);
        }
    }

    @Nested
    class GetStatusTest {

        @ParameterizedTest(name = "status: {0}")
        @EnumSource(ReportStatus.class)
        void allStatuses(ReportStatus status) {
            Report report = Report.builder()
                    .status(status)
                    .build();
            Report persistedReport = reportDao.save(report);

            ReportStatus actualStatus = reportService.getStatus(persistedReport.getId());

            assertEquals(status, actualStatus);
        }

        @Test
        void reportNotFound() {
            Long nonExistingId = Long.MAX_VALUE;

            ClientExceptionName expected = ClientExceptionName.REPORT_NOT_FOUND;

            ClientExceptionName actual = assertThrows(ReportNotFoundException.class, () -> reportService.getStatus(nonExistingId)).getExceptionName();

            assertEquals(expected, actual);
        }
    }

    @Nested
    class CompletedTest {

        @ParameterizedTest(name = "status: {0}, completed: {1}")
        @CsvSource({
                "PENDING,    false",
                "PROCESSING, false",
                "COMPLETED,  true",
                "FAILED,     false"
        })
        void allStatuses(String inputStatus, boolean expectedCompleted) {
            ReportStatus status = ReportStatus.valueOf(inputStatus);

            Report report = Report.builder()
                    .status(status)
                    .build();
            Report persistedReport = reportDao.save(report);

           boolean actualCompleted = reportService.completed(persistedReport.getId());

            assertEquals(expectedCompleted, actualCompleted);
        }

        @Test
        void reportNotFound() {
            Long nonExistingId = Long.MAX_VALUE;

            ClientExceptionName expected = ClientExceptionName.REPORT_NOT_FOUND;

            ClientExceptionName actual = assertThrows(ReportNotFoundException.class, () -> reportService.completed(nonExistingId)).getExceptionName();

            assertEquals(expected, actual);
        }
    }

    @Nested
    class getReport {

        private Long generateAndPersistReport(int rowsCount, CreateRequestFilterRequestDto requestFilterRequestDto) {

            CreateReportRequestDto requestDto = CreateReportRequestDto.builder()
                    .filter(requestFilterRequestDto)
                    .build();

            List<CreateRequestDto> createRequests = createRequestDtos(rowsCount);
            createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));

            return reportService.generateReport(requestDto);
        }

        @Test
        void reportNotExisting() {
            Long nonExistingId = Long.MAX_VALUE;
            int page = 0;
            GetReportPageRequestDto requestDto = GetReportPageRequestDto.builder()
                    .reportId(nonExistingId)
                    .page(page)
                    .build();

            ClientExceptionName expected = ClientExceptionName.REPORT_NOT_FOUND;

            ClientExceptionName actual = assertThrows(ReportNotFoundException.class, () -> reportService.getReport(requestDto)).getExceptionName();

            assertEquals(expected, actual);
        }

        @Test
        void reportIsEmpty() {

            int rowsCount = 0;
            Long reportId = generateAndPersistReport(rowsCount, CreateRequestFilterRequestDto.builder().build());
            int page = 0;
            GetReportPageRequestDto requestDto = GetReportPageRequestDto.builder()
                    .reportId(reportId)
                    .page(page)
                    .build();

            Page<ReportRowDto> actual = reportService.getReport(requestDto).getRows();

            assertEquals(rowsCount, actual.getTotalElements());
        }

        @Test
        void onePageInReport() {

            int rowsCount = dataPageSize / 2;
            List<CreateRequestDto> createRequests = createRequestDtos(rowsCount);
            createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));

            CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder().build();
            CreateReportRequestDto createReportRequestDto = CreateReportRequestDto.builder()
                    .filter(requestFilterRequestDto)
                    .build();

            Long reportId = reportService.generateReport(createReportRequestDto);
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> reportService.getStatus(reportId) == ReportStatus.COMPLETED);

            int page = 0;
            GetReportPageRequestDto requestDto = GetReportPageRequestDto.builder()
                    .reportId(reportId)
                    .page(page)
                    .build();


            ReportPageResponseDto actualReportPage = reportService.getReport(requestDto);
            Report actualReport = reportDao.findById(reportId).get();


            List<ReportRowDto> expectedRows = new LinkedList<>();
            for (int i = 0; i < rowsCount; i++) {
                ReportRowDto currentRow = ReportRowDto.builder()
                        .id(actualReportPage.getRows().getContent().get(i).getId())
                        .rowUUID(actualReportPage.getRows().getContent().get(i).getRowUUID())
                        .host(createRequests.get(i).getUrl())
                        .path(createRequests.get(i).getPath())
                        .avgHeaders(0.0)
                        .avgQueryParams(0.0)
                        .build();

                expectedRows.add(currentRow);
            }
            Page<ReportRowDto> pageOfReportRows = new PageImpl<>(
                    expectedRows,
                    PageRequest.of(0, rowsCount),
                    expectedRows.size()
            );
            RequestFilterDto expectedFilter = RequestFilterDto.builder()
                    .id(actualReport.getFilter().getId())
                    .build();
            ReportPageResponseDto expectedReportPage = ReportPageResponseDto.builder()
                    .reportId(reportId)
                    .filter(expectedFilter)
                    .status(ReportStatus.COMPLETED)
                    .createdAt(actualReport.getCreatedAt())
                    .finishedAt(actualReport.getFinishedAt())
                    .rows(pageOfReportRows)
                    .build();


            assertReportPageDtoEquals(expectedReportPage, actualReportPage);
            assertEquals(rowsCount, actualReportPage.getRows().getTotalElements());
            assertEquals(1, actualReportPage.getRows().getTotalPages());
        }

        @Test
        void fewPagesInReport() {

            int rowsCount = (int) (dataPageSize * 1.5);
            List<CreateRequestDto> createRequests = createRequestDtos(rowsCount);
            createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));

            CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder().build();
            CreateReportRequestDto createReportRequestDto = CreateReportRequestDto.builder()
                    .filter(requestFilterRequestDto)
                    .build();

            Long reportId = reportService.generateReport(createReportRequestDto);
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> reportService.getStatus(reportId) == ReportStatus.COMPLETED);

            GetReportPageRequestDto requestDto1 = GetReportPageRequestDto.builder()
                    .reportId(reportId)
                    .page(0)
                    .build();
            GetReportPageRequestDto requestDto2 = GetReportPageRequestDto.builder()
                    .reportId(reportId)
                    .page(1)
                    .build();


            Page<ReportRowDto> actualPage1 = reportService.getReport(requestDto1).getRows();
            Page<ReportRowDto> actualPage2 = reportService.getReport(requestDto2).getRows();
            List<ReportRowDto> actualRowsPage1 = actualPage1.getContent();
            List<ReportRowDto> actualRowsPage2 = actualPage2.getContent();
            List<ReportRowDto> actualRows = new LinkedList<>();
            actualRows.addAll(actualRowsPage1);
            actualRows.addAll(actualRowsPage2);

            List<ReportRowDto> expectedRows = new LinkedList<>();
            for (int i = 0; i < dataPageSize; i++) {
                String expectedUrl = createRequests.get(i).getUrl();
                String expectedPath = createRequests.get(i).getPath();
                Long expectedId = actualRows.stream().filter(dto -> dto.getHost().equals(expectedUrl)).findFirst().get().getId();
                UUID expectUUID = actualRows.stream().filter(dto -> dto.getHost().equals(expectedUrl)).findFirst().get().getRowUUID();

                ReportRowDto currentRow = ReportRowDto.builder()
                        .id(expectedId)
                        .rowUUID(expectUUID)
                        .host(expectedUrl)
                        .path(expectedPath)
                        .avgHeaders(0.0)
                        .avgQueryParams(0.0)
                        .build();

                expectedRows.add(currentRow);
            }
            for (int i = dataPageSize; i < rowsCount; i++) {
                String expectedUrl = createRequests.get(i).getUrl();
                String expectedPath = createRequests.get(i).getPath();
                Long expectedId = actualRows.stream().filter(dto -> dto.getHost().equals(expectedUrl)).findFirst().get().getId();
                UUID expectUUID = actualRows.stream().filter(dto -> dto.getHost().equals(expectedUrl)).findFirst().get().getRowUUID();

                ReportRowDto currentRow = ReportRowDto.builder()
                        .id(expectedId)
                        .rowUUID(expectUUID)
                        .host(expectedUrl)
                        .path(expectedPath)
                        .avgHeaders(0.0)
                        .avgQueryParams(0.0)
                        .build();

                expectedRows.add(currentRow);
            }

            assertReportRowsDtoEquals(expectedRows, actualRows);
            assertEquals(rowsCount, actualPage1.getTotalElements());
            assertEquals(2, actualPage1.getTotalPages());
        }

        @Test
        void reportUsingFilter() {

            List<CreateRequestDto> createRequests = createRequestDtos(2);
            createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));

            CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder()
                    .host(createRequests.get(0).getUrl())
                    .build();
            CreateReportRequestDto createReportRequestDto = CreateReportRequestDto.builder()
                    .filter(requestFilterRequestDto)
                    .build();

            Long reportId = reportService.generateReport(createReportRequestDto);
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> reportService.getStatus(reportId) == ReportStatus.COMPLETED);

            int page = 0;
            GetReportPageRequestDto requestDto = GetReportPageRequestDto.builder()
                    .reportId(reportId)
                    .page(page)
                    .build();


            Page<ReportRowDto> actual = reportService.getReport(requestDto).getRows();
            List<ReportRowDto> actualRows = actual.getContent();


            List<ReportRowDto> expectedRows = new LinkedList<>();
            ReportRowDto currentRow = ReportRowDto.builder()
                    .id(actualRows.get(0).getId())
                    .rowUUID(actualRows.get(0).getRowUUID())
                    .host(createRequests.get(0).getUrl())
                    .path(createRequests.get(0).getPath())
                    .avgHeaders(0.0)
                    .avgQueryParams(0.0)
                    .build();
            expectedRows.add(currentRow);


            assertReportRowsDtoEquals(expectedRows, actualRows);
            assertEquals(1, actual.getTotalElements());
            assertEquals(1, actual.getTotalPages());
        }
    }

    @Nested
    class getReports {

        @Test
        void onePageReport() {
            CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder().build();
            CreateReportRequestDto requestDto = CreateReportRequestDto.builder()
                    .filter(requestFilterRequestDto)
                    .build();

            List<CreateRequestDto> createRequests = createRequestDtos(1);
            createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));

            int reportsCount = dataPageSize / 2;
            for (int i = 0; i < reportsCount; i++) {
                Long reportId = reportService.generateReport(requestDto);
                await().atMost(10, TimeUnit.SECONDS)
                        .until(() -> reportService.getStatus(reportId) == ReportStatus.COMPLETED);
            }

            Page<ReportDataDto> actual = reportService.getReports(0);

            assertEquals(reportsCount, actual.getTotalElements());
        }

        @Test
        void fewPagesReport() {
            CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder().build();
            CreateReportRequestDto requestDto = CreateReportRequestDto.builder()
                    .filter(requestFilterRequestDto)
                    .build();

            List<CreateRequestDto> createRequests = createRequestDtos(1);
            createRequests.forEach(createRequestDto -> requestService.create(createRequestDto));

            int reportsCount = (int) (dataPageSize * 1.5);
            for (int i = 0; i < reportsCount; i++) {
                Long reportId = reportService.generateReport(requestDto);
                await().atMost(10, TimeUnit.SECONDS)
                        .until(() -> reportService.getStatus(reportId) == ReportStatus.COMPLETED);
            }

            Page<ReportDataDto> actual = reportService.getReports(0);

            assertEquals(reportsCount, actual.getTotalElements());
        }

        @Test
        void zeroReports() {
            int reportsCount = 0;

            Page<ReportDataDto> actual = reportService.getReports(0);

            assertEquals(reportsCount, actual.getTotalElements());
        }
    }

    @Nested
    class getReportTest {

        private List<CreateReportRequestDto> generateReport(int count) {
            CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder().build();

            List<CreateReportRequestDto> reports = new LinkedList<>();
            for (int i = 0; i < count; i++) {
                reports.add(
                        CreateReportRequestDto.builder()
                            .filter(requestFilterRequestDto)
                            .build()
                );
            }

            return reports;
        }

        @Test
        void getOnePageReport() {

            int count = dataPageSize / 2;
            List<CreateReportRequestDto> reportsCreateRequests = generateReport(count);
            reportsCreateRequests.forEach(request -> reportService.generateReport(request));

            Page<ReportDataDto> reports = reportService.getReports(0);

            assertEquals(count, reports.getTotalElements());
        }

        @Test
        void fewPagesReport() {

            int count = (int) (dataPageSize * 1.5);
            List<CreateReportRequestDto> reportsCreateRequests = generateReport(count);
            reportsCreateRequests.forEach(request -> reportService.generateReport(request));

            Page<ReportDataDto> reports = reportService.getReports(0);

            assertEquals(count, reports.getTotalElements());
        }

        @Test
        void zeroReports() {
            int count = 0;

            Page<ReportDataDto> reports = reportService.getReports(0);

            assertEquals(count, reports.getTotalElements());
        }
    }
}