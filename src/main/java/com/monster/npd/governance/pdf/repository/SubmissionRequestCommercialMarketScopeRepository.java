package com.monster.npd.governance.pdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monster.npd.governance.pdf.pojo.SubmissionRequestCommercialMarketScope;

public interface SubmissionRequestCommercialMarketScopeRepository
		extends JpaRepository<SubmissionRequestCommercialMarketScope, SubmissionRequestCommercialMarketScope.Id> {

}
