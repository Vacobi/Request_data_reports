package axi.practice.data_generation_reports.service.file_service;

import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import axi.practice.data_generation_reports.exception.*;
import axi.practice.data_generation_reports.service.ReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractFileService {
    
    protected final ReportService reportService;

    protected final ReportDao reportDao;

    private final String reportsDirectory;


    protected abstract Writer createWriter(File file) throws IOException;

    protected abstract void writeReportContent(Writer writer, Report report) throws IOException;

    protected abstract String generateReportName(Report report);


    @Transactional
    public File getReportFile(Long reportId) {

        Report report = getValidatedReport(reportId);

        File file = prepareReportFile(report);

        try (Writer writer = createWriter(file)) {
            writeReportContent(writer, report);
            return file;
        } catch (IOException e) {
            throw new CanNotGenerateFile(file.getName());
        }
    }

    private Report getValidatedReport(Long reportId) {
        Optional<Report> optionalReport = reportDao.findById(reportId);
        if (optionalReport.isEmpty()) {
            throw new ReportNotFound(reportId);
        }

        Report report = optionalReport.get();
        verifyState(report);

        return report;
    }

    private void verifyState(Report report) {
        if (!ReportStatus.isFinalState(report.getStatus())) {
            throw new ReportIsNotInFinalState(report.getId());
        }
    }

    private File prepareReportFile(Report report) {
        String fileName = generateReportName(report);
        File file = new File(reportsDirectory + fileName);

        if (file.exists()) {
            throw new ReportFileAlreadyExists(report.getId(), file.getAbsolutePath());
        }

        createParentDirectory(file);
        return file;
    }

    private void createParentDirectory(File file) {
        File parentDir = file.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new CanNotCreateDirectoryException(parentDir.getAbsolutePath());
        }
    }
}
