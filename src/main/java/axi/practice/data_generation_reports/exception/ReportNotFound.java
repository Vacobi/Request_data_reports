package axi.practice.data_generation_reports.exception;

import org.springframework.http.HttpStatus;

public class ReportNotFound extends BaseClientException{

    public ReportNotFound(Long reportId) {
        super(
                String.format("Report with id: %d not found", reportId),
                ClientExceptionName.REPORT_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
    }
}
