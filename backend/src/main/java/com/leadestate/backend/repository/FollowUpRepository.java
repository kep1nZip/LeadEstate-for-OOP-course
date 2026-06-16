package com.leadestate.backend.repository;

import com.leadestate.backend.entity.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {

    long count();

    long countByStatus(String status);

    @Query("SELECT f.status, COUNT(f) FROM FollowUp f GROUP BY f.status")
    List<Object[]> countFollowUpGroupByStatus();

    List<FollowUp> findByLeadIdOrderByCreatedAtDesc(
            Long leadId
    );
}