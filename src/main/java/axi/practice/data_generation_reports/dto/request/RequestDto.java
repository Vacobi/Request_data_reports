package axi.practice.data_generation_reports.dto.request;

import axi.practice.data_generation_reports.dto.header.HeaderDto;
import axi.practice.data_generation_reports.dto.queryparam.QueryParamDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RequestDto {
    private Long id;
    private String url;
    private String path;
    private String body;
    private LocalDateTime timestamp;

    @Builder.Default
    private List<HeaderDto> headers = new LinkedList<>();

    @Builder.Default
    private List<QueryParamDto> variableParams = new LinkedList<>();
}
