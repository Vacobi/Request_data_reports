package axi.practice.data_generation_reports.controller;

import axi.practice.data_generation_reports.dto.report.*;
import axi.practice.data_generation_reports.dto.report_file.ReportFileDto;
import axi.practice.data_generation_reports.entity.enums.StorageType;
import axi.practice.data_generation_reports.service.ReportService;
import axi.practice.data_generation_reports.service.file_service.CsvFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    private final CsvFileService csvFileService;

    @PostMapping
    public GenerateReportResponseDto generateReport(
            @RequestBody CreateReportRequestDto requestDto
    ) {
        Long reportId = reportService.generateReport(requestDto);

        return GenerateReportResponseDto.builder()
                .reportId(reportId)
                .build();
    }

    @GetMapping("/completed/{reportId}")
    public IsReportCompletedResponseDto isReportCompleted(@PathVariable("reportId") Long reportId) {

        return IsReportCompletedResponseDto.builder()
                .completed(reportService.completed(reportId))
                .build();
    }

    @GetMapping("/status/{reportId}")
    public GetReportStatusResponseDto getReportStatus(@PathVariable("reportId") Long reportId) {

        return GetReportStatusResponseDto.builder()
                .status(reportService.getStatus(reportId))
                .build();
    }

    @GetMapping("{reportId}")
    public ReportPageResponseDto getReport(
            @PathVariable("reportId") Long reportId,
            @RequestParam(defaultValue = "0") int page
    ) {
        GetReportPageRequestDto request = GetReportPageRequestDto.builder()
                .reportId(reportId)
                .page(page)
                .build();

        return reportService.getReport(request);
    }

    @GetMapping
    public Page<ReportDataDto> getReports(@RequestParam(defaultValue = "0") int page) {
        return reportService.getReports(page);
    }

    @PostMapping("/file/{reportId}")
    public ReportFileDto generateReportCsvFile(
            @PathVariable("reportId") Long reportId,
            @RequestParam(defaultValue = "DISK") StorageType storageType
            ) {
        return csvFileService.createReportFile(reportId, storageType);
    }
}
