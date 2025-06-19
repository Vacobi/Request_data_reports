package axi.practice.data_generation_reports.dto.report_row;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateReportRowRequestDto {
    private UUID rowUUID;
    private String host;
    private String path;
    private double avgHeaders;
    private double avgQueryParams;
}
