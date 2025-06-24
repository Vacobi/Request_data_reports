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
    INVALID_REQUEST_FILTER(204),

    REPORT_NOT_FOUND(300),
    REPORT_NOT_IN_FINAL_STATE(301),

    CAN_NOT_CREATE_DIRECTORY(400),
    CAN_NOT_CREATE_FILE(401),
    REPORT_FILE_ALREADY_EXISTS(402),
    REPORT_FILE_NOT_FOUND(403);

    private final int apiErrorCode;

    ClientExceptionName(int apiErrorCode) {
        this.apiErrorCode = apiErrorCode;
    }
}