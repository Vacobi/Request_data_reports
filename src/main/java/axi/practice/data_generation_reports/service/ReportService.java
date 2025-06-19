package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.dao.ReportDao;
import axi.practice.data_generation_reports.dto.filter.RequestFilterDto;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    @Transactional
    public CompletableFuture<ReportDto> generateReport(CreateReportRequestDto requestDto) {

        Report report = Report.builder()
                .status(ReportStatus.PENDING)
                .build();

        CompletableFuture<Report> reportFuture = CompletableFuture.supplyAsync(() -> reportDao.save(report));

        try {
            reportFuture = reportFuture.thenApply(currentReport -> {
                currentReport.setStatus(ReportStatus.PROCESSING);
                currentReport = reportDao.save(currentReport);
                return currentReport;
            });

            CompletableFuture<Report> reportFutureWithFilters = reportFuture.thenApply(currentReport -> {
                RequestFilterDto filterDto = requestFilterService.createFilter(requestDto.getFilter());
                RequestFilter filter = requestFilterMapper.toRequestFilter(filterDto);

                currentReport.setFilter(filter);
                return reportDao.save(currentReport);
            });

            CompletableFuture<Report> requestWithRows = reportFutureWithFilters.thenApply(currentReport -> {
                boolean pagesOut = false;
                int pageNumber = 0;
                while (!pagesOut) {
                    GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                            .requestFilter(requestDto.getFilter())
                            .page(pageNumber++)
                            .build();

                    Page<GroupRequestStatDto> pageOfGroupRequestStats = groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);

                    List<ReportRow> pageReportRows = reportRowMapper.toReportRows(pageOfGroupRequestStats.getContent(), currentReport);
                    currentReport.addRows(pageReportRows);
                    currentReport = reportDao.save(currentReport);

                    pagesOut = pageOfGroupRequestStats.getTotalPages() >= pageNumber;
                }

                return currentReport;
            });

            return requestWithRows.thenApply(currentReport -> {
                currentReport.setFinishedAt(LocalDateTime.now());
                currentReport.setStatus(ReportStatus.COMPLETED);
                currentReport = reportDao.save(currentReport);
                return reportMapper.toReportDto(currentReport);
            });

        } catch (Exception e) {
            reportFuture.completeExceptionally(e);
            reportFuture.thenAccept(currentReport -> {
                currentReport.setStatus(ReportStatus.FAILED);
                reportDao.save(currentReport);
            });
        }

        return reportFuture.thenApply(reportMapper::toReportDto);
    }
}