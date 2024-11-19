package com.monster.npd.governance.pdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monster.npd.governance.pdf.pojo.SubmissionRequest;

public interface SubmissionsRequestRepository extends JpaRepository<SubmissionRequest,Long> {
	
	

}
