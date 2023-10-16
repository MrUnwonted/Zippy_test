package com.camerinfolks.repository;

import com.camerinfolks.model.HISEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface HISEmployeeRepository extends JpaRepository<HISEmployee, Long>{
	
	List<HISEmployee> findByemployeetype(Long employeetype);

}
