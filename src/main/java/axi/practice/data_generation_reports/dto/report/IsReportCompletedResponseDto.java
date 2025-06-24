package axi.practice.data_generation_reports.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class IsReportCompletedResponseDto {
    private boolean completed;

    @Builder.Default
    private LocalDate requestTime = LocalDate.now();
}
