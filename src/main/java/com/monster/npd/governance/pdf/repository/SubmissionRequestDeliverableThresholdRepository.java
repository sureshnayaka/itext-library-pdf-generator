package com.monster.npd.governance.pdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monster.npd.governance.pdf.pojo.SubmissionRequestDeliverableThreshold;

public interface SubmissionRequestDeliverableThresholdRepository
		extends JpaRepository<SubmissionRequestDeliverableThreshold, SubmissionRequestDeliverableThreshold.Id> {

}
