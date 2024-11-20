package com.monster.npd.governance.pdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monster.npd.governance.pdf.pojo.UserIdentity;

@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentity, Long> {

	
}
