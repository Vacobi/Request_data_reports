package axi.practice.data_generation_reports.controller;

import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GetGroupRequestStatsDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GroupRequestStatDto;
import axi.practice.data_generation_reports.service.GroupRequestStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/requests/statistic")
@RequiredArgsConstructor
public class GroupRequestStatsController {

    private final GroupRequestStatsService groupRequestStatsService;

    @PostMapping
    public Page<GroupRequestStatDto> getStatistic(@RequestParam(defaultValue = "0") int page, CreateRequestFilterRequestDto filter) {

        GetGroupRequestStatsDto getGroupRequestStatsDto = GetGroupRequestStatsDto.builder()
                .page(page)
                .requestFilter(filter)
                .build();

        return groupRequestStatsService.getRequestGroupsStats(getGroupRequestStatsDto);
    }
}
