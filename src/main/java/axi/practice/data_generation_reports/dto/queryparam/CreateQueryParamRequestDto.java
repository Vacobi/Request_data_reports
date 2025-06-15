package axi.practice.data_generation_reports.dto.queryparam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateQueryParamRequestDto {
    private String name;
    private String value;
}
