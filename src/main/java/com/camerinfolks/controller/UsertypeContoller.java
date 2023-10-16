package com.camerinfolks.controller;

import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.model.HisUsertypes;
import com.camerinfolks.repository.UsertypeRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@XmlRootElement
@RequestMapping(value = "/usertype")
@EntityScan(basePackages = {"com.lifehis.*"})
public class UsertypeContoller {
	@Autowired
	UsertypeRepository usertypeRepository;
	
	
	@Autowired BaseRepository baserepository;
	
	
	@PostMapping(value = "/getehisusertype",produces = "application/json")
	public ResponseEntity<String> getUsertype() throws JSONException
	{			
		List <HisUsertypes> hisgetuser = baserepository.find(HisUsertypes.class);
		JSONObject site = new JSONObject();
		JSONArray siteArray = new JSONArray();
		JSONObject response = new JSONObject();
		try {
			for(HisUsertypes hisusertype : hisgetuser)
			{
				site = new JSONObject();
				site.put("id", hisusertype.getId());
				site.put("usertype_name", hisusertype.getUsertype_name());
				
				siteArray.put(site);
			}
			response.put("Site_List", siteArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}
	
	
	
	
	@PostMapping(value = "/saveusertype",produces = "application/json")
	public ResponseEntity<String> saveusertype(@RequestBody HisUsertypes hisusertype) throws JSONException
	{		JSONObject response = new JSONObject();	
	try {
		
		
		usertypeRepository.saveAndFlush(hisusertype);


		response.put("response", "Sucess");

	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}
}