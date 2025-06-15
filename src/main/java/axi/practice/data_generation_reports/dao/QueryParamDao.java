package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.QueryParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryParamDao extends JpaRepository<QueryParam, Long> {
}
