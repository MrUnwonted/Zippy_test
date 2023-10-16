package com.camerinfolks.controller;

import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.model.EmployeeDesignation;
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
@XmlRootElement
@RequestMapping(value = "/employeedesignation")
@EntityScan(basePackages = {"com.lifehis.*"})
public class EmployeeDesignationController {
	
	
	
	@Autowired BaseRepository baserepository;
	
	
	@PostMapping(value = "/getemployeeedesignation",produces = "application/json")
	public ResponseEntity<String> getEmpdesg() throws JSONException
	{			
		List <EmployeeDesignation> empdesg = baserepository.find(EmployeeDesignation.class);
		JSONObject site = new JSONObject();
		JSONArray siteArray = new JSONArray();
		JSONObject response = new JSONObject();
		try {
			for(EmployeeDesignation empob : empdesg)
			{
				site = new JSONObject();
				site.put("id", empob.getId());
				site.put("employee_type", empob.getEmployee_type());
				
				siteArray.put(site);
			}
			response.put("Site_List", siteArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}

	String loaduserquery ="SELECT c.`user_id`,c.`fullname`, c.`user_name`,e.`employee_type` FROM `crmuser` c LEFT JOIN `his_employee` h ON h.`userid`=c.`user_id` INNER JOIN `employee_type` e ON h.`employee_type`=e.`id`";
	@PostMapping(value = "/getallusers",produces = "application/json")
	public ResponseEntity<String> getallusers()throws JSONException{
	JSONObject response = new JSONObject();
	JSONArray responseArray = new JSONArray();
	JSONObject output = new JSONObject();
	try {
		List <Object []> userlist = baserepository.findQuery(loaduserquery,new Object[] {});
		for(Object[] userListOb : userlist)
		{
			response = new JSONObject();
			response.put("id",userListOb[0] );
			response.put("fullname",userListOb[1]);
			response.put("username",userListOb[2]);
			response.put("designation",userListOb[3]);
		
			
			responseArray.put(response);
		}
		output.put("User_List", responseArray);
	} catch (JSONException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

}
	

}