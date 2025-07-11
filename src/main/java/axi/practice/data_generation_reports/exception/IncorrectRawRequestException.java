package axi.practice.data_generation_reports.exception;

import org.springframework.http.HttpStatus;

public class IncorrectRawRequestException extends BaseClientException {

    public IncorrectRawRequestException(String rawRequest) {
        super(
                String.format("Incorrect raw request: '%s'", rawRequest),
                ClientExceptionName.INVALID_RAW_REQUEST,
                HttpStatus.BAD_REQUEST
        );
    }
}
