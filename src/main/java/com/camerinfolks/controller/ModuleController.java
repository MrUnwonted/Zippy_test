package com.camerinfolks.controller;

import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.model.ModuleMaster;
import com.camerinfolks.repository.ModuleRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.annotation.XmlRootElement;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = "/modulemaster")
@EntityScan(basePackages = {"com.lifehis.*"})
@XmlRootElement
public class ModuleController {
	
	
	@Autowired
	BaseRepository repository;
	
	@Autowired
	ModuleRepository modulerepository;
	
	@PostMapping(value = "/savemodule",produces = "application/json")
	public ResponseEntity<String> saveCategory(@RequestBody ModuleMaster module) throws JSONException
	{		JSONObject response = new JSONObject();	
	try {
		
//		module.setCreatedDatetime(new Date());
//	    module.setCreatedBy((long) 1);
		modulerepository.saveAndFlush(module);


		response.put("response", "Sucess");

	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}

}
