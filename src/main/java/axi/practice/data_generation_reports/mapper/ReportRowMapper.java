package axi.practice.data_generation_reports.mapper;

import axi.practice.data_generation_reports.dto.group_request_stat.GroupRequestStatDto;
import axi.practice.data_generation_reports.dto.report_row.CreateReportRowRequestDto;
import axi.practice.data_generation_reports.dto.report_row.ReportRowDto;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.ReportRow;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.LinkedList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface ReportRowMapper {

    @Mapping(target="id", ignore = true)
    ReportRow toReportRow(CreateReportRowRequestDto request, Report report);

    ReportRowDto toReportRowDto(ReportRow persisted);

    default ReportRow toReportRow(GroupRequestStatDto groupRequestStatDto, Report report) {
        return ReportRow.builder()
                .rowUUID(groupRequestStatDto.getId())
                .report(report)
                .host(groupRequestStatDto.getHost())
                .path(groupRequestStatDto.getPath())
                .avgHeaders(groupRequestStatDto.getAvgHeaders())
                .avgQueryParams(groupRequestStatDto.getAvgQueryParams())
                .build();
    }

    default List<ReportRow> toReportRows(List<GroupRequestStatDto> groupRequestStatDto, Report reportEntity) {
        List<ReportRow> reportRows = new LinkedList<>();

        groupRequestStatDto.forEach(g -> reportRows.add(toReportRow(g, reportEntity)));

        return reportRows;
    }
}
