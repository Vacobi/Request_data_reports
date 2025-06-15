package axi.practice.data_generation_reports.mapper;

import axi.practice.data_generation_reports.dto.header.CreateHeaderRequestDto;
import axi.practice.data_generation_reports.dto.header.HeaderDto;
import axi.practice.data_generation_reports.entity.Header;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface HeaderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    Header toHeader(CreateHeaderRequestDto requestDto);

    HeaderDto toHeaderDto(Header persisted);

    @Mapping(target = "request", ignore = true)
    Header toHeader(HeaderDto persisted);
}
