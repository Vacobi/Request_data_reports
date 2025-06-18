package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.GroupRequestStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRequestStatDao extends JpaRepository<GroupRequestStat, UUID> {

}
