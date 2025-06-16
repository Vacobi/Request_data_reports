package axi.practice.data_generation_reports.validator;

import axi.practice.data_generation_reports.dto.header.CreateHeaderRequestDto;
import axi.practice.data_generation_reports.dto.queryparam.CreateQueryParamRequestDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.exception.ClientExceptionName;
import axi.practice.data_generation_reports.exception.GroupValidationException;
import axi.practice.data_generation_reports.exception.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RequestValidator {

    private final int maxStringLength;

    public static final String INVALID_HEADER_NAME_CHARS = " ()<>@,;:\\\"/[]?={}";
    public static final String INVALID_HEADER_VALUE_CHARS;
    public static final String INVALID_QUERY_PARAM_CHARS;
    static {
        StringBuilder headerValue = new StringBuilder();
        for (char c = 0; c <= 31; c++) {
            if (c != '\t') {
                headerValue.append(c);
            }
        }
        headerValue.append((char) 127);
        INVALID_HEADER_VALUE_CHARS = headerValue.toString();

        StringBuilder qp = new StringBuilder();
        for (char c = 0; c < 128; c++) {
            if (!Character.isLetterOrDigit(c) && "-._~".indexOf(c) == -1) {
                qp.append(c);
            }
        }
        INVALID_QUERY_PARAM_CHARS = qp.toString();
    }

    private static boolean containsInvalidChar(String input, String forbiddenChars) {
        if (input == null) return false;
        for (int i = 0; i < input.length(); i++) {
            if (forbiddenChars.indexOf(input.charAt(i)) >= 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean validLength(String input, int maxLength) {
        if (input == null) return true;
        return input.length() <= maxLength;
    }

    public RequestValidator(@Qualifier("maxStringLength") int maxStringLength) {
        this.maxStringLength = maxStringLength;
    }

    public Optional<GroupValidationException> validateCreateRequest(CreateRequestDto request) {

        List<ValidationException> exceptions = new LinkedList<>();

        exceptions.addAll(validateRequestUrl(request.getUrl()));
        request.getHeaders().forEach(header -> validateCreateHeaderRequest(header).ifPresent(e -> exceptions.addAll(e.getExceptions())));
        request.getVariableParams().forEach(qp -> validateCreateQueryParamRequestDto(qp).ifPresent(e -> exceptions.addAll(e.getExceptions())));

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }

    private List<ValidationException> validateRequestUrl(String url) {

        List<ValidationException> exceptions = new LinkedList<>();

        if (url == null || url.isBlank()) {
            String exceptionDescription = "Url of request is missing";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_REQUEST));
        }

        if (!validLength(url, maxStringLength)) {
            String exceptionDescription = String.format("Url of request is too long. Max length is %d, actual %d", maxStringLength, url.length());
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_REQUEST));
        }

        return exceptions;
    }

    public Optional<GroupValidationException> validateCreateHeaderRequest(CreateHeaderRequestDto request) {

        List<ValidationException> exceptions = new LinkedList<>();

        exceptions.addAll(validateHeaderName(request.getName()));
        exceptions.addAll(validateHeaderValue(request.getValue()));

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }

    private List<ValidationException> validateHeaderName(String name) {

        List<ValidationException> exceptions = new LinkedList<>();

        if (name == null || name.isBlank()) {
            String exceptionDescription = "Name of header is missing";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_HEADER));
        }

        if (!validLength(name, maxStringLength)) {
            String exceptionDescription = String.format("Name of header is too long. Max length is %d, actual %d", maxStringLength, name.length());
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_HEADER));
        }

        if (containsInvalidChar(name, INVALID_HEADER_NAME_CHARS)) {
            String exceptionDescription = "Name of header contains invalid chars. All invalid chars: " + INVALID_HEADER_NAME_CHARS;
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_HEADER));
        }

        return exceptions;
    }

    private List<ValidationException> validateHeaderValue(String value) {

        List<ValidationException> exceptions = new LinkedList<>();

        if (value == null || value.isBlank()) {
            String exceptionDescription = "Value of header is missing";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_HEADER));
        }

        if (!validLength(value, maxStringLength)) {
            String exceptionDescription = String.format("Value of header is too long. Max length is %d, actual %d", maxStringLength, value.length());
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_HEADER));
        }

        if (containsInvalidChar(value, INVALID_HEADER_VALUE_CHARS)) {
            String exceptionDescription = String.format("Value of header contains invalid chars. All invalid chars: %s", INVALID_HEADER_VALUE_CHARS);
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_HEADER));
        }

        return exceptions;
    }

    public Optional<GroupValidationException> validateCreateQueryParamRequestDto(CreateQueryParamRequestDto request) {

        List<ValidationException> exceptions = new LinkedList<>();

        exceptions.addAll(validateQueryParamName(request.getName()));
        exceptions.addAll(validateQueryParamValue(request.getValue()));

        return exceptions.isEmpty() ? Optional.empty() : Optional.of(new GroupValidationException(exceptions));
    }

    private List<ValidationException> validateQueryParamName(String name) {

        List<ValidationException> exceptions = new LinkedList<>();

        if (name == null || name.isBlank()) {
            String exceptionDescription = "Name of query param is missing";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_QUERY_PARAM));
        }

        if (!validLength(name, maxStringLength)) {
            String exceptionDescription = String.format("Name of query param is too long. Max length is %d, actual %d", maxStringLength, name.length());
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_QUERY_PARAM));
        }

        if (containsInvalidChar(name, INVALID_QUERY_PARAM_CHARS)) {
            String exceptionDescription = String.format("Name of query param contains invalid chars. All invalid chars: %s", INVALID_QUERY_PARAM_CHARS);
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_QUERY_PARAM));
        }

        return exceptions;
    }

    private List<ValidationException> validateQueryParamValue(String value) {

        List<ValidationException> exceptions = new LinkedList<>();

        if (value == null || value.isBlank()) {
            String exceptionDescription = "Value of query param is missing";
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_QUERY_PARAM));
        }

        if (!validLength(value, maxStringLength)) {
            String exceptionDescription = String.format("Value of query param is too long. Max length is %d, actual %d", maxStringLength, value.length());
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_QUERY_PARAM));
        }

        if (containsInvalidChar(value, INVALID_QUERY_PARAM_CHARS)) {
            String exceptionDescription = String.format("Value of query param contains invalid chars. All invalid chars: %s", INVALID_QUERY_PARAM_CHARS);
            exceptions.add(new ValidationException(exceptionDescription, ClientExceptionName.INVALID_QUERY_PARAM));
        }

        return exceptions;
    }
}
