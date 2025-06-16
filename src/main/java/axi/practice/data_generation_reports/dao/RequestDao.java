package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestDao extends JpaRepository<Request, Long> {

}
