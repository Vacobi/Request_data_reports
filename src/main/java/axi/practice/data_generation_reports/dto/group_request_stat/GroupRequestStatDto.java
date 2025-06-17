package axi.practice.data_generation_reports.dto.group_request_stat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GroupRequestStatDto {
    private UUID id;
    private String host;
    private String path;
    private int avgHeaders;
    private int avgQueryParams;
}
