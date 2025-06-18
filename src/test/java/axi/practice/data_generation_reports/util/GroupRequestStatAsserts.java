package axi.practice.data_generation_reports.util;

import axi.practice.data_generation_reports.dto.group_request_stat.GroupRequestStatDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupRequestStatAsserts {

    public static void assertGroupRequestStatsDtoEquals(GroupRequestStatDto expected, GroupRequestStatDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getHost(), actual.getHost());
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getAvgHeaders(), actual.getAvgHeaders());
        assertEquals(expected.getAvgQueryParams(), actual.getAvgQueryParams());
    }
}
