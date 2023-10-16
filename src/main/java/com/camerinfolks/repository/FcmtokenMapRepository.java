package com.camerinfolks.repository;

import com.camerinfolks.model.FcmTokenMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmtokenMapRepository extends JpaRepository<FcmTokenMap, Long>{
	
	 Optional<FcmTokenMap> findById(Long id);
	 
	 Optional<FcmTokenMap> findByUserid(Long id);
}