package axi.practice.data_generation_reports.util;

import axi.practice.data_generation_reports.dto.filter.RequestFilterDto;
import axi.practice.data_generation_reports.entity.RequestFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestFilterTestAsserts {

    public static void assertRequestFilterEquals(RequestFilter expected, RequestFilter actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getFromDate(), actual.getFromDate());
        assertEquals(expected.getToDate(), actual.getToDate());
        assertEquals(expected.getHost(), actual.getHost());
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getAvgHeaders(), actual.getAvgHeaders());
        assertEquals(expected.getAvgQueryParams(), actual.getAvgQueryParams());
    }

    public static void assertRequestFilterDtoEquals(RequestFilterDto expected, RequestFilterDto actual) {
        assertEquals(expected.getFromDate(), actual.getFromDate());
        assertEquals(expected.getToDate(), actual.getToDate());
        assertEquals(expected.getHost(), actual.getHost());
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getAvgHeaders(), actual.getAvgHeaders());
        assertEquals(expected.getAvgQueryParams(), actual.getAvgQueryParams());
    }
}
