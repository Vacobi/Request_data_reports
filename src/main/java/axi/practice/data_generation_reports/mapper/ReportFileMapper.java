package axi.practice.data_generation_reports.mapper;

import axi.practice.data_generation_reports.dto.report_file.ReportFileDto;
import axi.practice.data_generation_reports.entity.ReportFile;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface ReportFileMapper {

    default ReportFileDto toReportFileDto(ReportFile reportFile) {
        return ReportFileDto.builder()
                .id(reportFile.getId())
                .reportId(reportFile.getReport().getId())
                .fileName(reportFile.getFileName())
                .filePath(reportFile.getFilePath())
                .storageType(reportFile.getStorageType())
                .mimeType(reportFile.getMimeType())
                .fileData(reportFile.getFileData())
                .createdAt(reportFile.getCreatedAt())
                .build();
    }
}
