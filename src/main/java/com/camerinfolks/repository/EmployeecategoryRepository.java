package com.camerinfolks.repository;

import com.camerinfolks.model.EmployeeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
@Component
public interface EmployeecategoryRepository extends JpaRepository<EmployeeCategory, Long>{
	

}
