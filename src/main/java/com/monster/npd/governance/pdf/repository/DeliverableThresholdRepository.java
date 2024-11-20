package com.monster.npd.governance.pdf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monster.npd.governance.pdf.pojo.DeliverableThreshold;

@Repository
public interface DeliverableThresholdRepository extends JpaRepository<DeliverableThreshold, Long> {
	
	public List<DeliverableThreshold> findByPoGovernanceMSId(Long poGovernanceMSId);

}
