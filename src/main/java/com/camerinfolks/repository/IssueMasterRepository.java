package com.camerinfolks.repository;

import com.camerinfolks.model.IssueMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface IssueMasterRepository extends JpaRepository<IssueMaster, Long>{
	
	 Page<IssueMaster> findById(Integer siteid, Pageable pageable);
	 Page<IssueMaster> findByissuetype(Integer issuetype, Pageable pageable);
	 Page<IssueMaster> findBypriority(Integer priority, Pageable pageable);
	 Page<IssueMaster> findByissuecode(String issuecode, Pageable pageable);
	 List<Object> findByCreatedDatetimeGreaterThanAndCreatedDatetimeLessThan(Date start,Date end);
	 
	 @Query("select  count(i) from IssueMaster i")
	 List<Object[]> countIssueMaster();
	 
	 @Query("select  count(i) from IssueMaster i where MONTH(i.createdDatetime) = MONTH(NOW())")
	 List<Object[]> countIssueMasterThisMonth();
	 
	 @Query("select  count(i) from IssueMaster i where i.issuestatus in (8,15)")
	 List<Object[]> countIssueMasterTotalResolved();
	 
	 @Query("select count(i) from IssueMaster i where i.issuetype not in (12)")
	 List<Object[]> countIssueMasterTotalBugs();
	 
	 @Query("select  count(i) from IssueMaster i where i.issuetype in (12)")
	 List<Object[]> countIssueMasterTotalRequirements();
	 
	 @Query("select count(i) from IssueMaster i where i.issuetype not in (14,15) and DATE_FORMAT(i.createdDatetime,'%Y-%m-%d')= DATE_FORMAT(NOW(),'%Y-%m-%d') and i.isactive=1")
	 List<Object[]> countIssueMasterTotalIssuesToday();
	 
}
