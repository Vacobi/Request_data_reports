package axi.practice.data_generation_reports.service.file_service;

import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dao.ReportFileDao;
import axi.practice.data_generation_reports.dto.report_file.CreateReportFileRequestDto;
import axi.practice.data_generation_reports.dto.report_file.ReportFileDto;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.ReportFile;
import axi.practice.data_generation_reports.entity.enums.MimeType;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import axi.practice.data_generation_reports.entity.enums.StorageType;
import axi.practice.data_generation_reports.exception.*;
import axi.practice.data_generation_reports.mapper.ReportFileMapper;
import axi.practice.data_generation_reports.service.ReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractFileService {
    
    protected final ReportService reportService;
    protected final ReportDao reportDao;
    private final ReportFileDao reportFileDao;
    private final ReportFileMapper reportFileMapper;
    private final String reportsDirectory;


    protected abstract void generateFileContent(OutputStream outputStream, Report report) throws IOException;

    public abstract MimeType getMimeType();


    @Transactional
    public ReportFileDto getReportFile(Long reportId) {
        Optional<ReportFile> optionalReportFile = reportFileDao.findByMimeTypeAndReport_Id(getMimeType(), reportId);

        if (optionalReportFile.isEmpty()) {
            throw new ReportFileNotFoundException(reportId);
        }

        return reportFileMapper.toReportFileDto(optionalReportFile.get());
    }

    @Transactional
    public ReportFileDto createReportFile(CreateReportFileRequestDto createRequestDto) {

        Optional<ReportFile> sameReportFile = reportFileDao.findByMimeTypeAndReport_Id(getMimeType(), createRequestDto.getReportId());
        if (sameReportFile.isPresent()) {
            throw new ReportFileAlreadyStoredException(createRequestDto.getReportId(), sameReportFile.get().getId());
        }

        Report report = getValidatedReport(createRequestDto.getReportId());
        String fileName = generateReportName(report);

        byte[] fileContent = generateFileContent(report);

        ReportFile reportFile = saveReportFile(report, fileName, fileContent, createRequestDto.getStorageType());

        linkReportToFile(report.getId(), reportFile);

        // Нужно, чтобы получить id записи (тк в Report каскадное сохранение)
        // get без проверки потому что, по логике, он там должен быть
        ReportFile persisted = reportFileDao.findByMimeTypeAndReport_Id(getMimeType(), createRequestDto.getReportId()).get();

        return reportFileMapper.toReportFileDto(persisted);
    }

    private Report getValidatedReport(Long reportId) {

        Report report = getRawReport(reportId);
        verifyState(report);

        return report;
    }

    private Report getRawReport(Long reportId) {
        Optional<Report> optionalReport = reportDao.findById(reportId);
        if (optionalReport.isEmpty()) {
            throw new ReportNotFoundException(reportId);
        }

        return optionalReport.get();
    }

    private void verifyState(Report report) {
        if (!ReportStatus.isFinalState(report.getStatus())) {
            throw new ReportIsNotInFinalStateException(report.getId());
        }
    }

    private byte[] generateFileContent(Report report) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            generateFileContent(outputStream, report);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new CanNotGenerateFileException("report_" + report.getId());
        }
    }

    private ReportFile saveReportFile(Report report, String fileName, byte[] fileContent, StorageType storageType) {
        if (storageType == StorageType.DISK) {
            return saveToDisk(report, fileName, fileContent);
        }

        return saveToDatabase(report, fileName, fileContent);
    }

    private void linkReportToFile(Long reportId, ReportFile reportFile) {
        reportService.addReportFileToReport(reportId, reportFile);
    }

    private ReportFile saveToDisk(Report report, String fileName, byte[] fileContent) {
        File file = prepareReportFile(report);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileContent);
        } catch (IOException e) {
            throw new CanNotGenerateFileException("Failed to write file: " + file.getName());
        }

        return ReportFile.builder()
                .report(report)
                .fileName(fileName)
                .filePath(file.getAbsolutePath())
                .storageType(StorageType.DISK)
                .mimeType(getMimeType())
                .build();
    }

    private ReportFile saveToDatabase(Report report, String fileName, byte[] fileContent) {
        return ReportFile.builder()
                .report(report)
                .fileName(fileName)
                .filePath(null)
                .storageType(StorageType.DATABASE)
                .mimeType(getMimeType())
                .fileData(fileContent)
                .build();
    }

    private File prepareReportFile(Report report) {
        String fileName = generateReportName(report);
        File file = new File(reportsDirectory + fileName);

        if (file.exists()) {
            throw new ReportFileAlreadyStoredException(file.getAbsolutePath());
        }

        createParentDirectory(file);
        return file;
    }

    protected String generateReportName(Report report) {
        return "report_" + report.getId() + "." + getMimeType().toString();
    }

    private void createParentDirectory(File file) {
        File parentDir = file.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new CanNotCreateDirectoryException(parentDir.getAbsolutePath());
        }
    }
}
