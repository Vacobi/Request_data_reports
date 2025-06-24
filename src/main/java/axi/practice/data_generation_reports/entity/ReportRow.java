package axi.practice.data_generation_reports.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@Table(name="report_rows")
public class ReportRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name="row_uuid", nullable = false)
    private UUID rowUUID;

    @Column(name="host", nullable = false)
    private String host;

    @Column(name="path", nullable = false)
    private String path;

    @Column(name="avg_headers")
    private double avgHeaders;

    @Column(name="avg_query_params")
    private double avgQueryParams;
}
