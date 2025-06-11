package axi.practice.data_generation_reports.exception;

import lombok.Getter;

@Getter
public enum ClientExceptionName {
    VALIDATION_EXCEPTION(800),
    GROUP_VALIDATION_EXCEPTION(801);

    private final int apiErrorCode;

    ClientExceptionName(int apiErrorCode) {
        this.apiErrorCode = apiErrorCode;
    }
}