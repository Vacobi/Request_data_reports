package axi.practice.data_generation_reports.entity;

import axi.practice.data_generation_reports.entity.enums.StorageType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Table(name="report_files")
public class ReportFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    private StorageType storageType;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Lob
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "file_data", columnDefinition = "BYTEA")
    private byte[] fileData;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "ReportFile{" +
                "id=" + id +
                ", reportId=" + (report != null ? report.getId() : "null") +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", storageType=" + storageType +
                ", mimeType='" + mimeType + '\'' +
                ", fileDataSize=" + (fileData != null ? fileData.length + " bytes" : "null") +
                ", createdAt=" + createdAt +
                '}';
    }
}
