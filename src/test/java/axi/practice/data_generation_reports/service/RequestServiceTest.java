package axi.practice.data_generation_reports.service;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dao.HeaderDao;
import axi.practice.data_generation_reports.dao.QueryParamDao;
import axi.practice.data_generation_reports.dao.RequestDao;
import axi.practice.data_generation_reports.dto.header.CreateHeaderRequestDto;
import axi.practice.data_generation_reports.dto.header.HeaderDto;
import axi.practice.data_generation_reports.dto.queryparam.CreateQueryParamRequestDto;
import axi.practice.data_generation_reports.dto.queryparam.QueryParamDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.dto.request.RequestDto;
import axi.practice.data_generation_reports.entity.Header;
import axi.practice.data_generation_reports.entity.QueryParam;
import axi.practice.data_generation_reports.entity.Request;
import axi.practice.data_generation_reports.exception.ClientExceptionName;
import axi.practice.data_generation_reports.exception.GroupValidationException;
import axi.practice.data_generation_reports.exception.ValidationException;
import axi.practice.data_generation_reports.mapper.HeaderMapper;
import axi.practice.data_generation_reports.mapper.QueryParamMapper;
import axi.practice.data_generation_reports.util.ClearableTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static axi.practice.data_generation_reports.util.TestAsserts.assertRequestDtoEquals;
import static axi.practice.data_generation_reports.util.TestAsserts.assertRequestEquals;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class RequestServiceTest extends ClearableTest {

    @Autowired
    private RequestDao requestDao;
    @Autowired
    private HeaderDao headerDao;
    @Autowired
    private QueryParamDao queryParamDao;

    @Autowired
    private RequestService requestService;

    @Autowired
    private HeaderMapper headerMapper;
    @Autowired
    private QueryParamMapper queryParamMapper;


    private long queryParamNumber = 0;
    private List<CreateQueryParamRequestDto> createQueryParamsRequests(long count) {

        List<CreateQueryParamRequestDto> queryParamRequests = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            CreateQueryParamRequestDto requestDto = CreateQueryParamRequestDto.builder()
                    .name("name " + queryParamNumber)
                    .value("value " + queryParamNumber)
                    .build();

            queryParamNumber++;

            queryParamRequests.add(requestDto);
        }

        return queryParamRequests;
    }

    private long headerNumber = 0;
    private List<CreateHeaderRequestDto> createHeaderRequests(long count) {

        List<CreateHeaderRequestDto> headerRequests = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            CreateHeaderRequestDto requestDto = CreateHeaderRequestDto.builder()
                    .name("name " + headerNumber)
                    .value("value " + headerNumber)
                    .build();

            headerNumber++;

            headerRequests.add(requestDto);
        }

        return headerRequests;
    }

    @Transactional
    @Nested
    class createRequestTest {

        @Test
        void createRequest() {

            String url = "https://axiomatikallc.visualstudio.com";
            String path = "Practice_20230120/_workitems/create/Task";
            String body = "Body of the request";

            int headersCount = 2;
            int queryParamsCount = 3;
            List<CreateHeaderRequestDto> headersCreateRequests = createHeaderRequests(headersCount);
            List<CreateQueryParamRequestDto> queryParamsCreateRequests = createQueryParamsRequests(queryParamsCount);
            CreateRequestDto createRequestDto = CreateRequestDto.builder()
                    .url(url)
                    .path(path)
                    .body(body)
                    .headers(headersCreateRequests)
                    .variableParams(queryParamsCreateRequests)
                    .build();


            long headersBeforeSave = headerDao.count();
            long queryParamsBeforeSave = queryParamDao.count();
            long requestBeforeSave = requestDao.count();

            RequestDto actualDto = requestService.create(createRequestDto);
            Optional<Request> actualOptional = requestDao.findByIdWithHeadersAndParams(actualDto.getId());
            assertTrue(actualOptional.isPresent());
            Request actual = actualOptional.get();

            long headersAfterSave = headerDao.count();
            long queryParamsAfterSave = queryParamDao.count();
            long requestAfterSave = requestDao.count();


            List<HeaderDto> expectedHeadersDto = new LinkedList<>();
            for (int i = 0; i < headersCount; i++) {
                Header header = headerMapper.toHeader(headersCreateRequests.get(i));
                HeaderDto headerDto = headerMapper.toHeaderDto(header);

                headerDto.setId(actualDto.getHeaders().get(i).getId());
                expectedHeadersDto.add(headerDto);
            }
            List<Header> expectedHeaders = new LinkedList<>();
            for (HeaderDto dto : expectedHeadersDto) {
                Header header = headerMapper.toHeader(dto);
                header.setRequest(actual);
                expectedHeaders.add(header);
            }

            List<QueryParamDto> expectedQueryParamsDto = new LinkedList<>();
            for (int i = 0; i < queryParamsCount; i++) {
                QueryParam queryParam = queryParamMapper.toQueryParam(queryParamsCreateRequests.get(i));
                QueryParamDto queryParamDto = queryParamMapper.toQueryParamDto(queryParam);

                queryParamDto.setId(actualDto.getVariableParams().get(i).getId());
                expectedQueryParamsDto.add(queryParamDto);
            }
            List<QueryParam> expectedQueryParams = new LinkedList<>();
            for (QueryParamDto dto : expectedQueryParamsDto) {
                QueryParam queryParam = queryParamMapper.toQueryParam(dto);
                queryParam.setRequest(actual);
                expectedQueryParams.add(queryParam);
            }

            RequestDto expectedDto = RequestDto.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .path(path)
                    .body(body)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeadersDto)
                    .variableParams(expectedQueryParamsDto)
                    .build();

            Request expected = Request.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .path(path)
                    .body(body)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeaders)
                    .queryParams(expectedQueryParams)
                    .build();


            assertRequestDtoEquals(expectedDto, actualDto);
            assertEquals(headersBeforeSave + headersCount, headersAfterSave);
            assertEquals(queryParamsBeforeSave + queryParamsCount, queryParamsAfterSave);
            assertEquals(requestBeforeSave + 1, requestAfterSave);
            assertRequestEquals(expected, actual);
        }

        @Test
        void createRequestWithoutHeaders() {

            String url = "https://axiomatikallc.visualstudio.com";
            String path = "Practice_20230120/_workitems/create/Task";
            String body = "Body of the request";

            int headersCount = 0;
            int queryParamsCount = 3;
            List<CreateQueryParamRequestDto> queryParamsCreateRequests = createQueryParamsRequests(queryParamsCount);
            CreateRequestDto createRequestDto = CreateRequestDto.builder()
                    .url(url)
                    .path(path)
                    .body(body)
                    .variableParams(queryParamsCreateRequests)
                    .build();


            long headersBeforeSave = headerDao.count();
            long queryParamsBeforeSave = queryParamDao.count();
            long requestBeforeSave = requestDao.count();

            RequestDto actualDto = requestService.create(createRequestDto);
            Optional<Request> actualOptional = requestDao.findByIdWithHeadersAndParams(actualDto.getId());
            assertTrue(actualOptional.isPresent());
            Request actual = actualOptional.get();

            long headersAfterSave = headerDao.count();
            long queryParamsAfterSave = queryParamDao.count();
            long requestAfterSave = requestDao.count();


            List<Header> expectedHeaders = new LinkedList<>();
            List<HeaderDto> expectedHeadersDto = new LinkedList<>();

            List<QueryParamDto> expectedQueryParamsDto = new LinkedList<>();
            for (int i = 0; i < queryParamsCount; i++) {
                QueryParam queryParam = queryParamMapper.toQueryParam(queryParamsCreateRequests.get(i));
                QueryParamDto queryParamDto = queryParamMapper.toQueryParamDto(queryParam);

                queryParamDto.setId(actualDto.getVariableParams().get(i).getId());
                expectedQueryParamsDto.add(queryParamDto);
            }
            List<QueryParam> expectedQueryParams = new LinkedList<>();
            for (QueryParamDto dto : expectedQueryParamsDto) {
                QueryParam queryParam = queryParamMapper.toQueryParam(dto);
                queryParam.setRequest(actual);
                expectedQueryParams.add(queryParam);
            }

            RequestDto expectedDto = RequestDto.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .path(path)
                    .body(body)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeadersDto)
                    .variableParams(expectedQueryParamsDto)
                    .build();

            Request expected = Request.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .path(path)
                    .body(body)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeaders)
                    .queryParams(expectedQueryParams)
                    .build();


            assertRequestDtoEquals(expectedDto, actualDto);
            assertEquals(headersBeforeSave + headersCount, headersAfterSave);
            assertEquals(queryParamsBeforeSave + queryParamsCount, queryParamsAfterSave);
            assertEquals(requestBeforeSave + 1, requestAfterSave);
            assertRequestEquals(expected, actual);
        }

        @Test
        void createRequestWithoutQueryParams() {

            String url = "https://axiomatikallc.visualstudio.com";
            String path = "Practice_20230120/_workitems/create/Task";
            String body = "Body of the request";

            int headersCount = 2;
            int queryParamsCount = 0;
            List<CreateHeaderRequestDto> headersCreateRequests = createHeaderRequests(2);
            CreateRequestDto createRequestDto = CreateRequestDto.builder()
                    .url(url)
                    .path(path)
                    .body(body)
                    .headers(headersCreateRequests)
                    .build();


            long headersBeforeSave = headerDao.count();
            long queryParamsBeforeSave = queryParamDao.count();
            long requestBeforeSave = requestDao.count();

            RequestDto actualDto = requestService.create(createRequestDto);
            Optional<Request> actualOptional = requestDao.findByIdWithHeadersAndParams(actualDto.getId());
            assertTrue(actualOptional.isPresent());
            Request actual = actualOptional.get();

            long headersAfterSave = headerDao.count();
            long queryParamsAfterSave = queryParamDao.count();
            long requestAfterSave = requestDao.count();


            List<HeaderDto> expectedHeadersDto = new LinkedList<>();
            for (int i = 0; i < headersCount; i++) {
                Header header = headerMapper.toHeader(headersCreateRequests.get(i));
                HeaderDto headerDto = headerMapper.toHeaderDto(header);

                headerDto.setId(actualDto.getHeaders().get(i).getId());
                expectedHeadersDto.add(headerDto);
            }
            List<Header> expectedHeaders = new LinkedList<>();
            for (HeaderDto dto : expectedHeadersDto) {
                Header header = headerMapper.toHeader(dto);
                header.setRequest(actual);
                expectedHeaders.add(header);
            }

            List<QueryParamDto> expectedQueryParamsDto = new LinkedList<>();
            List<QueryParam> expectedQueryParams = new LinkedList<>();

            RequestDto expectedDto = RequestDto.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .path(path)
                    .body(body)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeadersDto)
                    .variableParams(expectedQueryParamsDto)
                    .build();

            Request expected = Request.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .path(path)
                    .body(body)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeaders)
                    .queryParams(expectedQueryParams)
                    .build();


            assertRequestDtoEquals(expectedDto, actualDto);
            assertEquals(headersBeforeSave + headersCount, headersAfterSave);
            assertEquals(queryParamsBeforeSave + queryParamsCount, queryParamsAfterSave);
            assertEquals(requestBeforeSave + 1, requestAfterSave);
            assertRequestEquals(expected, actual);
        }

        @Test
        void createRequestWithoutPath() {

            String url = "https://axiomatikallc.visualstudio.com";
            String body = "Body of the request";

            int headersCount = 2;
            int queryParamsCount = 3;
            List<CreateHeaderRequestDto> headersCreateRequests = createHeaderRequests(headersCount);
            List<CreateQueryParamRequestDto> queryParamsCreateRequests = createQueryParamsRequests(queryParamsCount);
            CreateRequestDto createRequestDto = CreateRequestDto.builder()
                    .url(url)
                    .body(body)
                    .headers(headersCreateRequests)
                    .variableParams(queryParamsCreateRequests)
                    .build();


            long headersBeforeSave = headerDao.count();
            long queryParamsBeforeSave = queryParamDao.count();
            long requestBeforeSave = requestDao.count();

            RequestDto actualDto = requestService.create(createRequestDto);
            Optional<Request> actualOptional = requestDao.findByIdWithHeadersAndParams(actualDto.getId());
            assertTrue(actualOptional.isPresent());
            Request actual = actualOptional.get();

            long headersAfterSave = headerDao.count();
            long queryParamsAfterSave = queryParamDao.count();
            long requestAfterSave = requestDao.count();


            List<HeaderDto> expectedHeadersDto = new LinkedList<>();
            for (int i = 0; i < headersCount; i++) {
                Header header = headerMapper.toHeader(headersCreateRequests.get(i));
                HeaderDto headerDto = headerMapper.toHeaderDto(header);

                headerDto.setId(actualDto.getHeaders().get(i).getId());
                expectedHeadersDto.add(headerDto);
            }
            List<Header> expectedHeaders = new LinkedList<>();
            for (HeaderDto dto : expectedHeadersDto) {
                Header header = headerMapper.toHeader(dto);
                header.setRequest(actual);
                expectedHeaders.add(header);
            }

            List<QueryParamDto> expectedQueryParamsDto = new LinkedList<>();
            for (int i = 0; i < queryParamsCount; i++) {
                QueryParam queryParam = queryParamMapper.toQueryParam(queryParamsCreateRequests.get(i));
                QueryParamDto queryParamDto = queryParamMapper.toQueryParamDto(queryParam);

                queryParamDto.setId(actualDto.getVariableParams().get(i).getId());
                expectedQueryParamsDto.add(queryParamDto);
            }
            List<QueryParam> expectedQueryParams = new LinkedList<>();
            for (QueryParamDto dto : expectedQueryParamsDto) {
                QueryParam queryParam = queryParamMapper.toQueryParam(dto);
                queryParam.setRequest(actual);
                expectedQueryParams.add(queryParam);
            }

            RequestDto expectedDto = RequestDto.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .body(body)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeadersDto)
                    .variableParams(expectedQueryParamsDto)
                    .build();

            Request expected = Request.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .body(body)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeaders)
                    .queryParams(expectedQueryParams)
                    .build();


            assertRequestDtoEquals(expectedDto, actualDto);
            assertEquals(headersBeforeSave + headersCount, headersAfterSave);
            assertEquals(queryParamsBeforeSave + queryParamsCount, queryParamsAfterSave);
            assertEquals(requestBeforeSave + 1, requestAfterSave);
            assertRequestEquals(expected, actual);
        }

        @Test
        void createRequestWithoutBody() {

            String url = "https://axiomatikallc.visualstudio.com";
            String path = "Practice_20230120/_workitems/create/Task";

            int headersCount = 2;
            int queryParamsCount = 3;
            List<CreateHeaderRequestDto> headersCreateRequests = createHeaderRequests(headersCount);
            List<CreateQueryParamRequestDto> queryParamsCreateRequests = createQueryParamsRequests(queryParamsCount);
            CreateRequestDto createRequestDto = CreateRequestDto.builder()
                    .url(url)
                    .path(path)
                    .headers(headersCreateRequests)
                    .variableParams(queryParamsCreateRequests)
                    .build();


            long headersBeforeSave = headerDao.count();
            long queryParamsBeforeSave = queryParamDao.count();
            long requestBeforeSave = requestDao.count();

            RequestDto actualDto = requestService.create(createRequestDto);
            Optional<Request> actualOptional = requestDao.findByIdWithHeadersAndParams(actualDto.getId());
            assertTrue(actualOptional.isPresent());
            Request actual = actualOptional.get();

            long headersAfterSave = headerDao.count();
            long queryParamsAfterSave = queryParamDao.count();
            long requestAfterSave = requestDao.count();


            List<HeaderDto> expectedHeadersDto = new LinkedList<>();
            for (int i = 0; i < headersCount; i++) {
                Header header = headerMapper.toHeader(headersCreateRequests.get(i));
                HeaderDto headerDto = headerMapper.toHeaderDto(header);

                headerDto.setId(actualDto.getHeaders().get(i).getId());
                expectedHeadersDto.add(headerDto);
            }
            List<Header> expectedHeaders = new LinkedList<>();
            for (HeaderDto dto : expectedHeadersDto) {
                Header header = headerMapper.toHeader(dto);
                header.setRequest(actual);
                expectedHeaders.add(header);
            }

            List<QueryParamDto> expectedQueryParamsDto = new LinkedList<>();
            for (int i = 0; i < queryParamsCount; i++) {
                QueryParam queryParam = queryParamMapper.toQueryParam(queryParamsCreateRequests.get(i));
                QueryParamDto queryParamDto = queryParamMapper.toQueryParamDto(queryParam);

                queryParamDto.setId(actualDto.getVariableParams().get(i).getId());
                expectedQueryParamsDto.add(queryParamDto);
            }
            List<QueryParam> expectedQueryParams = new LinkedList<>();
            for (QueryParamDto dto : expectedQueryParamsDto) {
                QueryParam queryParam = queryParamMapper.toQueryParam(dto);
                queryParam.setRequest(actual);
                expectedQueryParams.add(queryParam);
            }

            RequestDto expectedDto = RequestDto.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .path(path)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeadersDto)
                    .variableParams(expectedQueryParamsDto)
                    .build();

            Request expected = Request.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .path(path)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeaders)
                    .queryParams(expectedQueryParams)
                    .build();


            assertRequestDtoEquals(expectedDto, actualDto);
            assertEquals(headersBeforeSave + headersCount, headersAfterSave);
            assertEquals(queryParamsBeforeSave + queryParamsCount, queryParamsAfterSave);
            assertEquals(requestBeforeSave + 1, requestAfterSave);
            assertRequestEquals(expected, actual);
        }

        @Test
        void requestWithMinimumData() {

            String url = "https://axiomatikallc.visualstudio.com";

            int headersCount = 0;
            int queryParamsCount = 0;
            CreateRequestDto createRequestDto = CreateRequestDto.builder()
                    .url(url)
                    .build();


            long headersBeforeSave = headerDao.count();
            long queryParamsBeforeSave = queryParamDao.count();
            long requestBeforeSave = requestDao.count();

            RequestDto actualDto = requestService.create(createRequestDto);
            Optional<Request> actualOptional = requestDao.findByIdWithHeadersAndParams(actualDto.getId());
            assertTrue(actualOptional.isPresent());
            Request actual = actualOptional.get();

            long headersAfterSave = headerDao.count();
            long queryParamsAfterSave = queryParamDao.count();
            long requestAfterSave = requestDao.count();


            List<HeaderDto> expectedHeadersDto = new LinkedList<>();
            List<Header> expectedHeaders = new LinkedList<>();

            List<QueryParamDto> expectedQueryParamsDto = new LinkedList<>();
            List<QueryParam> expectedQueryParams = new LinkedList<>();

            RequestDto expectedDto = RequestDto.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeadersDto)
                    .variableParams(expectedQueryParamsDto)
                    .build();

            Request expected = Request.builder()
                    .id(actualDto.getId())
                    .url(url)
                    .timestamp(actual.getTimestamp())
                    .headers(expectedHeaders)
                    .queryParams(expectedQueryParams)
                    .build();


            assertRequestDtoEquals(expectedDto, actualDto);
            assertEquals(headersBeforeSave + headersCount, headersAfterSave);
            assertEquals(queryParamsBeforeSave + queryParamsCount, queryParamsAfterSave);
            assertEquals(requestBeforeSave + 1, requestAfterSave);
            assertRequestEquals(expected, actual);
        }

        @Test
        void incorrectRequest() {

            String url = "https://axiomatikallc.visualstudio.com";
            String path = "Practice_20230120/_workitems/create/Task";
            String body = "Body of the request";

            CreateHeaderRequestDto createHeaderRequestDto = CreateHeaderRequestDto.builder()
                    .name("")
                    .value("value")
                    .build();
            List<CreateHeaderRequestDto> headersCreateRequests = List.of(createHeaderRequestDto);
            CreateRequestDto createRequestDto = CreateRequestDto.builder()
                    .url(url)
                    .path(path)
                    .body(body)
                    .headers(headersCreateRequests)
                    .build();


            long headersBeforeSave = headerDao.count();
            long queryParamsBeforeSave = queryParamDao.count();
            long requestBeforeSave = requestDao.count();

            GroupValidationException groupValidationException =
                    assertThrows(GroupValidationException.class, () -> requestService.create(createRequestDto));
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualExceptionNameList.add(validationException.getExceptionName()));

            long headersAfterSave = headerDao.count();
            long queryParamsAfterSave = queryParamDao.count();
            long requestAfterSave = requestDao.count();


            List<ClientExceptionName> expectedExceptionNames = List.of(ClientExceptionName.INVALID_HEADER);


            assertEquals(expectedExceptionNames, actualExceptionNameList);
            assertEquals(headersBeforeSave, headersAfterSave);
            assertEquals(queryParamsBeforeSave, queryParamsAfterSave);
            assertEquals(requestBeforeSave, requestAfterSave);
        }
    }
}
