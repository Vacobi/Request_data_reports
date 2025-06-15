package axi.practice.data_generation_reports.dto.request;

import axi.practice.data_generation_reports.dto.header.CreateHeaderRequestDto;
import axi.practice.data_generation_reports.dto.queryparam.CreateQueryParamRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateRequestDto {
    private String url;
    private String path;
    private String body;

    @Builder.Default
    private List<CreateHeaderRequestDto> headers = new LinkedList<>();

    @Builder.Default
    private List<CreateQueryParamRequestDto> variableParams = new LinkedList<>();
}
