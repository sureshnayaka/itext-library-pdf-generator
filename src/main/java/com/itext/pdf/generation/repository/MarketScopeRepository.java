package com.itext.pdf.generation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itext.pdf.generation.pojo.MarketScope;

@Repository
public interface MarketScopeRepository extends JpaRepository<MarketScope, Long> {
	

}
