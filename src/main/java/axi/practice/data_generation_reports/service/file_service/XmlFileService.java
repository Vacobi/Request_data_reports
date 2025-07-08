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
public class XmlFileService extends AbstractObjectNodeBuildingFileService {

    private final XmlMapper xmlMapper;

    public XmlFileService(
            ReportService reportService,
            ReportDao reportDao,
            ReportFileDao reportFileDao,
            ReportFileMapper reportFileMapper,
            String reportsDirectory) {
        super(reportService, reportDao, reportFileDao, reportFileMapper, reportsDirectory);

        this.xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected ObjectNode createBuilder(OutputStream outputStream) {
        return xmlMapper.createObjectNode();
    }

    @Override
    protected void endBuilding(ObjectNode root, OutputStream outputStream) throws IOException {
        xmlMapper.writer()
                .withRootName("report")
                .writeValue(outputStream, root);
    }

    @Override
    public MimeType getMimeType() {
        return MimeType.XML;
    }
}
