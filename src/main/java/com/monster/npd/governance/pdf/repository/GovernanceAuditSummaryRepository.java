package com.monster.npd.governance.pdf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monster.npd.governance.pdf.pojo.GovernanceAuditSummary;

@Repository
public interface GovernanceAuditSummaryRepository extends JpaRepository<GovernanceAuditSummary, Integer> {

	public List<GovernanceAuditSummary> findByRequestId(Integer requestId);

}
