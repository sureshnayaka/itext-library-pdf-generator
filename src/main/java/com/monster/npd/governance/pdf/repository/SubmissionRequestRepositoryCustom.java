package com.monster.npd.governance.pdf.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.monster.npd.governance.pdf.pojo.SubmissionRequest;

@Component
public class SubmissionRequestRepositoryCustom {

    @Value("${npd.table.prefix}")
    private String tablePrefix;

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
	public List<SubmissionRequest> findDbRowsNotInPim() {
        String query = String.format("SELECT * FROM %sSubmissionRequest", tablePrefix);
        return entityManager.createNativeQuery(query, SubmissionRequest.class).getResultList();
    }
   
}

