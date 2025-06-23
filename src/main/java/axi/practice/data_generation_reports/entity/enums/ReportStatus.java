package axi.practice.data_generation_reports.entity.enums;

public enum ReportStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED;

    public static boolean isFinalState(ReportStatus status) {
        return status == COMPLETED || status == FAILED;
    }
}
