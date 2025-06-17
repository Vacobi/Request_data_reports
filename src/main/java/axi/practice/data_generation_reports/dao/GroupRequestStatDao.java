package axi.practice.data_generation_reports.dao;

import axi.practice.data_generation_reports.entity.GroupRequestStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRequestStatDao extends JpaRepository<GroupRequestStat, Long> {

    @Query("""
            SELECT g
            FROM GroupRequestStat g
           """)
    Page<GroupRequestStat> findAllFilteredGroupRequestStats(Pageable pageable);
}
