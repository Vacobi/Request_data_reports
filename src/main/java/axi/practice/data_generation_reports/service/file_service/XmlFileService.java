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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class XmlFileService extends AbstractFileService {

    public XmlFileService(
            ReportService reportService,
            ReportDao reportDao,
            ReportFileDao reportFileDao,
            ReportFileMapper reportFileMapper,
            String reportsDirectory) {
        super(reportService, reportDao, reportFileDao, reportFileMapper, reportsDirectory);
    }

    @Override
    protected void generateFileContent(OutputStream outputStream, Report report) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

        ObjectNode root = xmlMapper.createObjectNode();

        writeReportMetadata(root, report);

        if (report.getStatus() != ReportStatus.FAILED) {
            writeFilterData(root, report.getFilter());
            writeReportRows(root, report);
        }

        xmlMapper.writer()
                .withRootName("report")
                .writeValue(outputStream, root);
    }

    private void writeReportMetadata(ObjectNode root, Report report) {
        ObjectNode metadata = root.putObject("report_data");
        metadata.put("report_id", report.getId());
        metadata.put("status", report.getStatus().name());
        metadata.put("created_at", report.getCreatedAt().toString());
        metadata.put("finished_at", report.getFinishedAt().toString());
    }

    private void writeFilterData(ObjectNode root, RequestFilter filter) {
        ObjectNode filterNode = root.putObject("filter");

        filterNode.put("filter_id", filter.getId());

        if (filter.getHost() != null) {
            filterNode.put("host", filter.getHost());
        }

        if (filter.getPath() != null) {
            filterNode.put("path", filter.getPath());
        }

        if (filter.getFromDate() != null) {
            filterNode.put("from", filter.getFromDate().toString());
        }

        if (filter.getToDate() != null) {
            filterNode.put("to", filter.getToDate().toString());
        }

        if (filter.getAvgHeaders() != null) {
            filterNode.put("avg_headers", filter.getAvgHeaders());
        }

        if (filter.getAvgQueryParams() != null) {
            filterNode.put("avg_params", filter.getAvgQueryParams());
        }
    }

    private void writeReportRows(ObjectNode root, Report report) {
        ObjectNode rowsWrapper = root.putObject("report_rows");
        ArrayNode rowsArray = rowsWrapper.putArray("row");

        int pageNumber = 0;
        boolean pagesOut = false;
        while (!pagesOut) {
            GetReportPageRequestDto request = GetReportPageRequestDto.builder()
                    .reportId(report.getId())
                    .page(pageNumber++)
                    .build();
            Page<ReportRowDto> page = reportService.getReport(request).getRows();

            List<ReportRowDto> rows = page.getContent();
            rows.forEach(row -> {
                ObjectNode rowNode = rowsArray.addObject();
                rowNode.put("rowUUID", row.getRowUUID().toString());
                rowNode.put("host", row.getHost());
                if (row.getPath() != null) {
                    rowNode.put("path", row.getPath());
                }
                rowNode.put("avg_headers", String.format("%.2f", row.getAvgHeaders()));
                rowNode.put("avg_params", String.format("%.2f", row.getAvgQueryParams()));
            });

            pagesOut = pageNumber >= page.getTotalPages();
        }
    }

    @Override
    public MimeType getMimeType() {
        return MimeType.XML;
    }
}
