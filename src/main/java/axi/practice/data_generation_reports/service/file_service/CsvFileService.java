package axi.practice.data_generation_reports.service.file_service;

import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dao.ReportFileDao;
import axi.practice.data_generation_reports.dto.report.GetReportPageRequestDto;
import axi.practice.data_generation_reports.dto.report_row.ReportRowDto;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.RequestFilter;
import axi.practice.data_generation_reports.entity.enums.MimeType;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import axi.practice.data_generation_reports.mapper.ReportFileMapper;
import axi.practice.data_generation_reports.service.ReportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class CsvFileService extends AbstractFileService {

    public CsvFileService(
            ReportService reportService,
            ReportDao reportDao,
            ReportFileDao reportFileDao,
            ReportFileMapper reportFileMapper,
            String reportsDirectory) {
        super(reportService, reportDao, reportFileDao, reportFileMapper, reportsDirectory);
    }

    @Override
    protected void generateFileContent(OutputStream outputStream, Report report) throws IOException {
        Writer writer = new OutputStreamWriter(outputStream);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

        writeReportMetadata(csvPrinter, report);

        if (report.getStatus() != ReportStatus.FAILED) {
            csvPrinter.println();
            writeFilterData(csvPrinter, report.getFilter());

            csvPrinter.println();
            writeReportRows(csvPrinter, report);
        }

        csvPrinter.flush();
        writer.flush();
    }

    private void writeReportMetadata(CSVPrinter csvPrinter, Report report) throws IOException {
        csvPrinter.printRecord(
                "report_id",
                "status",
                "created_at",
                "finished_at"
        );
        csvPrinter.printRecord(
                report.getId(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getFinishedAt()
        );
    }

    private void writeFilterData(CSVPrinter csvPrinter, RequestFilter filter) throws IOException {
        csvPrinter.printRecord("filter_id",
                "from",
                "to",
                "host",
                "Path",
                "avg_headers",
                "avg_params");
        csvPrinter.printRecord(filter.getId(),
                filter.getFromDate(),
                filter.getToDate(),
                filter.getHost(),
                filter.getPath(),
                write(filter.getAvgHeaders()),
                write(filter.getAvgQueryParams()));
    }

    private String write(Double value) {
        return value == null ? "" : String.format("%.2f", value);
    }

    private void writeReportRows(CSVPrinter csvPrinter, Report report) throws IOException {
        csvPrinter.printRecord("rowUUID", "host", "path", "avg_headers", "avg_params");

        int pageNumber = 0;
        boolean pagesOut = false;
        while (!pagesOut) {
            GetReportPageRequestDto request = GetReportPageRequestDto.builder()
                    .reportId(report.getId())
                    .page(pageNumber++)
                    .build();
            Page<ReportRowDto> page = reportService.getReport(request).getRows();

            List<ReportRowDto> rows = page.getContent();
            for (ReportRowDto row : rows) {
                csvPrinter.printRecord(
                        row.getRowUUID(),
                        row.getHost(),
                        row.getPath(),
                        String.format("%.2f", row.getAvgHeaders()), // не может быть null
                        String.format("%.2f", row.getAvgQueryParams()) // не может быть null
                );
            }

            pagesOut = pageNumber >= page.getTotalPages();
        }
    }

    @Override
    public MimeType getMimeType() {
        return MimeType.CSV;
    }
}
