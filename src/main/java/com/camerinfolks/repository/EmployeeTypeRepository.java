package com.camerinfolks.repository;

import com.camerinfolks.model.EmployeeDesignation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeTypeRepository extends JpaRepository<EmployeeDesignation, Long>{

}
