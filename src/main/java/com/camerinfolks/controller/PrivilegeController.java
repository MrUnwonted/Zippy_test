package com.camerinfolks.controller;

import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.model.Privilege;
import com.camerinfolks.model.Privilegemap;
import com.camerinfolks.repository.PrivilegeRepository;
import com.camerinfolks.repository.PrivilegemapRepository;
import com.camerinfolks.utils.BeanUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;
@CrossOrigin(maxAge = 3600)
@RestController
@XmlRootElement
@RequestMapping(value = "/privilege")
@EntityScan(basePackages = {"com.lifehis.*"})
public class PrivilegeController {
	@Autowired
	PrivilegeRepository privilegeRepository;
	@Autowired
	PrivilegemapRepository privilegemapRepository ;
	
	
	@Autowired BaseRepository baserepository;
	
	@PostMapping(value = "/saveprivilege",produces = "application/json")
	public ResponseEntity<String> saveCategory(@RequestBody Privilege privilegeob) throws JSONException
	{		JSONObject response = new JSONObject();	
	try {
		
	privilegeob.setCreatedDatetime(new Date());
	
		privilegeRepository.saveAndFlush(privilegeob);


		response.put("response", "Sucess");

	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}
	
	
	
	
	@PostMapping(value = "/getprivilege",produces = "application/json")
	public ResponseEntity<String> getreportemp() throws JSONException
	{			
		List <Privilege> privilege = baserepository.find(Privilege.class);
		JSONObject site = new JSONObject();
		JSONArray siteArray = new JSONArray();
		JSONObject response = new JSONObject();
		try {
			for(Privilege privilegeob : privilege)
			{
				site = new JSONObject();
				site.put("id", privilegeob.getId());
				site.put("privilege_name",privilegeob.getPrivilegename());
				
				siteArray.put(site);
			}
			response.put("Privilege_List", siteArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}
	
	String checkprivilage= "SELECT * FROM `privilege_map` p WHERE p.`employee_cat_id` = ? AND p.`privilege_id` = ?";
	
	@PostMapping(value = "/saveprivilegemap",produces = "application/json")
	public ResponseEntity<String> savePrivilegemap(@RequestBody Privilegemap privilegemapob) throws JSONException
	{		JSONObject response = new JSONObject();	
	List<?> existcheck = baserepository.findQuery(checkprivilage,new Object[] {privilegemapob.getEmpactid(),privilegemapob.getPrivilegeid()});
	if(BeanUtils.isNullOrEmpty(existcheck)){
	try {
		
//	privilegeob.setCreatedDatetime(new Date());

	
		
		privilegemapRepository.saveAndFlush(privilegemapob);


		response.put("response", "Sucess");

	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}
	
//	@PostMapping(value = "/saveprivilegemap",produces = "application/json")
//	public ResponseEntity<String> saveprivilegemap() throws JSONException
//	{			
//		List <Privilege> privilege = baserepository.find(Privilegemap.class);
//		JSONObject site = new JSONObject();
//		JSONArray siteArray = new JSONArray();
//		JSONObject response = new JSONObject();
//		try {
//			for(Privilegemap privilegemapob : privilege)
//			{
//				site = new JSONObject();
//				site.put("id", privilegeob.getId());
//				site.put("privilege_name",privilegeob.getPrivilegename());
//				
//				siteArray.put(site);
//			}
//			response.put("Privilege_List", siteArray);
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
//		}
//		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
//
//	}
	
	
	
}