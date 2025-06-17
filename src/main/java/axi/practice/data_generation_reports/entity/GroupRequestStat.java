package axi.practice.data_generation_reports.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Table(name="request_filters")
public class GroupRequestStat {

    @Id
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="host")
    private String host;

    @Column(name="path")
    private String path;

    @Column(name="avg_headers")
    private int avgHeaders;

    @Column(name="avg_params")
    private int avgQueryParams;
}
