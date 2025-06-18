package axi.practice.data_generation_reports.dto.group_request_stat;

import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetGroupRequestStatsDto {
    private CreateRequestFilterRequestDto requestFilter;
    private int page;
}
