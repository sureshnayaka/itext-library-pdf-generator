package com.monster.npd.governance.pdf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monster.npd.governance.pdf.pojo.SubmissionRequestGovernanceMilestone;

@Repository
public interface SubmissionRequestGovernanceMilestoneRepository
		extends JpaRepository<SubmissionRequestGovernanceMilestone, SubmissionRequestGovernanceMilestone.Identifier> {

	/**
	 * method to fetch all Governance milestone from the table
	 * "(SubmissionRequestGovernance_Milestone)"
	 */
	public List<SubmissionRequestGovernanceMilestone> findByIdRequestId(Long requestId);

}
