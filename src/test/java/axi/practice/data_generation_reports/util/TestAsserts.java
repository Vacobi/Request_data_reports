package axi.practice.data_generation_reports.util;

import axi.practice.data_generation_reports.dto.header.CreateHeaderRequestDto;
import axi.practice.data_generation_reports.dto.header.HeaderDto;
import axi.practice.data_generation_reports.dto.queryparam.CreateQueryParamRequestDto;
import axi.practice.data_generation_reports.dto.queryparam.QueryParamDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.dto.request.RequestDto;
import axi.practice.data_generation_reports.entity.Header;
import axi.practice.data_generation_reports.entity.QueryParam;
import axi.practice.data_generation_reports.entity.Request;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestAsserts {

    public static void assertCreateRequestDtoEquals(CreateRequestDto expected, CreateRequestDto actual) {
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getBody(), actual.getBody());
        assertCreateHeaderRequestsDtoEquals(expected.getHeaders(), actual.getHeaders());
        assertCreateQueryParamsDtoEquals(expected.getVariableParams(), actual.getVariableParams());
    }

    public static void assertCreateHeaderRequestsDtoEquals(List<CreateHeaderRequestDto> expected, List<CreateHeaderRequestDto> actual) {
        assertEquals(expected.size(), actual.size());
        IntStream.range(0, expected.size())
                .forEach(i -> assertCreateHeaderRequestDtoEquals(expected.get(i), actual.get(i)));
    }

    public static void assertCreateHeaderRequestDtoEquals(CreateHeaderRequestDto expected, CreateHeaderRequestDto actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
    }

    public static void assertCreateQueryParamsDtoEquals(List<CreateQueryParamRequestDto> expected, List<CreateQueryParamRequestDto> actual) {
        assertEquals(expected.size(), actual.size());
        IntStream.range(0, expected.size())
                .forEach(i -> assertCreateQueryParamDtoEquals(expected.get(i), actual.get(i)));
    }

    public static void assertCreateQueryParamDtoEquals(CreateQueryParamRequestDto expected, CreateQueryParamRequestDto actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
    }


    public static void assertRequestEquals(Request expected, Request actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getBody(), actual.getBody());
        assertNotNull(actual.getTimestamp());
        assertLocalDateTimeEquals(expected.getTimestamp(), actual.getTimestamp());
        assertHeadersEquals(expected.getHeaders(), actual.getHeaders());
        assertQueryParamsEquals(expected.getQueryParams(), actual.getQueryParams());
    }


    public static void assertRequestDtoEquals(RequestDto expected, RequestDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getBody(), actual.getBody());
        assertNotNull(actual.getTimestamp());
        assertLocalDateTimeEquals(expected.getTimestamp(), actual.getTimestamp());
        assertHeaderDtosEquals(expected.getHeaders(), actual.getHeaders());
        assertQueryParamsDtosEquals(expected.getVariableParams(), actual.getVariableParams());
    }


    public static void assertQueryParamsEquals(List<QueryParam> expected, List<QueryParam> actual) {
        assertEquals(expected.size(), actual.size());
        IntStream.range(0, expected.size())
                .forEach(i -> assertQueryParamEquals(expected.get(i), actual.get(i)));
    }

    public static void assertQueryParamEquals(QueryParam expected, QueryParam actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
        assertEquals(expected.getRequest(), actual.getRequest());
    }

    public static void assertQueryParamsDtosEquals(List<QueryParamDto> expected, List<QueryParamDto> actual) {
        assertEquals(expected.size(), actual.size());
        IntStream.range(0, expected.size())
                .forEach(i -> assertQueryParamDtoEquals(expected.get(i), actual.get(i)));
    }

    public static void assertQueryParamDtoEquals(QueryParamDto expected, QueryParamDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
    }


    public static void assertHeadersEquals(List<Header> expected, List<Header> actual) {
        assertEquals(expected.size(), actual.size());
        IntStream.range(0, expected.size())
                .forEach(i -> assertHeaderEquals(expected.get(i), actual.get(i)));
    }

    public static void assertHeaderEquals(Header expected, Header actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
        assertEquals(expected.getRequest(), actual.getRequest());
    }

    public static void assertHeaderDtosEquals(List<HeaderDto> expected, List<HeaderDto> actual) {
        assertEquals(expected.size(), actual.size());
        IntStream.range(0, expected.size())
                .forEach(i -> assertHeaderDtoEquals(expected.get(i), actual.get(i)));
    }

    public static void assertHeaderDtoEquals(HeaderDto expected, HeaderDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
    }

    public static void assertLocalDateTimeEquals(LocalDateTime expected, LocalDateTime actual) {
        if (expected != null && actual != null) {
            assertTrue(localDateTimeAreEquals(expected, actual));
        } else if (expected == null && actual == null) {
            assertTrue(true);
        } else {
            fail();
        }
    }

    public static void assertBothNotNull(Object object, Object otherObject) {
        assertNotNull(object);
        assertNotNull(otherObject);
    }

    public static boolean localDateTimeAreEquals(
            LocalDateTime first,
            LocalDateTime second
    ) {
        return localDateTimeAreEquals(first, second, ChronoUnit.SECONDS, 1);
    }

    private static boolean localDateTimeAreEquals(
            LocalDateTime first,
            LocalDateTime second,
            ChronoUnit precisionUnit,
            long precision
    ) {
        if (precision < 0) {
            throw new IllegalArgumentException("Precision must be greater than 0");
        }

        if (first == second) {
            return true;
        }

        return Duration.between(first, second).get(precisionUnit) < precision;
    }
}
