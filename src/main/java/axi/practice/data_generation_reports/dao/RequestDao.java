package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestDao extends JpaRepository<Request, Long> {

    @Query("SELECT DISTINCT r FROM Request r " +
            "LEFT JOIN FETCH r.headers " +
            "LEFT JOIN FETCH r.queryParams " +
            "WHERE r.id = :id")
    Optional<Request> findByIdWithHeadersAndParams(@Param("id") Long id);
}
