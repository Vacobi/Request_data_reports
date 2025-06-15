package axi.practice.data_generation_reports.mapper;

import axi.practice.data_generation_reports.dto.queryparam.CreateQueryParamRequestDto;
import axi.practice.data_generation_reports.dto.queryparam.QueryParamDto;
import axi.practice.data_generation_reports.entity.QueryParam;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface QueryParamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    QueryParam toQueryParam(CreateQueryParamRequestDto requestDto);

    QueryParamDto toQueryParamDto(QueryParam persisted);

    @Mapping(target = "request", ignore = true)
    QueryParam toQueryParam(QueryParamDto persisted);
}
