package axi.practice.data_generation_reports.exception;

import org.springframework.http.HttpStatus;

public class CanNotCreateDirectoryException extends BaseClientException {

    public CanNotCreateDirectoryException(String directory) {
        super(
                String.format("Can not create directory: '%s'", directory),
                ClientExceptionName.CAN_NOT_CREATE_DIRECTORY,
                HttpStatus.BAD_REQUEST
        );
    }
}
