package axi.practice.data_generation_reports.validator;

import axi.practice.data_generation_reports.dto.filter.CreateRequestFilterRequestDto;
import axi.practice.data_generation_reports.exception.ClientExceptionName;
import axi.practice.data_generation_reports.exception.GroupValidationException;
import axi.practice.data_generation_reports.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
public class RequestFilterValidator {

    public Optional<GroupValidationException> validateCreateRequestFilter(CreateRequestFilterRequestDto requestDto) {

        List<ValidationException> exceptions = new LinkedList<>();

        exceptions.addAll(validateFilterDates(requestDto.getFromDate(), requestDto.getToDate()));

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }

    private List<ValidationException> validateFilterDates(LocalDateTime from, LocalDateTime to) {

        List<ValidationException> exceptions = new LinkedList<>();

        if (from == null || to == null) {
            return exceptions;
        }

        if (from.isAfter(to)) {
            String exceptionDescription = String.format("'To' date (%s) is after 'From' date (%s)", to, from);
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_REQUEST_FILTER));
        }

        return exceptions;
    }
}
