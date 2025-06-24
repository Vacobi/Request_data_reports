package axi.practice.data_generation_reports.exception;

import org.springframework.http.HttpStatus;

public class ReportFileNotFound extends BaseClientException {

    public ReportFileNotFound(Long reportId) {
        super(
                String.format("File to report with id: %d not found", reportId),
                ClientExceptionName.REPORT_FILE_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
    }
}
