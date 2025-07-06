package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.dao.GroupRequestStatDao;
import axi.practice.data_generation_reports.dto.group_request_stat.GetGroupRequestStatsDto;
import axi.practice.data_generation_reports.dto.group_request_stat.GroupRequestStatDto;
import axi.practice.data_generation_reports.entity.GroupRequestStat;
import axi.practice.data_generation_reports.mapper.GroupRequestStatMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupRequestStatsService {

    private final int dataPageSize;

    private final GroupRequestStatDao groupRequestStatDao;

    private final RequestFilterService requestFilterService;

    private final GroupRequestStatMapper groupRequestStatMapper;

    @Transactional
    public Page<GroupRequestStatDto> getRequestGroupsStats(GetGroupRequestStatsDto requestDto) {

        requestFilterService.createFilter(requestDto.getRequestFilter());

        Pageable pageable = PageRequest.of(requestDto.getPage(), dataPageSize);

        Page<GroupRequestStat> pageOfGroupsStats = groupRequestStatDao.findAll(pageable);

        Page<GroupRequestStatDto> pageOfGroupsStatDtos = pageOfGroupsStats.map(groupRequestStatMapper::toGroupRequestStatDto);
        return pageOfGroupsStatDtos;
    }
}
