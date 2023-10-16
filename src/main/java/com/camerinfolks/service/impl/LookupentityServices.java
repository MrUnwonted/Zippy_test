package com.camerinfolks.service.impl;

import com.camerinfolks.model.core.LookupEntity;
import com.camerinfolks.repository.LookupentityRepository;
import com.camerinfolks.service.ILookuoentityServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LookupentityServices implements ILookuoentityServices {
	@Autowired
	private LookupentityRepository lookupentityrepository;
	public LookupEntity savelookupentity(LookupEntity lookupentity){
		lookupentity.setCreatedDatetime(new Date());
		lookupentityrepository.save(lookupentity);
		
		return lookupentity;
	}

}
