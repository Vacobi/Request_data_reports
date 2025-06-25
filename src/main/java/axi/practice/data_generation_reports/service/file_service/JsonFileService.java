package axi.practice.data_generation_reports.service.file_service;

import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dao.ReportFileDao;
import axi.practice.data_generation_reports.dto.report.GetReportPageRequestDto;
import axi.practice.data_generation_reports.dto.report_row.ReportRowDto;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.RequestFilter;
import axi.practice.data_generation_reports.entity.enums.MimeType;
import axi.practice.data_generation_reports.mapper.ReportFileMapper;
import axi.practice.data_generation_reports.service.ReportService;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class JsonFileService extends AbstractBuildingFileService<ObjectNode> {

    private final JsonMapper jsonMapper;

    public JsonFileService(
            ReportService reportService,
            ReportDao reportDao,
            ReportFileDao reportFileDao,
            ReportFileMapper reportFileMapper,
            String reportsDirectory) {
        super(reportService, reportDao, reportFileDao, reportFileMapper, reportsDirectory);

        this.jsonMapper = new JsonMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected ObjectNode createBuilder(OutputStream outputStream) {
        return jsonMapper.createObjectNode();
    }

    @Override
    protected void writeReportMetadata(ObjectNode root, Report report) {
        ObjectNode meta = root.putObject("report_data");
        meta.put("report_id", report.getId());
        meta.put("status", report.getStatus().name());
        meta.put("created_at", report.getCreatedAt().toString());
        meta.put("finished_at", report.getFinishedAt().toString());
    }

    @Override
    protected void writeFilterData(ObjectNode root, RequestFilter filter) {
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

    @Override
    protected void writeReportRows(ObjectNode root, Report report) {
        ArrayNode rowsArray = root.putArray("report_rows");

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
                ObjectNode rowNode = rowsArray.addObject();

                rowNode.put("rowUUID", row.getRowUUID().toString());
                rowNode.put("host", row.getHost());
                if (row.getPath() != null) {
                    rowNode.put("path", row.getPath());
                }
                rowNode.put("avg_headers", String.format("%.2f", row.getAvgHeaders()));
                rowNode.put("avg_params", String.format("%.2f", row.getAvgQueryParams()));
            }

            pagesOut = pageNumber >= page.getTotalPages();
        }
    }

    @Override
    protected void endBuilding(ObjectNode root, OutputStream outputStream) throws IOException {
        jsonMapper.writeValue(outputStream, root);
    }

    @Override
    public MimeType getMimeType() {
        return MimeType.JSON;
    }
}
