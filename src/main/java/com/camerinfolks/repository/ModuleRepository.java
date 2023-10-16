package com.camerinfolks.repository;

import com.camerinfolks.model.ModuleMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<ModuleMaster, Long>{

//	List<ModuleMaster> findAll(Class<ModuleMaster> class1);
	

}
