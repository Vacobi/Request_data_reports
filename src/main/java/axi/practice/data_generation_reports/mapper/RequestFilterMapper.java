package axi.practice.data_generation_reports.mapper;

import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.filter.RequestFilterDto;
import axi.practice.data_generation_reports.entity.RequestFilter;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface RequestFilterMapper {

    @Mapping(target = "id", ignore = true)
    RequestFilter toRequestFilter(CreateRequestFilterRequestDto requestDto);

    RequestFilterDto toRequestFilterDto(RequestFilter requestFilter);

    RequestFilter toRequestFilter(RequestFilterDto requestFilter);
}
