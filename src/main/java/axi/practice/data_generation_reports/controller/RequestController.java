package axi.practice.data_generation_reports.controller;

import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.dto.request.RequestDto;
import axi.practice.data_generation_reports.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto create(@RequestBody CreateRequestDto requestDto) {
        return requestService.create(requestDto);
    }
}
