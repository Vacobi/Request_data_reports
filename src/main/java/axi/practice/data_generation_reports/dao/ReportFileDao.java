package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.ReportFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportFileDao extends JpaRepository<ReportFile, Long> {
    ReportFile findByReport_Id(Long reportId);
}
