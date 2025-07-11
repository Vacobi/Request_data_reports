package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.RequestFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestFilterDao extends JpaRepository<RequestFilter, Long> {

}
