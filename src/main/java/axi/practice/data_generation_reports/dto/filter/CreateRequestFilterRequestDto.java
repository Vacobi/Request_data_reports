package axi.practice.data_generation_reports.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateRequestFilterRequestDto {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String host;
    private String path;
    private Double avgHeaders;
    private Double avgQueryParams;
}
