package axi.practice.data_generation_reports.validator;

import axi.practice.data_generation_reports.config.TestContainersConfig;
import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.exception.ClientExceptionName;
import axi.practice.data_generation_reports.exception.GroupValidationException;
import axi.practice.data_generation_reports.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class RequestFilterValidatorTest {

    @Autowired
    private RequestFilterValidator validator;

    @Test
    void toDateIsNull() {

        LocalDateTime fromDate = LocalDateTime.now();

        CreateRequestFilterRequestDto requestDto = CreateRequestFilterRequestDto.builder()
                .toDate(null)
                .fromDate(fromDate)
                .build();

        assertDoesNotThrow(() -> validator.validateCreateRequestFilter(requestDto));
    }

    @Test
    void toFromIsNull() {

        LocalDateTime toDate = LocalDateTime.now();

        CreateRequestFilterRequestDto requestDto = CreateRequestFilterRequestDto.builder()
                .toDate(toDate)
                .fromDate(null)
                .build();

        assertDoesNotThrow(() -> validator.validateCreateRequestFilter(requestDto));
    }

    @Test
    void toDateAndFromDateIsNull() {

        CreateRequestFilterRequestDto requestDto = CreateRequestFilterRequestDto.builder()
                .toDate(null)
                .fromDate(null)
                .build();

        assertDoesNotThrow(() -> validator.validateCreateRequestFilter(requestDto));
    }

    @Test
    void toDateAndFromDateNotNullAndCorrect() {

        LocalDateTime fromDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2022, 1, 1, 0, 0);
        CreateRequestFilterRequestDto requestDto = CreateRequestFilterRequestDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        assertDoesNotThrow(() -> validator.validateCreateRequestFilter(requestDto));
    }

    @Test
    void toDateAndFromDateSame() {

        LocalDateTime fromDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        CreateRequestFilterRequestDto requestDto = CreateRequestFilterRequestDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        assertDoesNotThrow(() -> validator.validateCreateRequestFilter(requestDto));
    }

    @Test
    void toDateAndFromDateNotNullIncorrect() {

        LocalDateTime toDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime fromDate = LocalDateTime.of(2020, 1, 1, 0, 1);
        CreateRequestFilterRequestDto requestDto = CreateRequestFilterRequestDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();


        List<ClientExceptionName> expectedExceptionNameList = new LinkedList<>();
        expectedExceptionNameList.add(ClientExceptionName.INVALID_REQUEST_FILTER);


        Optional<GroupValidationException> optionalGroupValidationException =
                validator.validateCreateRequestFilter(requestDto);


        assertTrue(optionalGroupValidationException.isPresent());

        GroupValidationException groupValidationException = optionalGroupValidationException.get();
        List<? extends ValidationException> validationExceptionList = groupValidationException.getExceptions();
        List<ClientExceptionName> actualClientExceptionNameList = new LinkedList<>();
        validationExceptionList.forEach(validationException -> actualClientExceptionNameList.add(validationException.getExceptionName()));

        assertEquals(expectedExceptionNameList, actualClientExceptionNameList);
    }
}