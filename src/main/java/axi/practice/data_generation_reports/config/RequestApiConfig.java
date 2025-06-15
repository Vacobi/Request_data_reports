package axi.practice.data_generation_reports.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestApiConfig {

    @Value("${app.max_string_length}")
    private int maxStringLength;

    @Bean
    public int maxStringLength() {
        return maxStringLength;
    }
}
