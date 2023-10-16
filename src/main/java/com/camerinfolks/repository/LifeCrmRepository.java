package com.camerinfolks.repository;

import com.camerinfolks.model.HISSites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface LifeCrmRepository extends JpaRepository<HISSites, Long>{

}
