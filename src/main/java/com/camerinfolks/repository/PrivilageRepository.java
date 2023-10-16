package com.camerinfolks.repository;

import com.camerinfolks.model.EmployeeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilageRepository extends JpaRepository<EmployeeCategory, Long>{
	

}