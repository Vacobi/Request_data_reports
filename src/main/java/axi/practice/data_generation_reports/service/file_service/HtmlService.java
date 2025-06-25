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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class HtmlService extends AbstractFileService {

    public HtmlService(
            ReportService reportService,
            ReportDao reportDao,
            ReportFileDao reportFileDao,
            ReportFileMapper reportFileMapper,
            String reportsDirectory) {
        super(reportService, reportDao, reportFileDao, reportFileMapper, reportsDirectory);
    }

    @Override
    protected void generateFileContent(OutputStream outputStream, Report report) throws IOException {
        Document doc = Jsoup.parse("<!DOCTYPE html><html><head><title>Report</title></head><body></body></html>");
        Element body = doc.body();

        writeReportMetadata(body, report);

        if (report.getStatus() != ReportStatus.FAILED) {
            writeFilterData(body, report.getFilter());
            writeReportRows(body, report);
        }

        outputStream.write(doc.outerHtml().getBytes(StandardCharsets.UTF_8));
    }

    private void writeReportMetadata(Element body, Report report) {
        Element metadata = body.appendElement("section").attr("id", "report_data");
        metadata.appendElement("h1").text("report_data");
        metadata.appendElement("p").text("report_id: " + report.getId());
        metadata.appendElement("p").text("status: " + report.getStatus());
        metadata.appendElement("p").text("created_at: " + report.getCreatedAt());
        metadata.appendElement("p").text("finished_at: " + report.getFinishedAt());
    }

    private void writeFilterData(Element body, RequestFilter filter) {
        Element section = body.appendElement("section").attr("id", "filter_id");

        section.appendElement("h2").text("filter");

        section.appendElement("p").text("filter_id: " + filter.getId());

        if (filter.getHost() != null) {
            section.appendElement("p").text("host: " + filter.getHost());
        }

        if (filter.getPath() != null) {
            section.appendElement("p").text("path: " + filter.getPath());
        }

        if (filter.getFromDate() != null) {
            section.appendElement("p").text("from: " + filter.getFromDate());
        }

        if (filter.getToDate() != null) {
            section.appendElement("p").text("to: " + filter.getToDate());
        }

        if (filter.getAvgHeaders() != null) {
            section.appendElement("p").text("avg_headers: " + filter.getAvgHeaders());
        }

        if (filter.getAvgQueryParams() != null) {
            section.appendElement("p").text("avg_params: " + filter.getAvgQueryParams());
        }
    }

    private void writeReportRows(Element body, Report report) {
        Element section = body.appendElement("section").attr("id", "rows");
        section.appendElement("h2").text("report_rows");

        Element table = section.appendElement("table").attr("border", "1");
        Element header = table.appendElement("tr");
        header.appendElement("th").text("rowUUID");
        header.appendElement("th").text("host");
        header.appendElement("th").text("path");
        header.appendElement("th").text("avg_headers");
        header.appendElement("th").text("avg_params");

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
                Element tr = table.appendElement("tr");
                tr.appendElement("td").text(row.getRowUUID().toString());
                tr.appendElement("td").text(row.getHost());
                tr.appendElement("td").text(row.getPath() != null ? row.getPath() : "");
                tr.appendElement("td").text(String.format("%.2f", row.getAvgHeaders()));
                tr.appendElement("td").text(String.format("%.2f", row.getAvgQueryParams()));
            }

            pagesOut = pageNumber >= page.getTotalPages();
        }
    }

    @Override
    public MimeType getMimeType() {
        return MimeType.HTML;
    }
}
