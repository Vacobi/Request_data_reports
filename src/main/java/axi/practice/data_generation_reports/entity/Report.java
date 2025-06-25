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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="filter_id")
    private RequestFilter filter;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ReportFile> reportFiles = new HashSet<>();

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
    // Здесь сделал список (не множество) на случай, если нужно будет еще сортировать по другим параметрам получившуюся выборку (по хосту по алфавиту, по авг заголовков и тд)
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

    public boolean stored() {
        return !reportFiles.isEmpty();
    }

    public void addReportFile(ReportFile file) {
        this.reportFiles.add(file);
    }
}
