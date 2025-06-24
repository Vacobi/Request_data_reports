package axi.practice.data_generation_reports.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestApiConfig {

    @Value("${app.max_string_length}")
    private int maxStringLength;

    @Value("${app.data_page_size}")
    private int dataPageSize;

    @Value("${app.reports.directory}")
    private String reportsDirectory;

    @Bean
    public int maxStringLength() {
        return maxStringLength;
    }

    @Bean
    public int dataPageSize() {
        return dataPageSize;
    }

    @Bean
    public String reportsDirectory() {
        return reportsDirectory;
    }
}
