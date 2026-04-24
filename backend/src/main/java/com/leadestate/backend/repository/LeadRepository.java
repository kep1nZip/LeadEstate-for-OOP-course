package com.leadestate.backend.repository;

import com.leadestate.backend.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface LeadRepository extends JpaRepository<Lead, Integer> {
}