package axi.practice.data_generation_reports.service.file_service;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dao.ReportFileDao;
import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.report.CreateReportRequestDto;
import axi.practice.data_generation_reports.dto.report_file.CreateReportFileRequestDto;
import axi.practice.data_generation_reports.dto.report_file.ReportFileDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.dto.request.RequestDto;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.ReportFile;
import axi.practice.data_generation_reports.entity.enums.MimeType;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import axi.practice.data_generation_reports.entity.enums.StorageType;
import axi.practice.data_generation_reports.exception.ClientExceptionName;
import axi.practice.data_generation_reports.exception.ReportFileAlreadyStored;
import axi.practice.data_generation_reports.service.ReportService;
import axi.practice.data_generation_reports.service.RequestService;
import axi.practice.data_generation_reports.util.ClearableTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static axi.practice.data_generation_reports.util.FileAsserts.assertReportFilesDtoEquals;
import static axi.practice.data_generation_reports.util.FileAsserts.assertReportFilesEquals;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
abstract class AbstractFileServiceTest extends ClearableTest {

    private final AbstractFileService fileService;

    @Autowired
    private ReportFileDao reportFileDao;

    @Autowired
    private RequestService requestService;
    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportDao reportDao;

    public AbstractFileServiceTest(AbstractFileService fileService) {
        this.fileService = fileService;
    }

    private Long currentRequest = 0L;
    protected List<RequestDto> createRequestDtos(int count) {

        List<CreateRequestDto> createRequests = new LinkedList<>();

        for (int i = 0; i < count; i++) {
            CreateRequestDto requestDto = CreateRequestDto.builder()
                    .url("google.com" + currentRequest)
                    .path("search" + currentRequest)
                    .build();
            createRequests.add(requestDto);

            currentRequest++;
        }

        List<RequestDto> requests = new LinkedList<>();
        createRequests.forEach(request -> requests.add(requestService.create(request)));

        return requests;
    }

    protected List<Report> generateReport(int count) {
        CreateRequestFilterRequestDto requestFilterRequestDto = CreateRequestFilterRequestDto.builder().build();

        List<CreateReportRequestDto> createReportRequests = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            createReportRequests.add(
                    CreateReportRequestDto.builder()
                            .filter(requestFilterRequestDto)
                            .build()
            );
        }

        final List<Report> reports = new LinkedList<>();
        createReportRequests.forEach(request -> {
            Long reportId = reportService.generateReport(request);
            Report actualReport = reportDao.findById(reportId).get();
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> reportService.getStatus(actualReport.getId()) == ReportStatus.COMPLETED);
            reports.add(reportDao.findById(reportId).get());
        });

        return reports;
    }

    protected abstract MimeType getMimeType();

    @Test
    void mimeType() {
        MimeType expected = getMimeType();

        MimeType actual = fileService.getMimeType();

        assertEquals(expected, actual);
    }

    @Test
    void generateReportWithOneRowToDb() {
        createRequestDtos(1);
        Report actualReport = generateReport(1).get(0);

        StorageType storageType = StorageType.DATABASE;
        CreateReportFileRequestDto createReportFileRequestDto = CreateReportFileRequestDto.builder()
                .reportId(actualReport.getId())
                .mimeType(getMimeType())
                .storageType(storageType)
                .build();


        long filesCountBeforeGenerate = reportFileDao.count();

        ReportFileDto actualDto = fileService.createReportFile(createReportFileRequestDto);
        ReportFile actual = reportFileDao.findByIdDetailed(actualDto.getId()).get();

        long filesCountAfterGenerate = reportFileDao.count();


        String expectedFileName = "report_" + actualReport.getId() + "." + getMimeType().toString().toLowerCase();
        ReportFileDto expectedDto = ReportFileDto.builder()
                .id(actualDto.getId())
                .reportId(actualReport.getId())
                .fileName(expectedFileName)
                .filePath(actualDto.getFilePath())
                .storageType(storageType)
                .mimeType(getMimeType())
                .fileData(actualDto.getFileData())
                .createdAt(actualDto.getCreatedAt())
                .build();
        ReportFile expected = ReportFile.builder()
                .id(actualDto.getId())
                .report(actualReport)
                .fileName(expectedFileName)
                .filePath(actualDto.getFilePath())
                .storageType(storageType)
                .mimeType(getMimeType())
                .fileData(actual.getFileData())
                .createdAt(actualDto.getCreatedAt())
                .build();

        assertReportFilesDtoEquals(expectedDto, actualDto);
        assertReportFilesEquals(expected, actual);
        assertEquals(filesCountBeforeGenerate + 1, filesCountAfterGenerate);
    }

    @Test
    void generateReportWithFewRowsToDb() {
        createRequestDtos(5);
        Report actualReport = generateReport(1).get(0);

        StorageType storageType = StorageType.DATABASE;
        CreateReportFileRequestDto createReportFileRequestDto = CreateReportFileRequestDto.builder()
                .reportId(actualReport.getId())
                .mimeType(getMimeType())
                .storageType(storageType)
                .build();


        long filesCountBeforeGenerate = reportFileDao.count();

        ReportFileDto actualDto = fileService.createReportFile(createReportFileRequestDto);
        ReportFile actual = reportFileDao.findByIdDetailed(actualDto.getId()).get();

        long filesCountAfterGenerate = reportFileDao.count();


        String expectedFileName = "report_" + actualReport.getId() + "." + getMimeType().toString().toLowerCase();
        ReportFileDto expectedDto = ReportFileDto.builder()
                .id(actualDto.getId())
                .reportId(actualReport.getId())
                .fileName(expectedFileName)
                .filePath(actualDto.getFilePath())
                .storageType(storageType)
                .mimeType(getMimeType())
                .fileData(actualDto.getFileData())
                .createdAt(actualDto.getCreatedAt())
                .build();
        ReportFile expected = ReportFile.builder()
                .id(actualDto.getId())
                .report(actualReport)
                .fileName(expectedFileName)
                .filePath(actualDto.getFilePath())
                .storageType(storageType)
                .mimeType(getMimeType())
                .fileData(actual.getFileData())
                .createdAt(actualDto.getCreatedAt())
                .build();

        assertReportFilesDtoEquals(expectedDto, actualDto);
        assertReportFilesEquals(expected, actual);
        assertEquals(filesCountBeforeGenerate + 1, filesCountAfterGenerate);
    }

    @Test
    void generateFileToOneReportWithFithFewReportsInDb() {
        createRequestDtos(3);
        Report actualReport = generateReport(5).get(2);

        StorageType storageType = StorageType.DATABASE;
        CreateReportFileRequestDto createReportFileRequestDto = CreateReportFileRequestDto.builder()
                .reportId(actualReport.getId())
                .mimeType(getMimeType())
                .storageType(storageType)
                .build();


        long filesCountBeforeGenerate = reportFileDao.count();

        ReportFileDto actualDto = fileService.createReportFile(createReportFileRequestDto);
        ReportFile actual = reportFileDao.findByIdDetailed(actualDto.getId()).get();

        long filesCountAfterGenerate = reportFileDao.count();


        String expectedFileName = "report_" + actualReport.getId() + "." + getMimeType().toString().toLowerCase();
        ReportFileDto expectedDto = ReportFileDto.builder()
                .id(actualDto.getId())
                .reportId(actualReport.getId())
                .fileName(expectedFileName)
                .filePath(actualDto.getFilePath())
                .storageType(storageType)
                .mimeType(getMimeType())
                .fileData(actualDto.getFileData())
                .createdAt(actualDto.getCreatedAt())
                .build();
        ReportFile expected = ReportFile.builder()
                .id(actualDto.getId())
                .report(actualReport)
                .fileName(expectedFileName)
                .filePath(actualDto.getFilePath())
                .storageType(storageType)
                .mimeType(getMimeType())
                .fileData(actual.getFileData())
                .createdAt(actualDto.getCreatedAt())
                .build();

        assertReportFilesDtoEquals(expectedDto, actualDto);
        assertReportFilesEquals(expected, actual);
        assertEquals(filesCountBeforeGenerate + 1, filesCountAfterGenerate);
    }

    @Test
    void generateFilesToSeveralReports() {
        createRequestDtos(3);
        int reportsCount = 4;
        List<Report> actualReports = generateReport(reportsCount);

        StorageType storageType = StorageType.DATABASE;


        long filesCountBeforeGenerate = reportFileDao.count();

        actualReports.forEach(report -> {
            CreateReportFileRequestDto createReportFileRequestDto = CreateReportFileRequestDto.builder()
                    .reportId(report.getId())
                    .mimeType(getMimeType())
                    .storageType(storageType)
                    .build();

            fileService.createReportFile(createReportFileRequestDto);
        });

        long filesCountAfterGenerate = reportFileDao.count();


        assertEquals(filesCountBeforeGenerate + reportsCount, filesCountAfterGenerate);
    }

    @Test
    void generateCopyOfReportFile() {
        createRequestDtos(3);
        Report actualReport = generateReport(1).get(0);

        StorageType storageType = StorageType.DATABASE;
        CreateReportFileRequestDto createReportFileRequestDto = CreateReportFileRequestDto.builder()
                .reportId(actualReport.getId())
                .mimeType(getMimeType())
                .storageType(storageType)
                .build();


        long filesCountBeforeGenerate = reportFileDao.count();

        fileService.createReportFile(createReportFileRequestDto);
        ReportFileAlreadyStored actualException = assertThrows(ReportFileAlreadyStored.class, () -> fileService.createReportFile(createReportFileRequestDto));
        ClientExceptionName actualExceptionName = actualException.getExceptionName();

        long filesCountAfterGenerate = reportFileDao.count();


        ClientExceptionName expectedExceptionName = ClientExceptionName.REPORT_FILE_ALREADY_EXISTS;


        assertEquals(expectedExceptionName, actualExceptionName);
        assertEquals(filesCountBeforeGenerate + 1, filesCountAfterGenerate);
    }
}