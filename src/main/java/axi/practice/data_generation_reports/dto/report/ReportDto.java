package axi.practice.data_generation_reports.dto.report;

import axi.practice.data_generation_reports.dto.filter.RequestFilterDto;
import axi.practice.data_generation_reports.dto.report_file.ReportFileDto;
import axi.practice.data_generation_reports.dto.report_row.ReportRowDto;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReportDto {
    private Long id;
    private RequestFilterDto filter;
    private Set<ReportFileDto> reportFiles;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
    private List<ReportRowDto> rows;
}
