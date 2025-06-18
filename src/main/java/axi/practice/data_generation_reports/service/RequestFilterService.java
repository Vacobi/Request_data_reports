package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.dao.RequestFilterDao;
import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.dto.filter.RequestFilterDto;
import axi.practice.data_generation_reports.entity.RequestFilter;
import axi.practice.data_generation_reports.mapper.RequestFilterMapper;
import axi.practice.data_generation_reports.validator.RequestFilterValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestFilterService {

    private final RequestFilterDao requestFilterDao;

    private final RequestFilterValidator requestFilterValidator;

    private final RequestFilterMapper requestFilterMapper;

    @Transactional
    public RequestFilterDto createFilter(CreateRequestFilterRequestDto requestDto) {

        requestFilterValidator.validateCreateRequestFilter(requestDto).ifPresent(e -> {
            throw e;
        });

        RequestFilter requestFilter = requestFilterMapper.toRequestFilter(requestDto);

        RequestFilter persisted = requestFilterDao.save(requestFilter);

        return requestFilterMapper.toRequestFilterDto(persisted);
    }
}
