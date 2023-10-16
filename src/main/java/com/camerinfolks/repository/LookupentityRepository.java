package com.camerinfolks.repository;

import com.camerinfolks.model.core.LookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
@Component
public interface LookupentityRepository extends JpaRepository<LookupEntity, Long> {

}
