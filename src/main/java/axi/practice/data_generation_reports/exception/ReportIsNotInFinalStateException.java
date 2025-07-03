package axi.practice.data_generation_reports.exception;

import org.springframework.http.HttpStatus;

public class ReportIsNotInFinalStateException extends BaseClientException {

    public ReportIsNotInFinalStateException(Long reportId) {
        super(
                String.format("Report with id: %d not in final state. Wait until file is fully processed", reportId),
                ClientExceptionName.REPORT_NOT_IN_FINAL_STATE,
                HttpStatus.LOCKED
        );
    }
}
