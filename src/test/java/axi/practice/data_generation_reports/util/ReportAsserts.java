package axi.practice.data_generation_reports.util;

import axi.practice.data_generation_reports.dto.report.ReportDto;
import axi.practice.data_generation_reports.dto.report.ReportPageResponseDto;
import axi.practice.data_generation_reports.dto.report_row.ReportRowDto;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.ReportRow;

import java.util.List;

import static axi.practice.data_generation_reports.util.RequestFilterTestAsserts.assertRequestFilterDtoEquals;
import static axi.practice.data_generation_reports.util.RequestFilterTestAsserts.assertRequestFilterEquals;
import static axi.practice.data_generation_reports.util.TestAsserts.assertLocalDateTimeEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportAsserts {

    public static void assertReportPageDtoEquals(ReportPageResponseDto expected, ReportPageResponseDto actual) {
        assertEquals(expected.getReportId(), actual.getReportId());
        assertRequestFilterDtoEquals(expected.getFilter(), actual.getFilter());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertLocalDateTimeEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertLocalDateTimeEquals(expected.getFinishedAt(), actual.getFinishedAt());
        assertReportRowsDtoEquals(expected.getRows().getContent(), actual.getRows().getContent());
    }

    public static void assertReportEquals(Report expected, Report actual) {
        assertEquals(expected.getId(), actual.getId());
        assertRequestFilterEquals(expected.getFilter(), actual.getFilter());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertLocalDateTimeEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertLocalDateTimeEquals(expected.getFinishedAt(), actual.getFinishedAt());
        assertReportRowsEquals(expected.getRows(), actual.getRows());
    }

    public static void assertReportRowsEquals(List<ReportRow> expected, List<ReportRow> actual) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            ReportRow expectedCurrent = expected.get(i);
            ReportRow actualCurrent = actual.stream()
                    .filter(entity -> entity.getHost().equals(expectedCurrent.getHost()))
                    .findFirst().get();

            assertReportRowEquals(expectedCurrent, actualCurrent);
        }
    }

    public static void assertReportRowEquals(ReportRow expected, ReportRow actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getRowUUID(), actual.getRowUUID());
        assertEquals(expected.getHost(), actual.getHost());
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getAvgHeaders(), actual.getAvgHeaders());
        assertEquals(expected.getAvgQueryParams(), actual.getAvgQueryParams());
    }

    public static void assertReportDtoEquals(ReportDto expected, ReportDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertRequestFilterDtoEquals(expected.getFilter(), actual.getFilter());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertLocalDateTimeEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertLocalDateTimeEquals(expected.getFinishedAt(), actual.getFinishedAt());
        assertReportRowsDtoEquals(expected.getRows(), actual.getRows());
    }

    public static void assertReportRowsDtoEquals(List<ReportRowDto> expected, List<ReportRowDto> actual) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            ReportRowDto expectedCurrent = expected.get(i);
            ReportRowDto actualCurrent = actual.stream()
                    .filter(dto -> dto.getHost().equals(expectedCurrent.getHost()))
                    .findFirst().get();

            assertReportRowDtoEquals(expectedCurrent, actualCurrent);
        }
    }

    public static void assertReportRowDtoEquals(ReportRowDto expected, ReportRowDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getRowUUID(), actual.getRowUUID());
        assertEquals(expected.getHost(), actual.getHost());
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getAvgHeaders(), actual.getAvgHeaders());
        assertEquals(expected.getAvgQueryParams(), actual.getAvgQueryParams());
    }
}
