package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.ReportFile;
import axi.practice.data_generation_reports.entity.enums.MimeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportFileDao extends JpaRepository<ReportFile, Long> {

    Optional<ReportFile> findByMimeTypeAndReport_Id(MimeType mimeType, Long reportId);

    @Query("""
           SELECT rf FROM ReportFile rf
           JOIN FETCH rf.report r
           LEFT JOIN FETCH r.rows
           LEFT JOIN FETCH r.filter
           WHERE rf.id = :id
           """)
    Optional<ReportFile> findByIdDetailed(Long id);
}
