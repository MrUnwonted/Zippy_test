package com.camerinfolks.repository;

import com.camerinfolks.model.HisUsertypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UsertypeRepository extends JpaRepository<HisUsertypes, Long>{

}
