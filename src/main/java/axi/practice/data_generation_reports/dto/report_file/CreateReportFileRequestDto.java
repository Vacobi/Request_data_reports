package axi.practice.data_generation_reports.dto.report_file;

import axi.practice.data_generation_reports.entity.enums.MimeType;
import axi.practice.data_generation_reports.entity.enums.StorageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateReportFileRequestDto {
    private Long reportId;
    private StorageType storageType;
    private MimeType mimeType;
}
