package axi.practice.data_generation_reports.exception;

import org.springframework.http.HttpStatus;

public class ReportFileAlreadyExists extends BaseClientException {

    public ReportFileAlreadyExists(Long reportId, String path) {
        super(
                String.format("Report with id: %d already write in file. Path: %s", reportId, path),
                ClientExceptionName.REPORT_FILE_ALREADY_EXISTS,
                HttpStatus.CONFLICT
        );
    }
}
