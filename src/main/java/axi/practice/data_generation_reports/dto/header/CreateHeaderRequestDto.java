package axi.practice.data_generation_reports.dto.header;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateHeaderRequestDto {
    private String name;
    private String value;
}
