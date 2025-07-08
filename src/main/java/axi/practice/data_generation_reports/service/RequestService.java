package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.dao.RequestDao;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.dto.request.RequestDto;
import axi.practice.data_generation_reports.entity.Header;
import axi.practice.data_generation_reports.entity.QueryParam;
import axi.practice.data_generation_reports.entity.Request;
import axi.practice.data_generation_reports.exception.IncorrectRawRequestException;
import axi.practice.data_generation_reports.mapper.HeaderMapper;
import axi.practice.data_generation_reports.mapper.QueryParamMapper;
import axi.practice.data_generation_reports.mapper.RawHttpToJsonMapper;
import axi.practice.data_generation_reports.mapper.RequestMapper;
import axi.practice.data_generation_reports.validator.RequestValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestDao requestDao;

    private final RequestValidator requestValidator;

    private final RequestMapper requestMapper;
    private final HeaderMapper headerMapper;
    private final QueryParamMapper queryParamMapper;

    private final RawHttpToJsonMapper rawHttpToJsonMapper;

    @Transactional
    public RequestDto create(String raw) {

        CreateRequestDto createRequestDto;
        try {
            createRequestDto = rawHttpToJsonMapper.toCreateRequestDto(raw);
        } catch (Exception e) {
            throw new IncorrectRawRequestException(raw);
        }

        return create(createRequestDto);
    }

    @Transactional
    public RequestDto create(CreateRequestDto createRequestDto) {

        requestValidator.validateCreateRequest(createRequestDto).ifPresent(e -> {
            throw e;
        });

        Request request = requestMapper.toRequest(createRequestDto);

        createRequestDto.getHeaders().forEach(headerDto -> {
            Header header = headerMapper.toHeader(headerDto);
            header.setRequest(request);
            request.getHeaders().add(header);
        });

        createRequestDto.getVariableParams().forEach(paramDto -> {
            QueryParam param = queryParamMapper.toQueryParam(paramDto);
            param.setRequest(request);
            request.getQueryParams().add(param);
        });

        Request persistedRequest = requestDao.save(request);

        return requestMapper.toRequestDto(persistedRequest);
    }
}
