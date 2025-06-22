package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportDao extends JpaRepository<Report, Long> {

    @Query("""
            SELECT r FROM Report r
            LEFT JOIN FETCH r.rows
            LEFT JOIN FETCH r.filter
            WHERE r.id = :id
            """)
    Optional<Report> findByIdWithFilterAndRows(@Param("id") Long id);

    @Query("""
            SELECT r FROM Report r
            LEFT JOIN FETCH r.rows
            LEFT JOIN FETCH r.filter
            WHERE r.id = (SELECT MAX(r2.id) FROM Report r2
            )""")
    Optional<Report> findLastWithRows();
}
