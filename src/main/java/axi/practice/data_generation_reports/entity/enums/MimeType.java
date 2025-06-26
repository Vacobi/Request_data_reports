package axi.practice.data_generation_reports.entity.enums;

public enum MimeType {
    CSV,
    JSON,
    XML,
    HTML;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
