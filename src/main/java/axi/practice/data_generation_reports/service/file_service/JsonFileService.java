package axi.practice.data_generation_reports.service.file_service;

import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dao.ReportFileDao;
import axi.practice.data_generation_reports.entity.enums.MimeType;
import axi.practice.data_generation_reports.mapper.ReportFileMapper;
import axi.practice.data_generation_reports.service.ReportService;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
public class JsonFileService extends AbstractObjectNodeBuildingFileService {

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
    protected void endBuilding(ObjectNode root, OutputStream outputStream) throws IOException {
        jsonMapper.writeValue(outputStream, root);
    }

    @Override
    public MimeType getMimeType() {
        return MimeType.JSON;
    }
}
