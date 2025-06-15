package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.Header;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeaderDao extends JpaRepository<Header, Long> {
}
