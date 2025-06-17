package axi.practice.data_generation_reports.exception;

import lombok.Getter;

@Getter
public enum ClientExceptionName {
    VALIDATION_EXCEPTION(800),
    GROUP_VALIDATION_EXCEPTION(801),

    INVALID_HEADER(200),
    INVALID_QUERY_PARAM(201),
    INVALID_REQUEST(202),
    INVALID_RAW_REQUEST(203),
    INVALID_REQUEST_FILTER(204);

    private final int apiErrorCode;

    ClientExceptionName(int apiErrorCode) {
        this.apiErrorCode = apiErrorCode;
    }
}