package com.camerinfolks.controller;

import com.camerinfolks.model.core.LookupEntity;
import com.camerinfolks.service.impl.LookupentityServices;
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
@XmlRootElement
@RequestMapping(value = "/lookup")
@EntityScan(basePackages = {"com.lifehis.*"})
public class LookupentityController {
	
	@Autowired
	private LookupentityServices lookupentityservice;
	
	
	@PostMapping
	public ResponseEntity< String> savelookup(@RequestBody LookupEntity lookup) throws JSONException{
		JSONObject response = new JSONObject();
		try {	
			lookupentityservice.savelookupentity(lookup);
			response.put("response", "success");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("response", "Failed");
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
		
				
			
	}

}
