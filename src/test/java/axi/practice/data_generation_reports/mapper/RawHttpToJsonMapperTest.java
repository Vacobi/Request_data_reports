package axi.practice.data_generation_reports.mapper;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dto.queryparam.CreateQueryParamRequestDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.exception.ClientExceptionName;
import axi.practice.data_generation_reports.exception.IncorrectRawRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static axi.practice.data_generation_reports.util.TestAsserts.assertCreateRequestDtoEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class RawHttpToJsonMapperTest {

    @Autowired
    private RawHttpToJsonMapper mapper;

    @Test
    void stringContainsOnlyHostWithSchema() {
        String raw = "https://google.com";

        CreateRequestDto actual = mapper.toCreateRequestDto(raw);

        CreateRequestDto expected = CreateRequestDto.builder()
                .url("google.com")
                .build();

        assertCreateRequestDtoEquals(expected, actual);
    }

    @Test
    void stringContainsOnlyHostWithoutSchema() {
        String raw = "google.com";

        CreateRequestDto actual = mapper.toCreateRequestDto(raw);

        CreateRequestDto expected = CreateRequestDto.builder()
                .url("google.com")
                .build();

        assertCreateRequestDtoEquals(expected, actual);
    }

    @Test
    void stringContainsOnlyHostWithPath() {
        String raw = "https://docs.google.com/document/u/0/";

        CreateRequestDto actual = mapper.toCreateRequestDto(raw);

        CreateRequestDto expected = CreateRequestDto.builder()
                .url("docs.google.com")
                .path("document/u/0/")
                .build();

        assertCreateRequestDtoEquals(expected, actual);
    }

    @Test
    void stringContainsOneQueryParam() {
        String raw = "https://github.com/Vacobi?tab=repositories";

        CreateRequestDto actual = mapper.toCreateRequestDto(raw);

        CreateQueryParamRequestDto createQueryParamRequestDto = CreateQueryParamRequestDto.builder()
                .name("tab")
                .value("repositories")
                .build();
        CreateRequestDto expected = CreateRequestDto.builder()
                .url("github.com")
                .path("Vacobi")
                .variableParams(List.of(createQueryParamRequestDto))
                .build();

        assertCreateRequestDtoEquals(expected, actual);
    }

    @Test
    void stringContainsSeveralQueryParams() {
        String raw = "https://google.com/search?q=openai&lang=en&safe=active";


        CreateRequestDto actual = mapper.toCreateRequestDto(raw);


        CreateQueryParamRequestDto createQueryParamRequestDto1 = CreateQueryParamRequestDto.builder()
                .name("q")
                .value("openai")
                .build();
        CreateQueryParamRequestDto createQueryParamRequestDto2 = CreateQueryParamRequestDto.builder()
                .name("lang")
                .value("en")
                .build();
        CreateQueryParamRequestDto createQueryParamRequestDto3 = CreateQueryParamRequestDto.builder()
                .name("safe")
                .value("active")
                .build();

        CreateRequestDto expected = CreateRequestDto.builder()
                .url("google.com")
                .path("search")
                .variableParams(List.of(createQueryParamRequestDto1, createQueryParamRequestDto2, createQueryParamRequestDto3))
                .build();


        assertCreateRequestDtoEquals(expected, actual);
    }

    @Test
    void emptyString() {
        String raw = "";

        IncorrectRawRequest actualException = assertThrows(IncorrectRawRequest.class, () -> mapper.toCreateRequestDto(raw));

        ClientExceptionName expectedExceptionName = ClientExceptionName.INVALID_RAW_REQUEST;

        assertEquals(expectedExceptionName, actualException.getExceptionName());
    }

    @Test
    void stringContainsPathWithoutHost() {
        String raw = "https:///path1";

        CreateRequestDto actual = mapper.toCreateRequestDto(raw);

        CreateRequestDto expected = CreateRequestDto.builder()
                .path("path1")
                .build();

        assertCreateRequestDtoEquals(expected, actual);
    }

    @Test
    void stringContainsOneQueryParamWithoutValue() {
        String raw = "https://github.com/Vacobi?tab=";

        CreateRequestDto actual = mapper.toCreateRequestDto(raw);

        CreateQueryParamRequestDto createQueryParamRequestDto = CreateQueryParamRequestDto.builder()
                .name("tab")
                .build();
        CreateRequestDto expected = CreateRequestDto.builder()
                .url("github.com")
                .path("Vacobi")
                .variableParams(List.of(createQueryParamRequestDto))
                .build();

        assertCreateRequestDtoEquals(expected, actual);
    }
}