package com.monster.npd.governance.pdf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.monster.npd.governance.pdf.pojo.SubmissionRequest;

@Repository
@EnableJpaRepositories
public interface SubmissionsRequestRepository extends JpaRepository<SubmissionRequest, Long> {

	List<SubmissionRequest> getAllSubmissionsById(long id);

}
