package axi.practice.data_generation_reports.exception;

import org.springframework.http.HttpStatus;

public class CanNotGenerateFileException extends BaseClientException {

    public CanNotGenerateFileException(String file) {
        super(
                String.format("Can not generate file with name: '%s'", file),
                ClientExceptionName.CAN_NOT_CREATE_FILE,
                HttpStatus.BAD_REQUEST
        );
    }
}
