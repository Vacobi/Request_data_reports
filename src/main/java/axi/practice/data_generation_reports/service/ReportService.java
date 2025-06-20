package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GetGroupRequestStatsDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GroupRequestStatDto;
import axi.practice.data_generation_reports.dto.report.CreateReportRequestDto;
import axi.practice.data_generation_reports.dto.report.ReportDto;
import axi.practice.data_generation_reports.entity.Report;
import axi.practice.data_generation_reports.entity.ReportRow;
import axi.practice.data_generation_reports.entity.RequestFilter;
import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import axi.practice.data_generation_reports.mapper.ReportMapper;
import axi.practice.data_generation_reports.mapper.ReportRowMapper;
import axi.practice.data_generation_reports.mapper.RequestFilterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final RequestFilterMapper requestFilterMapper;
    private final ReportMapper reportMapper;
    private final ReportRowMapper reportRowMapper;

    private final RequestFilterService requestFilterService;
    private final GroupRequestStatsService groupRequestStatsService;

    private final ReportDao reportDao;

    @Async
    public CompletableFuture<ReportDto> generateReport(CreateReportRequestDto requestDto) {
        final Report initialReport = reportDao.save(
                Report.builder().status(ReportStatus.PENDING).build()
        );

        return CompletableFuture.supplyAsync(() -> initialReport)
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
            report = reportDao.save(report);

            pagesOut = pageNumber >= pageOfGroupRequestStats.getTotalPages();
        }

        return report;
    }

    private ReportDto finalizeReport(Report report) {
        report.setFinishedAt(LocalDateTime.now());
        report.setStatus(ReportStatus.COMPLETED);
        return reportMapper.toReportDto(reportDao.save(report));
    }

    private RuntimeException unwrapCompletionException(Throwable e) {
        Throwable cause = e instanceof CompletionException ? e.getCause() : e;
        return cause instanceof RuntimeException ?
                (RuntimeException) cause : new CompletionException(cause);
    }
}