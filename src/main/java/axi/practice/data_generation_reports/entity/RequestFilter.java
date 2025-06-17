package axi.practice.data_generation_reports.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@Table(name="request_filters")
public class RequestFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="from_date")
    private LocalDateTime fromDate;

    @Column(name="to_date")
    private LocalDateTime toDate;

    @Column(name="host")
    private String host;

    @Column(name="path")
    private String path;

    @Column(name="avg_headers")
    private Double avgHeaders;

    @Column(name="avg_query_params")
    private Double avgQueryParams;
}
