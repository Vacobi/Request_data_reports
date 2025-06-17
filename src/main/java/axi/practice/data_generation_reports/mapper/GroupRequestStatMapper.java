package axi.practice.data_generation_reports.mapper;

import axi.practice.data_generation_reports.dto.group_request_stat.GroupRequestStatDto;
import axi.practice.data_generation_reports.entity.GroupRequestStat;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface GroupRequestStatMapper {

    GroupRequestStatDto toGroupRequestStatDto(GroupRequestStat groupRequestStat);
}
