package axi.practice.data_generation_reports.entity;

import axi.practice.data_generation_reports.entity.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Table(name="reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name="filter_id")
    private RequestFilter filter;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="finished_at")
    private LocalDateTime finishedAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReportRow> rows = new LinkedList<>();

    public void addRows(List<ReportRow> rows) {
        this.rows.addAll(rows);
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", finishedAt=" + finishedAt +
                ", filter=" + (filter != null ? filter.getId() : null) +
                ", rowsCount=" + (rows != null ? rows.size() : 0) +
                '}';
    }
}
