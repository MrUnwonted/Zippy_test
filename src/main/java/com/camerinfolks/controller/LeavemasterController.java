package com.camerinfolks.controller;

import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.model.Leavemaster;
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
@RequestMapping(value = "/leavemaster")
@EntityScan(basePackages = {"com.lifehis.*"})
@XmlRootElement
public class 	LeavemasterController {
	
	@Autowired
	BaseRepository baserepository;
	
	
	
	String adminquery= "SELECT c.`user_id`,c.`fullname` FROM crmuser c WHERE c.`category`=1 AND c.`user_id`!=1";
	String leavevaluesquery= "SELECT l.`lookupid`,l.`lookupvalue` FROM `lookupentity` l WHERE l.`lookupcategory`=5";
	@PostMapping(value = "/loadleavevalues",produces = "application/json")
	public ResponseEntity<String> loadLeavevalues() throws JSONException
	{	
		
	JSONObject output = new JSONObject();
		try{
			JSONObject response = new JSONObject();
			JSONArray responseArray = new JSONArray();
			JSONArray adminArray = new JSONArray();
		
			List <Object []> leavelist = baserepository.findQuery(leavevaluesquery ,new Object[]{});
			for(Object [] leavelistObj : leavelist)
			{
				response = new JSONObject();
				
				response.put("id", leavelistObj[0]);
				response.put("leave_types", leavelistObj[1]);
				
				responseArray.put(response);				
			}

			if (leavelist==null){
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			List <Object []> adminlist = baserepository.findQuery(adminquery ,new Object[]{});
			for(Object [] adminlistObj : adminlist)
			{
				response = new JSONObject();
				
				response.put("id", adminlistObj[0]);
				response.put("reporting_manager", adminlistObj[1]);
				
				adminArray.put(response);				
			}

			if (adminlist==null){
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

		output.put("leavetype_List", responseArray);
		output.put("admin_list", adminArray);
		
		
	} catch (JSONException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
	}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);		
	}

	
	
	
	@PostMapping(value = "/saveleave",produces = "application/json")
	public ResponseEntity<String> saveCategory(@RequestBody Leavemaster leavemaster) throws JSONException
	{		JSONObject response = new JSONObject();	
	try {
		
		baserepository.persist(leavemaster);


		response.put("response", "Success");

	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}
	



String holidayquery="SELECT hl.`id`,hl.`date`,hl.`occasion`,hl.`optional` FROM `holiday_list` hl";
@PostMapping(value = "/loadholidays",produces = "application/json")
public ResponseEntity<String> loadholidaylist(@RequestParam(required = false) boolean id  ) throws JSONException
{	
	
	
JSONObject output = new JSONObject();
	try{
		String appendQuery = " ";
		if(id==false)
		{
			appendQuery += " WHERE hl.`optional`=0 " ;
		}
		JSONObject response = new JSONObject();
		JSONArray responseArray = new JSONArray();
		JSONArray holidayArray = new JSONArray();
	
		List <Object []> holidaylist = baserepository.findQuery(holidayquery  + appendQuery ,new Object[]{});
		for(Object [] holidaylistObj : holidaylist)
		{
			response = new JSONObject();
			
			response.put("id", holidaylistObj[0]);
			response.put("date", holidaylistObj[1]);
			response.put("occasion", holidaylistObj[2]);
			response.put("optional", holidaylistObj[3]);
			
			responseArray.put(response);				
		}

		if (holidaylist==null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
			
	output.put("holiday_List", responseArray);
	
} catch (JSONException e) {
	e.printStackTrace();
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
}
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	
}




String loadleaveapplied="SELECT lm.`id`, trunc(lm.`start_date`),trunc(lm.`end_date`),lm.`reason`,c.`fullname`,l1.`lookupvalue`,CONCAT(l2.`lookupvalue`)"
		+ " FROM `leave_master` lm LEFT JOIN `crmuser` c ON c.`user_id`=lm.`reporting_manager`"
		+ " LEFT JOIN `lookupentity` l1 ON l1.`lookupid`=lm.`leave_type` "
		+ "LEFT JOIN `lookupentity` l2 ON l2.`lookupid`=lm.`leave_status`"
		+ " WHERE lm.`employee_id`=?";
@PostMapping(value = "/loadleaveapplied",produces = "application/json")
public ResponseEntity<String> loadLeaveappled(@RequestParam(name = "id") Long userId) throws JSONException
{	
	 
JSONObject output = new JSONObject();
	try{
		JSONObject response = new JSONObject();
		
		JSONArray leaveappliedArray = new JSONArray();




			List<Object[]> leaveaplliedlist = baserepository.findQuery(loadleaveapplied, new Object[]{userId});

			for (Object[] leaveaplliedlistObj : leaveaplliedlist) {
				response = new JSONObject();

				response.put("id", leaveaplliedlistObj[0]);
				response.put("start_date", leaveaplliedlistObj[1]);
				response.put("end_date", leaveaplliedlistObj[2]);
				response.put("reason", leaveaplliedlistObj[3]);
				response.put("fullname", leaveaplliedlistObj[4]);
				response.put("leave_type", leaveaplliedlistObj[5]);
				response.put("leave_status", leaveaplliedlistObj[6]);


				leaveappliedArray.put(response);
			}
	if (leaveaplliedlist==null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	
	
	output.put("leaveapplied_list", leaveappliedArray);
	
	
} catch (JSONException e) {
	e.printStackTrace();
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
}
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);		
}





String leaveappliedadmin="SELECT lm.`id`,lm.`start_date`,lm.`end_date`,lm.`reason`,l1.`lookupvalue`,c.`fullname` "
		+ "FROM `leave_master` lm LEFT JOIN `crmuser` c ON c.`user_id`=lm.`employee_id`"
		+ " LEFT JOIN `lookupentity` l1 ON l1.`lookupid`=lm.`leave_type` "
		+ "WHERE lm.`reporting_manager`=? AND lm.`leave_status`=27";
@PostMapping(value = "/leaveappliedadmin",produces = "application/json")
public ResponseEntity<String> leaveappliedadmin(@RequestParam(name = "id") Long userId) throws JSONException
{	
	 
JSONObject output = new JSONObject();
	try{
		JSONObject response = new JSONObject();
		
		JSONArray leaveappliedArray = new JSONArray();
	
		List <Object []> leaveaplliedlist = baserepository.findQuery(leaveappliedadmin ,new Object[]{userId});
		for(Object [] leaveaplliedlistObj : leaveaplliedlist)
		{
			response = new JSONObject();
			
			response.put("id", leaveaplliedlistObj[0]);
			response.put("start_date", leaveaplliedlistObj[1]);
			response.put("end_date", leaveaplliedlistObj[2]);
			response.put("reason", leaveaplliedlistObj[3]);
			response.put("leave_type", leaveaplliedlistObj[4]);
			response.put("fullname", leaveaplliedlistObj[5]);
			
			
			
			
			leaveappliedArray.put(response);				
		}

		if (leaveaplliedlist==null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	
	output.put("leaveappliedadmin_list", leaveappliedArray);
	
	
} catch (JSONException e) {
	e.printStackTrace();
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
}
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);		
}


String upadteleavequery="UPDATE `leave_master` SET `leave_status`=? WHERE `id`=?";
@PostMapping(value = "/upadteleave",produces = "application/json")
public ResponseEntity<String> upadteleavestatus(@RequestBody Leavemaster leavemaster) throws JSONException
{		JSONObject response = new JSONObject();	
try {
	Long id=leavemaster.getId();
	Long leave_status=leavemaster.getLeave_status();
	
	
	baserepository.updateQuery(upadteleavequery, new Object[]{leave_status,id});
//	(upadteleavequery,new Object[]{id,leave_status});


	response.put("response", "Success");

} catch (Exception e) {
	e.printStackTrace();
	response.put("response", "Failed");
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
}
return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

}



}









