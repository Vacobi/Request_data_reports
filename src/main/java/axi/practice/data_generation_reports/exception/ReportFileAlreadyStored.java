package axi.practice.data_generation_reports.exception;

import org.springframework.http.HttpStatus;

public class ReportFileAlreadyStored extends BaseClientException {

    public ReportFileAlreadyStored(Long reportId, Long fileId) {
        super(
                String.format("Report with id: %d already has file with id: %d", reportId, fileId),
                ClientExceptionName.REPORT_FILE_ALREADY_EXISTS,
                HttpStatus.CONFLICT
        );
    }

    public ReportFileAlreadyStored(String directory) {
        super(
                String.format("Directory already exists", directory),
                ClientExceptionName.REPORT_FILE_ALREADY_EXISTS,
                HttpStatus.CONFLICT
        );
    }
}
