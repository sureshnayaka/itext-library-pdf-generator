package com.itext.pdf.generation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itext.pdf.generation.pojo.SubmissionGovernanceAudit;

@Repository
public interface SubmissionGovernanceAuditRepository extends JpaRepository<SubmissionGovernanceAudit, Long> {
	

}
