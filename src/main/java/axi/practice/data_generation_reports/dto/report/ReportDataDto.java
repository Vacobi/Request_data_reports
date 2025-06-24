package axi.practice.data_generation_reports.dto.report;

import axi.practice.data_generation_reports.dto.filter.RequestFilterDto;
import axi.practice.data_generation_reports.dto.report_file.ReportFileDto;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReportDataDto {
    private Long id;
    private RequestFilterDto filter;
    private ReportFileDto reportFile;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
}
