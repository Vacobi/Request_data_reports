package axi.practice.data_generation_reports.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public abstract class BaseClientException extends RuntimeException {

  private ClientExceptionName exceptionName;
  private HttpStatusCode statusCode;

  public BaseClientException(
          String reason,
          ClientExceptionName exceptionName,
          HttpStatusCode HttpStatusCode
  ) {
    super(reason);
    this.exceptionName = exceptionName;
    this.statusCode = HttpStatusCode;
  }

  public Map<String, Object> properties() {
    return new LinkedHashMap<>();
  }
}