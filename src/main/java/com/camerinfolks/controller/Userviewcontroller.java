package com.camerinfolks.controller;

import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.model.HISClients;
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
@RequestMapping(value = "/userview")
@EntityScan(basePackages = {"com.lifehis.*"})
@XmlRootElement
public class Userviewcontroller {
	@Autowired
	BaseRepository baseRepository;
	String loaduserquery ="SELECT h.`id`,c.`fullname`, c.`user_name`,e.`employee_type` FROM `crmuser` c LEFT JOIN `his_employee` h ON h.`userid`=c.`user_id` INNER JOIN `employee_type` e ON h.`employee_type`=e.`id`ORDER BY  c.`fullname` ASC";
	@PostMapping(value = "/getallusers",produces = "application/json")
	public ResponseEntity<String> getallusers()throws JSONException{
	JSONObject response = new JSONObject();
	JSONArray responseArray = new JSONArray();
	JSONObject output = new JSONObject();
	try {
		List <Object []> userlist = baseRepository.findQuery(loaduserquery,new Object[] {});
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
	
	
	@PostMapping(value = "/getallclients",produces = "application/json")
	public ResponseEntity<String> getallclients()throws JSONException{
		List <HISClients> clientlist = baseRepository.find(HISClients.class);
	JSONObject response = new JSONObject();
	JSONArray responseArray = new JSONArray();
	JSONObject output = new JSONObject();
	try {
	
	
			for(HISClients clientob : clientlist)
			{
				response = new JSONObject();
				response.put("id", clientob.getId());
				response.put("reportemp_name", clientob.getFirstname()+ clientob.getLastname());
				response.put("email", clientob.getEmail());
				responseArray.put(response);
		}
	
		output.put("Client_List", responseArray);
	} catch (JSONException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

}
	
	
	
	
	String loadclientedit="SELECT h.`id`, h.`first_name`,h.`last_name`,c.`user_name`,c.`password`,h.`email`,h.`userid`,h.`mobile`,c.`usertype` FROM crmuser c  INNER JOIN his_clients h ON h.`userid`=c.`user_id` WHERE h.`id`=? ";
	@PostMapping(value = "/getclientedit",produces = "application/json")
	public ResponseEntity<String> getclientsedit(@RequestParam (name = "id") Long id)throws JSONException{
	JSONObject response = new JSONObject();
	JSONArray responseArray = new JSONArray();
	JSONObject output = new JSONObject();
	try {
		List <Object []> clientedit = baseRepository.findQuery(loadclientedit,new Object[] {id});
		for(Object[] clienteditOb : clientedit)
		{
			response = new JSONObject();
			response.put("id",clienteditOb[0] );
			response.put("firstname",clienteditOb[1]);
			response.put("lastname",clienteditOb[2]);
			response.put("username",clienteditOb[3]);
			response.put("password",clienteditOb[4]);
		
			response.put("email",clienteditOb[5]);
			response.put("userid",clienteditOb[6]);
			response.put("mobile",clienteditOb[7]);
			response.put("usertype", clienteditOb[8]);
		
			
			responseArray.put(response);
		}
		output.put("Clientedit_List", responseArray);
	} catch (JSONException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

}
	




String loademployeeedit="SELECT h.`id`, h.`first_name`,h.`last_name`,c.`user_name`,c.`password`,h.`email`,h.`userid`,h.`mobile`,h.`employee_type`,h.`reporting_employee`,c.`category` FROM crmuser c  INNER JOIN his_employee h ON h.`userid`=c.`user_id` WHERE h.`id`=?   ";
@PostMapping(value = "/getemployeeedit",produces = "application/json")
public ResponseEntity<String> getemployeeedit(@RequestParam (name = "id") Long id)throws JSONException{
JSONObject response = new JSONObject();
JSONArray responseArray = new JSONArray();
JSONObject output = new JSONObject();
try {
	List <Object []> empedit = baseRepository.findQuery(loademployeeedit,new Object[] {id});
	for(Object[] empeditOb : empedit)
	{
		response = new JSONObject();
		response.put("id",empeditOb[0] );
		response.put("firstname",empeditOb[1]);
		response.put("lastname",empeditOb[2]);
		response.put("username",empeditOb[3]);
		response.put("password",empeditOb[4]);
		response.put("email",empeditOb[5]);
		response.put("userid",empeditOb[6]);
		response.put("mobile",empeditOb[7]);
		response.put("employee_type",empeditOb[8]);
		response.put("reporting_employee",empeditOb[9]);
		response.put("employee_category",empeditOb[10]);
//		response.put("usertype", empeditOb[11]);
		
		
	
		
		responseArray.put(response);
	}
	output.put("Employeeedit_List", responseArray);
} catch (JSONException e) {
	e.printStackTrace();
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
}
return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

}

}
