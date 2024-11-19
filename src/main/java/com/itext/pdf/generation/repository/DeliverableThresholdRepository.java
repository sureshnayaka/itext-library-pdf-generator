package com.itext.pdf.generation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itext.pdf.generation.pojo.DeliverableThreshold;

@Repository
public interface DeliverableThresholdRepository extends JpaRepository<DeliverableThreshold, Long> {
	

}
