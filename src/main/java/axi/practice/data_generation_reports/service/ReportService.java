package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dao.ReportRowDao;
import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GetGroupRequestStatsDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GroupRequestStatDto;
import axi.practice.data_generation_reports.dto.report.CreateReportRequestDto;
import axi.practice.data_generation_reports.dto.report.GetReportPageRequestDto;
import axi.practice.data_generation_reports.dto.report.ReportDataDto;
import axi.practice.data_generation_reports.dto.report.ReportPageResponseDto;
import axi.practice.data_generation_reports.dto.report_row.ReportRowDto;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.ReportRow;
import axi.practice.data_generation_reports.entity.RequestFilter;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import axi.practice.data_generation_reports.exception.ReportNotFound;
import axi.practice.data_generation_reports.mapper.ReportMapper;
import axi.practice.data_generation_reports.mapper.ReportRowMapper;
import axi.practice.data_generation_reports.mapper.RequestFilterMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final int dataPageSize;

    private final RequestFilterMapper requestFilterMapper;
    private final ReportRowMapper reportRowMapper;

    private final RequestFilterService requestFilterService;
    private final GroupRequestStatsService groupRequestStatsService;

    private final ReportDao reportDao;
    private final ReportRowDao reportRowDao;
    private final ReportMapper reportMapper;

    @Async
    public CompletableFuture<Long> generateReport(CreateReportRequestDto requestDto) {
        final Report initialReport = reportDao.save(
                Report.builder().status(ReportStatus.PENDING).build()
        );

        CompletableFuture.supplyAsync(() -> initialReport)
                .thenApply(report -> updateStatus(report, ReportStatus.PROCESSING))
                .thenApply(report -> addFilter(report, requestDto.getFilter()))
                .thenApply(report -> generateRows(requestDto.getFilter(), report))
                .handle((report, e) -> {
                    if (e != null) {
                        updateStatus(initialReport, ReportStatus.FAILED);
                        throw unwrapCompletionException(e);
                    }
                    return finalizeReport(report);
                });

        return CompletableFuture.completedFuture(initialReport.getId());
    }

    private Report updateStatus(Report report, ReportStatus status) {
        report.setFinishedAt(LocalDateTime.now());
        report.setStatus(status);
        return reportDao.save(report);
    }

    private Report addFilter(Report report, CreateRequestFilterRequestDto filterDto) {
        RequestFilter filter = requestFilterMapper.toRequestFilter(
                requestFilterService.createFilter(filterDto)
        );
        report.setFilter(filter);
        return reportDao.save(report);
    }

    private Report generateRows(CreateRequestFilterRequestDto filterRequestDto, Report report) {
        boolean pagesOut = false;
        int pageNumber = 0;
        while (!pagesOut) {
            GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                    .requestFilter(filterRequestDto)
                    .page(pageNumber++)
                    .build();

            Page<GroupRequestStatDto> pageOfGroupRequestStats = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);

            List<ReportRow> pageReportRows = reportRowMapper.toReportRows(pageOfGroupRequestStats.getContent(), report);
            report.addRows(pageReportRows);

            pagesOut = pageNumber >= pageOfGroupRequestStats.getTotalPages();
        }

        return reportDao.save(report);
    }

    private Long finalizeReport(Report report) {
        report.setFinishedAt(LocalDateTime.now());
        report.setStatus(ReportStatus.COMPLETED);
        report = reportDao.save(report);
        return report.getId();
    }

    private RuntimeException unwrapCompletionException(Throwable e) {
        Throwable cause = e instanceof CompletionException ? e.getCause() : e;
        return cause instanceof RuntimeException ?
                (RuntimeException) cause : new CompletionException(cause);
    }

    public ReportStatus getStatus(Long reportId) {
        Optional<ReportStatus> optionalStatus = reportDao.getStatus(reportId);

        if (optionalStatus.isEmpty()) {
            throw new ReportNotFound(reportId);
        }

        return optionalStatus.get();
    }

    public boolean completed(Long reportId) {
        Optional<ReportStatus> optionalStatus = reportDao.getStatus(reportId);

        if (optionalStatus.isEmpty()) {
            throw new ReportNotFound(reportId);
        }

        return optionalStatus.get() == ReportStatus.COMPLETED;
    }

    @Transactional
    public ReportPageResponseDto getReport(GetReportPageRequestDto requestDto) {

        Optional<Report> optionalReport = reportDao.findById(requestDto.getReportId());
        if (optionalReport.isEmpty()) {
            throw new ReportNotFound(requestDto.getReportId());
        }
        Report report = optionalReport.get();

        Pageable pageable = PageRequest.of(requestDto.getPage(), dataPageSize);
        Page<ReportRow> reportRowPage = reportRowDao.findByReport(report, pageable);

        Page<ReportRowDto> reportRowDtoPage = reportRowPage.map(reportRowMapper::toReportRowDto);
        return ReportPageResponseDto.builder()
                .reportId(report.getId())
                .filter(requestFilterMapper.toRequestFilterDto(report.getFilter()))
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .finishedAt(report.getFinishedAt())
                .rows(reportRowDtoPage)
                .build();
    }

    @Transactional
    public Page<ReportDataDto> getReports(int page) {
        Pageable pageable = PageRequest.of(page, dataPageSize);

        Page<Report> reportPage = reportDao.findAll(pageable);

        return reportPage.map(reportMapper::toReportDataDto);
    }
}