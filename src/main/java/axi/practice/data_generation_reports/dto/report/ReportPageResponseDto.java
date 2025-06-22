package axi.practice.data_generation_reports.dto.report;

import axi.practice.data_generation_reports.dto.filter.RequestFilterDto;
import axi.practice.data_generation_reports.dto.report_row.ReportRowDto;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReportPageResponseDto {
    private Long reportId;
    private RequestFilterDto filter;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private Page<ReportRowDto> rows;
}
