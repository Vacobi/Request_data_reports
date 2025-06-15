package axi.practice.data_generation_reports.validator;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dto.header.CreateHeaderRequestDto;
import axi.practice.data_generation_reports.dto.queryparam.CreateQueryParamRequestDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.exception.ClientExceptionName;
import axi.practice.data_generation_reports.exception.GroupValidationException;
import axi.practice.data_generation_reports.exception.ValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class RequestValidatorTest {

    @Autowired
    private RequestValidator validator;

    @Autowired
    private int maxStringLength;

    String stringLength(int length){
        return "a".repeat(Math.max(0, length));
    }

    @Nested
    class ValidateCreateQueryParamRequestTest {

        @Test
        void nameIsNull() {

            String name = null;
            String value = "value";
            CreateQueryParamRequestDto request = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateQueryParamRequestDto(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void nameIsEmpty() {

            String name = "";
            String value = "value";
            CreateQueryParamRequestDto request = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateQueryParamRequestDto(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void nameIsMaxLength() {

            String name = stringLength(maxStringLength);
            String value = "value";
            CreateQueryParamRequestDto request = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateQueryParamRequestDto(request);


            assertFalse(optionalGroupValidationException.isPresent());
        }

        @Test
        void nameIsTooLong() {

            String name = stringLength(maxStringLength + 1);
            String value = "value";
            CreateQueryParamRequestDto request = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateQueryParamRequestDto(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void valueIsNull() {

            String name = "name";
            String value = null;
            CreateQueryParamRequestDto request = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateQueryParamRequestDto(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void valueIsEmpty() {

            String name = "name";
            String value = "";
            CreateQueryParamRequestDto request = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateQueryParamRequestDto(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void valueIsMaxLength() {

            String name = "name";
            String value = stringLength(maxStringLength);
            CreateQueryParamRequestDto request = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateQueryParamRequestDto(request);


            assertFalse(optionalGroupValidationException.isPresent());
        }

        @Test
        void valueIsTooLong() {

            String name = "name";
            String value = stringLength(maxStringLength + 1);
            CreateQueryParamRequestDto request = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateQueryParamRequestDto(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void nameAndValueIncorrect() {

            String name = "";
            String value = null;
            CreateQueryParamRequestDto request = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateQueryParamRequestDto(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void requestIsCorrect() {

            String name = "name";
            String value = "value";
            CreateQueryParamRequestDto request = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateQueryParamRequestDto(request);


            assertTrue(optionalGroupValidationException.isEmpty());
        }
    }

    @Nested
    class validateCreateHeaderRequestTest {

        @Test
        void nameIsNull() {

            String name = null;
            String value = "value";
            CreateHeaderRequestDto request = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateHeaderRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void nameIsEmpty() {

            String name = "";
            String value = "value";
            CreateHeaderRequestDto request = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateHeaderRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void nameIsMaxLength() {

            String name = stringLength(maxStringLength);
            String value = "value";
            CreateHeaderRequestDto request = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateHeaderRequest(request);


            assertFalse(optionalGroupValidationException.isPresent());
        }

        @Test
        void nameIsTooLong() {

            String name = stringLength(maxStringLength + 1);
            String value = "value";
            CreateHeaderRequestDto request = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateHeaderRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void valueIsNull() {

            String name = "name";
            String value = null;
            CreateHeaderRequestDto request = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateHeaderRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void valueIsEmpty() {

            String name = "name";
            String value = "";
            CreateHeaderRequestDto request = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateHeaderRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void valueIsMaxLength() {

            String name = "name";
            String value = stringLength(maxStringLength);
            CreateHeaderRequestDto request = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateHeaderRequest(request);


            assertFalse(optionalGroupValidationException.isPresent());
        }

        @Test
        void valueIsTooLong() {

            String name = "name";
            String value = stringLength(maxStringLength + 1);
            CreateHeaderRequestDto request = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateHeaderRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void nameAndValueIsIncorrect() {

            String name = "";
            String value = null;
            CreateHeaderRequestDto request = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateHeaderRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void requestIsCorrect() {

            String name = "name";
            String value = "value";
            CreateHeaderRequestDto request = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateHeaderRequest(request);


            assertTrue(optionalGroupValidationException.isEmpty());
        }
    }

    @Nested
    class ValidateCreateRequestTest {

        @Test
        void urlIsNull() {

            String url = null;
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .build();

            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_REQUEST);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void urlIsEmpty() {

            String url = "";
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .build();

            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_REQUEST);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void urlIsMaxLength() {

            String url = stringLength(maxStringLength);
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .build();


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertFalse(optionalGroupValidationException.isPresent());
        }

        @Test
        void urlIsTooLong() {

            String url = stringLength(maxStringLength + 1);
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .build();

            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_REQUEST);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void headerIsIncorrect() {

            String name = "name";
            String value = null;
            CreateHeaderRequestDto headerRequest = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();

            String url = "url";
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .headers(List.of(headerRequest))
                    .build();

            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void queryParamIsIncorrect() {

            String name = "name";
            String value = null;
            CreateQueryParamRequestDto queryParamRequest = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();

            String url = "url";
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .variableParams(List.of(queryParamRequest))
                    .build();

            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void requestAndHeaderAndQueryParamIsIncorrect() {

            String name = "name";
            String value = null;
            CreateQueryParamRequestDto queryParamRequest = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateHeaderRequestDto headerRequest = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();

            String url = "";
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .headers(List.of(headerRequest))
                    .variableParams(List.of(queryParamRequest))
                    .build();

            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_REQUEST);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void requestAndHeaderAndQueryParamIsCorrect() {

            String name = "name";
            String value = "value";
            CreateQueryParamRequestDto queryParamRequest = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateHeaderRequestDto headerRequest = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();

            String url = "url";
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .headers(List.of(headerRequest))
                    .variableParams(List.of(queryParamRequest))
                    .build();


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertTrue(optionalGroupValidationException.isEmpty());
        }

        @Test
        void severalHeadersInRequestIsIncorrect() {

            String name = "name";
            String value = null;
            CreateHeaderRequestDto headerRequest1 = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateHeaderRequestDto headerRequest2 = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateHeaderRequestDto headerRequest3 = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();

            String url = "url";
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .headers(List.of(headerRequest1, headerRequest2, headerRequest3))
                    .build();

            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void severalQueryParamsInRequestIsIncorrect() {

            String name = "name";
            String value = null;
            CreateQueryParamRequestDto queryParamRequest1 = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateQueryParamRequestDto queryParamRequest2 = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateQueryParamRequestDto queryParamRequest3 = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();

            String url = "url";
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .variableParams(List.of(queryParamRequest1, queryParamRequest2, queryParamRequest3))
                    .build();

            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }

        @Test
        void severalQueryParamsAndHeadersInRequestIsIncorrect() {

            String name = "name";
            String value = null;
            CreateQueryParamRequestDto queryParamRequest1 = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateQueryParamRequestDto queryParamRequest2 = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateQueryParamRequestDto queryParamRequest3 = CreateQueryParamRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateHeaderRequestDto headerRequest1 = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateHeaderRequestDto headerRequest2 = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();
            CreateHeaderRequestDto headerRequest3 = CreateHeaderRequestDto.builder()
                    .name(name)
                    .value(value)
                    .build();

            String url = "url";
            CreateRequestDto request = CreateRequestDto.builder()
                    .url(url)
                    .headers(List.of(headerRequest1, headerRequest2, headerRequest3))
                    .variableParams(List.of(queryParamRequest1, queryParamRequest2, queryParamRequest3))
                    .build();

            List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_HEADER);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);
            expectedExceptionNameList.add(ClientExceptionName.INVALID_QUERY_PARAM);


            Optional<GroupValidationException> optionalGroupValidationException =
                    validator.validateCreateRequest(request);


            assertTrue(optionalGroupValidationException.isPresent());

            GroupValidationException groupValidationException = optionalGroupValidationException.get();
            List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
            List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
            validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

            assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
        }
    }
}