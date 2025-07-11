package axi.practice.data_generation_reports.mapper;

import axi.practice.data_generation_reports.dto.report.ReportDataDto;
import axi.practice.data_generation_reports.dto.report.ReportDto;
import axi.practice.data_generation_reports.entity.Report;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {ReportRowMapper.class, ReportFileMapper.class}
)
public interface ReportMapper {

    ReportDto toReportDto(Report report);

    ReportDataDto toReportDataDto(Report report);
}
