package axi.practice.data_generation_reports.service.file_service;

import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dao.ReportFileDao;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.RequestFilter;
import axi.practice.data_generation_reports.entity.enums.MimeType;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import axi.practice.data_generation_reports.mapper.ReportFileMapper;
import axi.practice.data_generation_reports.service.ReportService;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public abstract class AbstractBuildingFileService<B> extends AbstractFileService {

    public AbstractBuildingFileService(
            ReportService reportService,
            ReportDao reportDao,
            ReportFileDao reportFileDao,
            ReportFileMapper reportFileMapper,
            String reportsDirectory) {
        super(reportService, reportDao, reportFileDao, reportFileMapper, reportsDirectory);
    }

    protected abstract B createBuilder(OutputStream outputStream) throws IOException;
    protected abstract void writeReportMetadata(B builder, Report report) throws IOException ;
    protected abstract void writeFilterData(B builder, RequestFilter filter) throws IOException ;
    protected abstract void writeReportRows(B builder, Report report) throws IOException ;
    protected abstract void endBuilding(B builder, OutputStream outputStream) throws IOException ;

    public abstract MimeType getMimeType();

    public final void generateFileContent(OutputStream outputStream, Report report) throws IOException {
        B builder = createBuilder(outputStream);
        writeReportMetadata(builder, report);

        if (report.getStatus() != ReportStatus.FAILED) {
            writeFilterData(builder, report.getFilter());
            writeReportRows(builder, report);
        }

        endBuilding(builder, outputStream);
    }
}
