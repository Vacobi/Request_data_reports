package axi.practice.data_generation_reports.entity;

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
@Table(name="requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name="url", nullable = false)
    private String url;

    @Column(name="path")
    private String path;

    @Column(name="body")
    private String body;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Header> headers = new LinkedList<>();

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QueryParam> queryParams = new LinkedList<>();

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", body='" + (body != null ? body.length() + " chars" : "null") + '\'' +
                ", timestamp=" + timestamp +
                ", headers=" + (headers != null ?
                headers.stream()
                        .map(h -> "Header{id=" + h.getId() + ", name='" + h.getName() + "', value='" + h.getValue() + "'}")
                        .toList()
                : "null") +
                ", queryParams=" + (queryParams != null ?
                queryParams.stream()
                        .map(p -> "QueryParam{id=" + p.getId() + ", name='" + p.getName() + "', value='" + p.getValue() + "'}")
                        .toList()
                : "null") +
                '}';
    }

    @PrePersist
    public void prePersist() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
