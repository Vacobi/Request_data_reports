package axi.practice.data_generation_reports.util;

import axi.practice.data_generation_reports.dto.report_file.ReportFileDto;
import axi.practice.data_generation_reports.entity.ReportFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileAsserts {

    public static void assertReportFilesDtoEquals(ReportFileDto actualDto, ReportFileDto expectedDto) {
        assertEquals(actualDto.getId(), expectedDto.getId());
        assertEquals(actualDto.getReportId(), expectedDto.getReportId());
        assertEquals(actualDto.getFileName(), expectedDto.getFileName());
        assertEquals(actualDto.getFilePath(), expectedDto.getFilePath());
        assertEquals(actualDto.getMimeType(), expectedDto.getMimeType());
        assertEquals(actualDto.getFileData(), expectedDto.getFileData());
        assertEquals(actualDto.getCreatedAt(), expectedDto.getCreatedAt());
    }

    public static void assertReportFilesEquals(ReportFile actualDto, ReportFile expectedDto) {
        assertEquals(actualDto.getId(), expectedDto.getId());
        assertEquals(actualDto.getReport().getId(), expectedDto.getReport().getId());
        assertEquals(actualDto.getFileName(), expectedDto.getFileName());
        assertEquals(actualDto.getFilePath(), expectedDto.getFilePath());
        assertEquals(actualDto.getMimeType(), expectedDto.getMimeType());
        assertEquals(actualDto.getFileData(), expectedDto.getFileData());
        assertEquals(actualDto.getCreatedAt(), expectedDto.getCreatedAt());
    }
}
