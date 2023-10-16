package com.camerinfolks.repository;

import com.camerinfolks.model.HISSites;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface HisSitesRepository extends JpaRepository<HISSites, Long>{
	
	List<HISSites> findAllByOrderBySitenameAsc();

}
