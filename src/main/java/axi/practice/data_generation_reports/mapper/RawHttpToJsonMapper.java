package axi.practice.data_generation_reports.mapper;

import axi.practice.data_generation_reports.dto.queryparam.CreateQueryParamRequestDto;
import axi.practice.data_generation_reports.dto.request.CreateRequestDto;
import axi.practice.data_generation_reports.exception.IncorrectRawRequestException;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component
public class RawHttpToJsonMapper {

    public CreateRequestDto toCreateRequestDto(String inputUrl) {

        String urlToParse = inputUrl.matches("^[a-zA-Z][a-zA-Z0-9+\\-.]*://.*$")
                ? inputUrl
                : "https://" + inputUrl;

        URI uri;
        try {
            uri = new URI(urlToParse);
        } catch (Exception e) {
            throw new IncorrectRawRequestException(inputUrl);
        }

        URIBuilder b = new URIBuilder(uri);

        String host = Objects.equals(uri.getHost(), "") ? null : uri.getHost();
        String path = Objects.equals(uri.getPath(), "")
                ? null
                : (uri.getPath().startsWith("/")
                    ? uri.getPath().substring(1)
                    : uri.getPath());

        List<CreateQueryParamRequestDto> queryParams = new LinkedList<>();
        b.getQueryParams().forEach(p -> {
                String name = Objects.equals(p.getName(), "") ? null : p.getName();
                String value = Objects.equals(p.getValue(), "") ? null : p.getValue();
                queryParams.add(new CreateQueryParamRequestDto(name, value));
            }
        );

        return CreateRequestDto.builder()
                .url(host)
                .path(path)
                .variableParams(queryParams)
                .build();
    }
}
