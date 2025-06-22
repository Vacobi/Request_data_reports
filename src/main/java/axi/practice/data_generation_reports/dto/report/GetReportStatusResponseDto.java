package axi.practice.data_generation_reports.dto.report;

import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetReportStatusResponseDto {
    private ReportStatus status;

    @Builder.Default
    private LocalDate requestTime = LocalDate.now();
}
