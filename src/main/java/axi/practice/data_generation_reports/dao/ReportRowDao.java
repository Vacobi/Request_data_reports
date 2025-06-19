package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.ReportRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRowDao extends JpaRepository<ReportRow, Long> {

}
