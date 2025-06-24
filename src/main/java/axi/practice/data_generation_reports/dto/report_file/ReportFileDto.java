package axi.practice.data_generation_reports.dto.report_file;

import axi.practice.data_generation_reports.entity.enums.StorageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReportFileDto {
    private Long id;
    private Long reportId;
    private String fileName;
    private String filePath;
    private StorageType storageType;
    private String mimeType;
    private byte[] fileData;
    private LocalDateTime createdAt;
}
