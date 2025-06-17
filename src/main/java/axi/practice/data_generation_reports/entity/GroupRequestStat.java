package axi.practice.data_generation_reports.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Table(name="group_request_stats")
@Immutable
public class GroupRequestStat {

    @Id
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="host")
    private String host;

    @Column(name="path")
    private String path;

    @Column(name="avg_headers")
    private Double avgHeaders;

    @Column(name="avg_params")
    private Double avgQueryParams;
}
