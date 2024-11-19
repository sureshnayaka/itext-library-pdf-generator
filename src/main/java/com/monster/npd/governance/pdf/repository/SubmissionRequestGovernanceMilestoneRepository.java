package com.monster.npd.governance.pdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monster.npd.governance.pdf.pojo.SubmissionRequestGovernanceMilestone;

public interface SubmissionRequestGovernanceMilestoneRepository
		extends JpaRepository<SubmissionRequestGovernanceMilestone, SubmissionRequestGovernanceMilestone.Id> {

}
