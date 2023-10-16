

package com.camerinfolks.controller;

import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.model.HISEmployee;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = "/reportingemployee")
@EntityScan(basePackages = {"com.lifehis.*"})
@XmlRootElement
public class Reportingcontroller {
	
	@Autowired BaseRepository baserepository;
	
	
	
	@PostMapping(value = "/getreportingemployee",produces = "application/json")
	public ResponseEntity<String> getreportemp() throws JSONException
	{			
		List <HISEmployee> reportemp = baserepository.find(HISEmployee.class);
		JSONObject site = new JSONObject();
		JSONArray siteArray = new JSONArray();
		JSONObject response = new JSONObject();
		try {
			for(HISEmployee empob : reportemp)
			{
				site = new JSONObject();
				site.put("id", empob.getId());
				site.put("reportemp_name", empob.getFirstname()+" "+empob.getLastname());
				
				siteArray.put(site);
			}
			response.put("Site_List", siteArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}

}
