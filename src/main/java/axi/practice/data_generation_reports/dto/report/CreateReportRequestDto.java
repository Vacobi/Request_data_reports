package axi.practice.data_generation_reports.dto.report;

import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateReportRequestDto {

    @Builder.Default
    private CreateRequestFilterRequestDto filter = CreateRequestFilterRequestDto.builder().build();
}
