package com.camerinfolks.controller;

import com.camerinfolks.repository.BaseRepository;
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
@RequestMapping(value = "/issueboardmaster")
@EntityScan(basePackages = {"com.lifehis.*"})
@XmlRootElement
public class IssueBoardController {
	
	@Autowired
	BaseRepository baseRepository;
	
	
	private String loadboardissues= "SELECT i.id,i.`siteid`,i.`issuecode`,i.`issuetype`,i.`priority`,i.`issuestatus`,"
			+ "lel.`lookupcode`, CONCAT(le2.lookupvalue), CONCAT(le3.lookupvalue, \"\"),iad.`issue_master_id`,s.`sitename`"
			+ " FROM `issue_master` i RIGHT JOIN `assigned_issue_details` iad ON  i.`id`=iad.`issue_master_id`"
			+"LEFT JOIN `lookupentity` lel ON lel.`lookupid`=i.`issuetype`"
			+ "LEFT JOIN `lookupentity` le2 ON le2.`lookupid`=i.`priority`"
			+ " LEFT JOIN `lookupentity` le3 ON le3.`lookupid`=i.`issuestatus`"
			+ " LEFT JOIN `sites` s ON s.`id`=i.`siteid`"
			+ " WHERE (iad.`employee_id`=? )  ";
	@PostMapping(value = "/loadissueboarddetails",produces = "application/json")
	public ResponseEntity<String> loadissueBoarddetails(@RequestParam (name = "id") Long id)  throws JSONException
	{	
//		List <Object []> empedit = baseRepository.findQuery(loadboardissues,new Object[] {id});
		
		JSONObject response = new JSONObject();
		JSONArray responseArray = new JSONArray();
		JSONObject output = new JSONObject();
		try {
			JSONArray issueopenArray = new JSONArray();
			JSONArray issuereleasePendingArray = new JSONArray();
			JSONArray issuereleasedArray = new JSONArray();
			JSONArray issueunderDevArray = new JSONArray();
			JSONArray issueDevcompArray = new JSONArray();
			JSONArray issueUnderTestingArray = new JSONArray();
			List <Object []> issueb1 = baseRepository.findQuery(loadboardissues, new Object[] {id});
			for(Object[] issueboardob : issueb1)
			{
				
				String status=(String) issueboardob[8];
				
				if(status.equals("OPEN")){
				
				response = new JSONObject();
			response.put("id",issueboardob[0] );
			response.put("siteid",issueboardob[1]);
			response.put("issuecode",issueboardob[2]);
			response.put("issuetypeid",issueboardob[3] );
			response.put("priorityid",issueboardob[4]);
			response.put("issuestatusid",issueboardob[5] );
			response.put("issuetype",issueboardob[6]);
			response.put("priority",issueboardob[7] );
			response.put("issuestatus",issueboardob[8]);
			response.put("sitename",issueboardob[10] );
			
			issueopenArray.put(response);
				}
				else if(status.equals("RELEASED"))
				{
					response = new JSONObject();
				
					response.put("id",issueboardob[0] );
					response.put("siteid",issueboardob[1]);
					response.put("issuecode",issueboardob[2]);
					response.put("issuetypeid",issueboardob[3] );
					response.put("priorityid",issueboardob[4]);
					response.put("issuestatusid",issueboardob[5] );
					response.put("issuetype",issueboardob[6]);
					response.put("priority",issueboardob[7] );
					response.put("issuestatus",issueboardob[8]);
					response.put("sitename",issueboardob[10] );
					
					issuereleasedArray.put(response);	
				}
				else if(status.equals("UNDER DEVELOPMENT"))
				{
					
					response = new JSONObject();
					response.put("id",issueboardob[0] );
					response.put("siteid",issueboardob[1]);
					response.put("issuecode",issueboardob[2]);
					response.put("issuetypeid",issueboardob[3] );
					response.put("priorityid",issueboardob[4]);
					response.put("issuestatusid",issueboardob[5] );
					response.put("issuetype",issueboardob[6]);
					response.put("priority",issueboardob[7] );
					response.put("issuestatus",issueboardob[8]);
					response.put("sitename",issueboardob[10] );
					
					issueunderDevArray.put(response);	
				}
				else if(status.equals("DEV COMPLETED"))
				{
					
					response = new JSONObject();
					response.put("id",issueboardob[0] );
					response.put("siteid",issueboardob[1]);
					response.put("issuecode",issueboardob[2]);
					response.put("issuetypeid",issueboardob[3] );
					response.put("priorityid",issueboardob[4]);
					response.put("issuestatusid",issueboardob[5] );
					response.put("issuetype",issueboardob[6]);
					response.put("priority",issueboardob[7] );
					response.put("issuestatus",issueboardob[8]);
					response.put("sitename",issueboardob[10] );
					
					issueDevcompArray.put(response);	
				}
				else if(status.equals("UNDER TESTING"))
				{
					
					response = new JSONObject();
					response.put("id",issueboardob[0] );
					response.put("siteid",issueboardob[1]);
					response.put("issuecode",issueboardob[2]);
					response.put("issuetypeid",issueboardob[3] );
					response.put("priorityid",issueboardob[4]);
					response.put("issuestatusid",issueboardob[5] );
					response.put("issuetype",issueboardob[6]);
					response.put("priority",issueboardob[7] );
					response.put("issuestatus",issueboardob[8]);
					response.put("sitename",issueboardob[10] );
					
					issueUnderTestingArray.put(response);	
				}
				else if(status.equals("RELEASE PENDING"))
				{
					
					response = new JSONObject();
					response.put("id",issueboardob[0] );
					response.put("siteid",issueboardob[1]);
					response.put("issuecode",issueboardob[2]);
					response.put("issuetypeid",issueboardob[3] );
					response.put("priorityid",issueboardob[4]);
					response.put("issuestatusid",issueboardob[5] );
					response.put("issuetype",issueboardob[6]);
					response.put("priority",issueboardob[7] );
					response.put("issuestatus",issueboardob[8]);
					response.put("sitename",issueboardob[10] );
					
					issuereleasePendingArray.put(response);	
				}
			
	
			
			}
			output.put("Assigned_List", issueopenArray);
			output.put("UnderDev_List", issueunderDevArray);
			output.put("Devcompleted_List", issueDevcompArray);
			output.put("Undertesting_List", issueUnderTestingArray);
			output.put("ReleasePend_List", issuereleasePendingArray);
			output.put("Released_List", issuereleasedArray);
			
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}

}
