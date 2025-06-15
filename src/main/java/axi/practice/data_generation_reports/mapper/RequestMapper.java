package axi.practice.data_generation_reports.mapper;

import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.dto.request.RequestDto;
import axi.practice.data_generation_reports.entity.Request;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface RequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "queryParams", expression = "java(new java.util.LinkedList<>())")
    @Mapping(target = "headers", expression = "java(new java.util.LinkedList<>())")
    Request toRequest(CreateRequestDto requestDto);

    @Mapping(source = "queryParams", target = "variableParams")
    RequestDto toRequestDto(Request persisted);
}