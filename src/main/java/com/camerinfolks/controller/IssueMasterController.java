package com.camerinfolks.controller;


import com.camerinfolks.model.*;
import com.camerinfolks.repository.*;
import com.camerinfolks.model.core.IssueSearchCriteria;
import com.camerinfolks.model.core.QueryParameters;
import com.camerinfolks.model.core.StartupProperties;
import com.camerinfolks.response.HisIssueMasterResponse;
import com.camerinfolks.service.IUserControllerServices;
import com.camerinfolks.utils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(value = "/issuemaster")
@EntityScan(basePackages = {"com.lifehis.*"})
@XmlRootElement
public class IssueMasterController {
	
	@Autowired
	IssueMasterRepository issueMasterRepository;
	
	@Autowired
	HISEmployeeRepository hisEmployeeRepository;
	
	@Autowired
	ModuleRepository modulerepository;
	
	@Autowired
	IUserControllerServices userControllerServices;
	
	@Autowired
	BaseRepository baseRepository;
	
	@Autowired
	MobileFireBaseNotification mobileFireBaseNotification;
	
	@Autowired
	FcmtokenMapRepository fcmtokenMapRepository;
	
	@Autowired
	private StartupProperties startupProperties;
	
	@Autowired
	private HisSitesRepository hisrepository;
	
	@PostMapping(value = "/getallissues",produces = "application/json")
	public ResponseEntity<HisIssueMasterResponse> getAllIssues(@RequestBody QueryParameters queryParam)
	{
		return new ResponseEntity<HisIssueMasterResponse>(getAllIssues(queryParam.getOffset(),queryParam.getPageSize(),queryParam.getSortBy(),queryParam.getSortAsc()),HttpStatus.OK);
	}
	
	public HisIssueMasterResponse getAllIssues(int pageNo,int pageSize,String sortBy,int sortAsc) {
		Sort sort = sortAsc == 1 ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageble = PageRequest.of(pageNo, pageSize,sort);
		Page<IssueMaster> posts =  issueMasterRepository.findAll(pageble);
		//get Content From page Object
		List<IssueMaster> listPost = posts.getContent();
		HisIssueMasterResponse response = new HisIssueMasterResponse();
		response.setContent(listPost);
		response.setPageNo(posts.getNumber());
		response.setPageSize(posts.getSize());
		response.setTotalElement(posts.getTotalElements());
		response.setTotalPages(posts.getTotalPages());
		response.setIsLast(posts.isLast());
		return response;
	}
	
	private String loadIssuesQuery = "SELECT i.id,s.id AS siteid,s.sitename,i.description,i.issuetype,le1.lookupvalue,i.priority,CONCAT(le2.lookupvalue), u.usertype,"
			+ "CONCAT(hc.first_name,' ',hc.last_name) AS clientname,CONCAT(he.first_name,' ',he.last_name) AS employeename, i.issuecode,CONCAT(le3.lookupvalue,\"\"), "
			+ " trunc(i.createddatetime)"
			+ " FROM issue_master i LEFT JOIN lookupentity le1 ON le1.lookupid = i.issuetype LEFT JOIN lookupentity le2 ON le2.lookupid "
			+ "= i.priority LEFT JOIN sites s ON s.id = i.siteid LEFT JOIN crmuser u ON u.user_id = i.createdby LEFT JOIN his_clients hc"
			+ " ON hc.userid = u.user_id LEFT JOIN his_employee he ON he.userid = u.user_id LEFT JOIN lookupentity le3 ON le3.lookupid = i.issuestatus where u.usertype is not null AND i.`isactive`=1 ";
	@PostMapping(value = "/loadissues",produces = "application/json")
	public ResponseEntity<String> loadIssues(@RequestBody IssueSearchCriteria searchCriteria) throws JSONException
	{
		LocalDate today = LocalDate.now();
		JSONObject response = new JSONObject();
		JSONArray responseArray = new JSONArray();
		JSONObject output = new JSONObject();
		try {
			String appendQuery = " ";
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getSiteid()))
			{
				appendQuery += " and s.id = " + searchCriteria.getSiteid();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getDescription()))
			{
				appendQuery += " and i.description like \"%" + searchCriteria.getDescription()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuetype()))
			{
				appendQuery += " and i.issuetype = " + searchCriteria.getIssuetype();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getPriority()))
			{
				appendQuery += " and i.priority = " + searchCriteria.getPriority();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getCreatedby()))
			{
				appendQuery += " and u.user_id = " + searchCriteria.getCreatedby();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuecode()))
			{
				appendQuery += " and i.issuecode like  \"%" + searchCriteria.getIssuecode()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getYear()))
			{
				appendQuery += " and YEAR(i.createddatetime) = " + searchCriteria.getYear();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getMonth()))
			{
				appendQuery += " and MONTH(i.createddatetime) = " + searchCriteria.getMonth();
			}
			else{
				appendQuery += " and MONTH(i.createddatetime) = " + today.getMonthValue();
			}
			if(!BeanUtils.isNull(searchCriteria.getCreatedon()))
			{
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			    String createdon = df.format(searchCriteria.getCreatedon());
				appendQuery += " and DATE_FORMAT(i.createddatetime,'%Y-%m-%d')= DATE_FORMAT( '" + createdon +"','%Y-%m-%d') ";
			}
//			else
//			{
//				appendQuery += " and DATE_FORMAT(i.createddatetime,'%Y-%m-%d')= DATE_FORMAT(NOW(),'%Y-%m-%d') ";
//			}
			if(!BeanUtils.isNullOrZero(searchCriteria.getIssuestatus()))
			{
				appendQuery += " and i.issuestatus = " + searchCriteria.getIssuestatus();
			}
			 appendQuery += " order by i.id desc ";
			List <Object []> loadIssues = baseRepository.findQuery(loadIssuesQuery + appendQuery,new Object[]{});
			for(Object[] issuesListOb : loadIssues)
			{
				response = new JSONObject();
				response.put("id",issuesListOb[0] );
				response.put("siteid",issuesListOb[1]);
				response.put("sitename",issuesListOb[2]);
				response.put("description",issuesListOb[3]);
				response.put("issuetypeId",issuesListOb[4]);
				response.put("issuetype",issuesListOb[5]);
				response.put("priorityid",issuesListOb[6]);
				response.put("priority",issuesListOb[7]);
				response.put("usertype",issuesListOb[8]);
				/// usertype 2 ::: client 
				if(Integer.parseInt(issuesListOb[8].toString()) == 1)
				{				
					response.put("name",issuesListOb[10]);
				}

				else if(Integer.parseInt(issuesListOb[8].toString()) == 2)
				{
					response.put("name",issuesListOb[9]);
				}
				response.put("issuecode",issuesListOb[11]);
				response.put("issuestatus",issuesListOb[12]);
				response.put("created_on", issuesListOb[13]);
				responseArray.put(response);
			}
			
			List <HISSites> sites = baseRepository.find(HISSites.class);
			JSONObject site = new JSONObject();
			JSONArray siteArray = new JSONArray();
			for(HISSites siteOb : sites)
			{
				site = new JSONObject();
				site.put("id", siteOb.getId());
				site.put("name", siteOb.getSitename());
				site.put("code", siteOb.getSitecode());
				siteArray.put(site);				
			}
			
			JSONObject issueTypeOb = new JSONObject();
			JSONArray issueTypeArray = new JSONArray();
			List <Object []> issueType = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_TYPE"});
			for(Object [] issueTypeObj : issueType)
			{
				issueTypeOb = new JSONObject();
				issueTypeOb.put("id", issueTypeObj[0]);
				issueTypeOb.put("code", issueTypeObj[1]);
				issueTypeOb.put("value", issueTypeObj[2]);
				issueTypeArray.put(issueTypeOb);				
			}
			JSONObject priorityOb = new JSONObject();
			JSONArray priorityArray = new JSONArray();
			List <Object []> priority = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"PRIORITY"});
			for(Object [] priorityObj : priority)
			{
				priorityOb = new JSONObject();
				priorityOb.put("id", priorityObj[0]);
				priorityOb.put("code", priorityObj[1]);
				priorityOb.put("value", priorityObj[2]);
				priorityArray.put(priorityOb);				
			}
			JSONObject issuestatusOb = new JSONObject();
			JSONArray issuestatusArray = new JSONArray();
			List <Object []> issuestatus = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_STATUS"});
			for(Object [] priorityObj : issuestatus)
			{
				issuestatusOb = new JSONObject();
				issuestatusOb.put("id", priorityObj[0]);
				issuestatusOb.put("code", priorityObj[1]);
				issuestatusOb.put("value", priorityObj[2]);
				issuestatusArray.put(issuestatusOb);				
			}
			output.put("Issue_List", responseArray);
			output.put("Issue_type_list", issueTypeArray);
			output.put("Priority", priorityArray);
			output.put("Issue_Status", issuestatusArray);
			output.put("sitelist", siteArray);
			
			List<Object[]> totalCountList = issueMasterRepository.countIssueMaster();
			List<Object[]> totalCountMonthList = issueMasterRepository.countIssueMasterThisMonth();
			List<Object[]> totalCountRequirementList = issueMasterRepository.countIssueMasterTotalRequirements();
			List<Object[]> totalCountBugsList = issueMasterRepository.countIssueMasterTotalBugs();
			List<Object[]> totalCountResolvedList = issueMasterRepository.countIssueMasterTotalResolved();
			output.put("totalCountList", totalCountList.get(0)[0]);
			output.put("totalCountMonthList", totalCountMonthList.get(0)[0]);
			output.put("totalCountRequirementList", totalCountRequirementList.get(0)[0]);
			output.put("totalCountBugsList", totalCountBugsList.get(0)[0]);
			output.put("totalCountResolvedList", totalCountResolvedList.get(0)[0]);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}

	String modulequery= "SELECT m.`module_name` FROM `modules` m";
	String createIssueDefaultValues= "SELECT le.lookupid,le.lookupcode,le.lookupvalue FROM lookupentity le LEFT JOIN lookupcategory lc ON le.lookupcategory = lc.categoryid WHERE lc.categoryname = ? order by le.lookupvalue ASC";
	@GetMapping(value = "/loadCreateIssueDetails",produces = "application/json")
	public ResponseEntity<String> loadCreateIssueDetails() throws JSONException
	{		
		JSONObject output = new JSONObject();
		try {
			List<HISSites> sites = hisrepository.findAllByOrderBySitenameAsc();
			JSONObject site = new JSONObject();
			JSONArray siteArray = new JSONArray();
			for(HISSites siteOb : sites)
			{
				site = new JSONObject();
				site.put("id", siteOb.getId());
				site.put("name", siteOb.getSitename());
				site.put("code", siteOb.getSitecode());
				siteArray.put(site);				
			}
			
			JSONObject issueTypeOb = new JSONObject();
			JSONArray issueTypeArray = new JSONArray();
			List <Object []> issueType = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_TYPE"});
			for(Object [] issueTypeObj : issueType)
			{
				issueTypeOb = new JSONObject();
				issueTypeOb.put("id", issueTypeObj[0]);
				issueTypeOb.put("code", issueTypeObj[1]);
				issueTypeOb.put("value", issueTypeObj[2]);
				issueTypeArray.put(issueTypeOb);				
			}
			JSONObject priorityOb = new JSONObject();
			JSONArray priorityArray = new JSONArray();
			List <Object []> priority = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"PRIORITY"});
			for(Object [] priorityObj : priority)
			{
				priorityOb = new JSONObject();
				priorityOb.put("id", priorityObj[0]);
				priorityOb.put("code", priorityObj[1]);
				priorityOb.put("value", priorityObj[2]);
				priorityArray.put(priorityOb);				
			}
			JSONObject issuestatusOb = new JSONObject();
			JSONArray issuestatusArray = new JSONArray();
			List <Object []> issuestatus = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_STATUS"});
			for(Object [] priorityObj : issuestatus)
			{
				issuestatusOb = new JSONObject();
				issuestatusOb.put("id", priorityObj[0]);
				issuestatusOb.put("code", priorityObj[1]);
				issuestatusOb.put("value", priorityObj[2]);
				issuestatusArray.put(issuestatusOb);				
			}
			
					
				List <ModuleMaster> modulelist = baseRepository.find(ModuleMaster.class);
				JSONObject module = new JSONObject();
				JSONArray moduleArray = new JSONArray();
			
			
					for(ModuleMaster modulelistob : modulelist)
					{
						module = new JSONObject();
						module.put("module_name", modulelistob.getModule_name());
						module.put("id", modulelistob.getId());
						
						moduleArray.put(module);
					}
					
			
				

			
			List <HISEmployee> devList = hisEmployeeRepository.findByemployeetype(2l);
			//get Content From page Object
//			List<HISSites> listPost = posts.getContent();
			List <HISEmployee> testList = hisEmployeeRepository.findByemployeetype(3l);
			List <HISEmployee> impList = hisEmployeeRepository.findByemployeetype(4l);

			output.put("dev_List",convertHisEmployeetoJson(devList));
			output.put("testList",convertHisEmployeetoJson(testList));
			output.put("impList", convertHisEmployeetoJson(impList));
			output.put("Issue_type_list", issueTypeArray);
			output.put("Priority", priorityArray);
			output.put("Issue_Status", issuestatusArray);
			output.put("sitelist", siteArray);
			output.put("modulelist", moduleArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}
	

	@PostMapping(value = "/createIssue",produces = "application/json")
	public ResponseEntity<String> createIssue(@RequestBody IssueMaster issue) throws JSONException
	{		JSONObject response = new JSONObject();	
	try {
		issue.setCreatedDatetime(new Date());
		issue.setIssuecode(userControllerServices.createIssueCode(issue, 1l));
		issue.setIssuestatus(7l);
		if(!BeanUtils.isNullOrEmpty(issue.getUploadFileName()))
		{
			String filename = issue.getUploadFileName() +"_"+ System.currentTimeMillis();
			issue.setUploadFileName(filename);
			byte[] bytes = issue.getUploadFileData();
			writeBytesToFileApache(startupProperties.getprofileImages() + filename , bytes);
		}
//		issue.getDalist().clear();
//		DevDto d = new DevDto();
//		d.setDeveloper(3l);
//		DeveloperAssign da = new DeveloperAssign();
		
//		
//		issue.getDalist().add(a);
		baseRepository.persist(issue);
		
		if(!BeanUtils.isNullOrEmpty(issue.getDalist()) 
//				|| !BeanUtils.isNullOrEmpty(issue.getTestlist()) || !BeanUtils.isNullOrEmpty(issue.getImplist())
				)
		{
			Assinedissuesdetail developerAssign = new Assinedissuesdetail();
			
		
			
			List<Assinedissuesdetail> danewlist= issue.getDalist() ;
	
			
			
			
			for (Assinedissuesdetail  daob :  danewlist ){
				developerAssign = new Assinedissuesdetail();
				developerAssign.setIssueMasterId(issue.getId());
				developerAssign.setEmployee_id(daob.getEmployee_id());
				developerAssign.setEmployee_type((long) 2);
				developerAssign.setCreated_datetime(new Date());
				developerAssign.setDuedate(issue.getExpectedReleasedate());
				baseRepository.persist(developerAssign);
			}
		}
		 if(!BeanUtils.isNullOrEmpty(issue.getTestlist())){
			 Assinedissuesdetail testerAssign = new Assinedissuesdetail();
			List<Assinedissuesdetail> tanewlist= issue.getTestlist() ;
			
			for (Assinedissuesdetail  taob :  tanewlist ){
				testerAssign = new Assinedissuesdetail();
				testerAssign.setIssueMasterId(issue.getId());
				testerAssign.setEmployee_id(taob.getEmployee_id());
				testerAssign.setEmployee_type((long) 3);
				testerAssign.setCreated_datetime(new Date());
				testerAssign.setDuedate(issue.getExpectedReleasedate());
				baseRepository.persist(testerAssign);
			}
		}
			
		 if(!BeanUtils.isNullOrEmpty(issue.getImplist())){
			 Assinedissuesdetail implementerAssign = new Assinedissuesdetail();
			
			List<Assinedissuesdetail> imanewlist= issue.getImplist() ;
			
			for (Assinedissuesdetail  impob :  imanewlist ){
				implementerAssign = new Assinedissuesdetail();
				implementerAssign.setIssueMasterId(issue.getId());
				implementerAssign.setEmployee_id(impob.getEmployee_id());
				implementerAssign.setEmployee_type((long) 4);
				implementerAssign.setCreated_datetime(new Date());
				implementerAssign.setDuedate(issue.getExpectedReleasedate());
				baseRepository.persist(implementerAssign);
			}
		}
			
			
			
			
			
			
			

			
		
		response.put("response", "Sucess");
		response.put("issuecode", issue.getIssuecode());

	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}
	
    private static void writeBytesToFileApache(String fileOutput, byte[] bytes)
            throws IOException {

            FileUtils.writeByteArrayToFile(new File(fileOutput), bytes);

        }
	
    
// load assigned details of issues    
    
	String loadAssignIssueValues= "SELECT s.sitename,le1.lookupid AS issuetypeid,le1.lookupvalue AS issuetype,le2.lookupid AS issuePriorityid,"
			+ "CONCAT(le2.lookupvalue) AS issuePriority,le3.lookupid AS issueStatusid,CONCAT(le3.lookupvalue,\"\") AS issueStatus,\r\n"
			+ "i.description,i.issuecode,i.createddatetime,c4.fullname,i.uploadfilename,"
//			+ "ia.id,ia.developer,ia.tester,ia.implementer,"
			+ "i.expected_release_date,i.tester_date,i.developer_date\r\n"
			+ " FROM issue_master i\r\n"
//			+ "LEFT JOIN issue_assign_details ia ON i.id = ia.issue_master_id\r\n"
//			+ "LEFT JOIN crmuser c1 ON c1.user_id = ia.developer\r\n"
//			+ "LEFT JOIN crmuser c2 ON c2.user_id = ia.tester\r\n"
//			+ "LEFT JOIN crmuser c3 ON c3.user_id = ia.implementer\r\n"
			+ "LEFT JOIN crmuser c4 ON c4.user_id = i.createdby\r\n"
			+ "LEFT JOIN sites s ON s.id = i.siteid\r\n"
			+ "LEFT JOIN lookupentity le1 ON le1.lookupid = i.issuetype \r\n"
			+ "LEFT JOIN lookupentity le2 ON le2.lookupid = i.priority\r\n"
			+ "LEFT JOIN lookupentity le3 ON le3.lookupid = i.issuestatus WHERE i.id = ? AND i.`isactive`=1 ORDER BY i.createddatetime DESC";
	
      String loadassigneddetails = "SELECT aid.id,aid.employee_type,aid.employee_id FROM `assigned_issue_details` aid WHERE aid.issue_master_id = ? ";
	
	
	String loaddeveloperassigned= " SELECT 	ida.id,ida.developer FROM `developer_assignedissue` ida  WHERE ida.`issue_master_id`= ? " ;
	String loadtesterassigned= "SELECT 	ita.id,ita.`tester` FROM `tester_assignedissue` ita  WHERE ita.`issue_master_id`= ?   ";
	String loadimplementerassigned= " SELECT 	iia.id,iia.implementer FROM  `implementer_assignedissue` iia  WHERE iia.`issue_master_id`= ? ";
	@GetMapping(value = "/loadAssignIssueValues",produces = "application/json")
	public ResponseEntity<String> loadAssignIssueValues(@RequestParam(name = "id") Long issueId) throws JSONException,IOException
	{		
		JSONObject output = new JSONObject();
		try {
			JSONObject assignedDetOb = new JSONObject();
			JSONArray assignedDetArray = new JSONArray();
			List <Object []> issueDetails = baseRepository.findQuery(loadAssignIssueValues,new Object[] {issueId});
			for(Object[] issueObj: issueDetails)
			{
				assignedDetOb = new JSONObject();
				assignedDetOb.put("sitename", issueObj[0]);
				assignedDetOb.put("issuetypeid", issueObj[1]);
				assignedDetOb.put("issuetype", issueObj[2]);
				assignedDetOb.put("issuePriorityid", issueObj[3]);
				assignedDetOb.put("issuePriority", issueObj[4]);
				assignedDetOb.put("issueStatusid", issueObj[5]);
				assignedDetOb.put("issueStatus", issueObj[6]);
				assignedDetOb.put("description", issueObj[7]);
				assignedDetOb.put("issuecode", issueObj[8]);
				assignedDetOb.put("createddatetime", issueObj[9]);
				assignedDetOb.put("fullname", issueObj[10]);
				if(!BeanUtils.isNull(issueObj[11]))
				{
					assignedDetOb.put("filename", issueObj[11]);
					File resourceFile = new File("D://image/"+issueObj[11].toString());	
					String encodedText =new String(Base64.encodeBase64(FileUtils.readFileToByteArray(resourceFile)));
					assignedDetOb.put("fileBytes",encodedText);
				}
				else
				{
					assignedDetOb.put("filename", "");
					assignedDetOb.put("fileBytes","");
				}
//				assignedDetOb.put("issueassigned_id",issueObj[12]);
//				assignedDetOb.put("developerid",issueObj[13]);
//				assignedDetOb.put("testerid",issueObj[14]);
//				assignedDetOb.put("implementerid",issueObj[15]);
				assignedDetOb.put("expectedReleasedate", issueObj[12]);
				assignedDetOb.put("developerdate", issueObj[13]);
				assignedDetOb.put("testerdate", issueObj[14]);
				assignedDetArray.put(assignedDetOb);
			}
			
			
			
			
			
			
			JSONObject devassignedOb = new JSONObject();
			JSONArray devassignedArray = new JSONArray();
			JSONObject testassignedOb = new JSONObject();
			JSONArray testassignedArray = new JSONArray();
			JSONObject impassignedOb = new JSONObject();
			JSONArray impassignedArray = new JSONArray();

			List <Object []> assignedissueDetails = baseRepository.findQuery(loadassigneddetails,new Object[] {issueId});

			if(!BeanUtils.isNullOrEmpty(assignedissueDetails)){
			for(Object[] issueObj: assignedissueDetails)
			{
				devassignedOb = new JSONObject();
				if(issueObj[1].toString().equalsIgnoreCase("2")){
				devassignedOb.put("id", issueObj[0]);
				devassignedOb.put("developer_id", issueObj[2]);
				devassignedArray.put(devassignedOb);
				}
				
				
				testassignedOb = new JSONObject();
				if(issueObj[1].toString().equalsIgnoreCase("3")){
					testassignedOb.put("id", issueObj[0]);
					testassignedOb.put("tester_id", issueObj[2]);
					testassignedArray.put(testassignedOb);
					}
				
				
				impassignedOb = new JSONObject();
				if(issueObj[1].toString().equalsIgnoreCase("4"))
					
				   {
					impassignedOb.put("id", issueObj[0]);
					impassignedOb.put("imp_id", issueObj[2]);
					impassignedArray.put(impassignedOb);
					}
				
			}
			
			}
			
			
			
			
			
			
			
//			List <Object []> testissueDetails = baseRepository.findQuery(loadtesterassigned,new Object[] {issueId});
//			for(Object[] issueObj: testissueDetails)
//			{
//				testassignedOb = new JSONObject();
//				testassignedOb.put("id", issueObj[0]);
//				testassignedOb.put("tester_id", issueObj[1]);
//				testassignedArray.put(testassignedOb);
//			}
//			
//		
//			List <Object []> impissueDetails = baseRepository.findQuery(loadimplementerassigned,new Object[] {issueId});
//			for(Object[] issueObj: impissueDetails)
//			{
//				impassignedOb = new JSONObject();
//				impassignedOb.put("id", issueObj[0]);
//				impassignedOb.put("imp_id", issueObj[1]);
//				impassignedArray.put(impassignedOb);
//			}
			
			
			
			JSONObject issueTypeOb = new JSONObject();
			JSONArray issueTypeArray = new JSONArray();
			List <Object []> issueType = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_TYPE"});
			for(Object [] issueTypeObj : issueType)
			{
				issueTypeOb = new JSONObject();
				issueTypeOb.put("id", issueTypeObj[0]);
				issueTypeOb.put("code", issueTypeObj[1]);
				issueTypeOb.put("value", issueTypeObj[2]);
				issueTypeArray.put(issueTypeOb);				
			}
			JSONObject priorityOb = new JSONObject();
			JSONArray priorityArray = new JSONArray();
			List <Object []> priority = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"PRIORITY"});
			for(Object [] priorityObj : priority)
			{
				priorityOb = new JSONObject();
				priorityOb.put("id", priorityObj[0]);
				priorityOb.put("code", priorityObj[1]);
				priorityOb.put("value", priorityObj[2]);
				priorityArray.put(priorityOb);				
			}
			JSONObject issuestatusOb = new JSONObject();
			JSONArray issuestatusArray = new JSONArray();
			List <Object []> issuestatus = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_STATUS"});
			for(Object [] issuestatusObj : issuestatus)
			{
				issuestatusOb = new JSONObject();
				issuestatusOb.put("id", issuestatusObj[0]);
				issuestatusOb.put("code", issuestatusObj[1]);
				issuestatusOb.put("value", issuestatusObj[2]);
				issuestatusArray.put(issuestatusOb);				
			}
			List <HISEmployee> devList = hisEmployeeRepository.findByemployeetype(2l);
			//get Content From page Object
//			List<HISSites> listPost = posts.getContent();
			List <HISEmployee> testList = hisEmployeeRepository.findByemployeetype(3l);
			List <HISEmployee> impList = hisEmployeeRepository.findByemployeetype(4l);

			output.put("dev_List",convertHisEmployeetoJson(devList));
			output.put("testList",convertHisEmployeetoJson(testList));
			output.put("impList", convertHisEmployeetoJson(impList));
			output.put("Issue_type_list", issueTypeArray);
			output.put("Priority", priorityArray);
			output.put("Issue_Status", issuestatusArray);
			output.put("Assigned_Values", assignedDetArray);
			output.put("dev_details", devassignedArray);
			output.put("test_details", testassignedArray);
			output.put("imp_details", impassignedArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}
	
	private JSONArray convertHisEmployeetoJson(List <HISEmployee> hISEmployee) throws JSONException
	{
		JSONArray hisEmployeeArray = new JSONArray();
		JSONObject hisEmployeeOb = new JSONObject();
		for(HISEmployee hISEmployeeOb : hISEmployee)
		{
			hisEmployeeOb = new JSONObject();
			hisEmployeeOb.put("id", hISEmployeeOb.getId());
			hisEmployeeOb.put("employee_name", hISEmployeeOb.getFirstname() + " " + hISEmployeeOb.getLastname());
			hisEmployeeArray.put(hisEmployeeOb);
		}
		
		return hisEmployeeArray;
	}
	
	
	
	
	
	
	@PostMapping(value = "/assignIssue",produces = "application/json")
	public ResponseEntity<String> saveAssignIssues(@RequestBody IssueMaster issueDetails) throws JSONException
	{		JSONObject response = new JSONObject();	
	
	
	try {
		
		
		
		
		if(!BeanUtils.isNullOrEmpty(issueDetails.getDalist()))
		{	
					
		
		 
		    	
		    	Assinedissuesdetail developerAssign = new Assinedissuesdetail();
                        List<Assinedissuesdetail> danewlist= issueDetails.getDalist() ;
                    	for (Assinedissuesdetail  daob :  danewlist )
                        	{
                    		   if(BeanUtils.isNullOrEmpty(daob.getId()) )
                    			 	
                 		      {
                 		    	
                    			   developerAssign = new Assinedissuesdetail();
                            	developerAssign.setIssueMasterId(issueDetails.getId());
                            	developerAssign.setEmployee_id(daob.getEmployee_id());
                            	developerAssign.setEmployee_type((long) 2);
                                developerAssign.setDuedate(issueDetails.getDue_date());
                                developerAssign.setCreated_datetime(new Date());
                                  baseRepository.persist(developerAssign);
                        	}     
                    		   
                    		   else{        
                    			            developerAssign = new Assinedissuesdetail();
                    			            developerAssign.setId(daob.getId());
                    			            developerAssign.setIssueMasterId(issueDetails.getId());
                    	                	developerAssign.setEmployee_id(daob.getEmployee_id());
                    	                    developerAssign.setDuedate(issueDetails.getDue_date());
                    	                    developerAssign.setCreated_datetime(new Date());
                    	                	developerAssign.setEmployee_type((long) 2);
                    	                      baseRepository.update(developerAssign);
                    			
                    			       
                    			 }
                    			
                        	
                        	}  
                    	
		      }
	    
		
		
		

		if(!BeanUtils.isNullOrEmpty(issueDetails.getTestlist()))
		{	
					
		
		 
		    	
		    	Assinedissuesdetail testerAssign = new Assinedissuesdetail();
                        List<Assinedissuesdetail> danewlist= issueDetails.getTestlist() ;
                    	for (Assinedissuesdetail  daob :  danewlist )
                        	{
                    		   if(BeanUtils.isNullOrEmpty(daob.getId()) )
                    			 	
                 		      {
                 		    	
                    			   testerAssign = new Assinedissuesdetail();
                    			   testerAssign.setIssueMasterId(issueDetails.getId());
                    			   testerAssign.setEmployee_id(daob.getEmployee_id());
                    			   testerAssign.setEmployee_type((long) 3);
                    			   testerAssign.setDuedate(issueDetails.getDue_date());
                    			   testerAssign.setCreated_datetime(new Date());
                                  baseRepository.persist(testerAssign);
                        	}     
                    		   
                    		   else{        
                    			   testerAssign = new Assinedissuesdetail();
                    			   testerAssign.setId(daob.getId());
                    			   testerAssign.setIssueMasterId(issueDetails.getId());
                    			   testerAssign.setEmployee_id(daob.getEmployee_id());
                    			   testerAssign.setDuedate(issueDetails.getDue_date());
                    			   testerAssign.setCreated_datetime(new Date());
                    			   testerAssign.setEmployee_type((long) 3);
                    	                      baseRepository.update(testerAssign);
                    			
                    			       
                    			 }
                    			
                        	
                        	}  
                    	
		      }
		
		if(!BeanUtils.isNullOrEmpty(issueDetails.getImplist()))
		{	
					
		
		 
		    	
		    	Assinedissuesdetail implementerAssign = new Assinedissuesdetail();
                        List<Assinedissuesdetail> danewlist= issueDetails.getImplist() ;
                    	for (Assinedissuesdetail  daob :  danewlist )
                        	{
                    		   if(BeanUtils.isNullOrEmpty(daob.getId()) )
                    			 	
                 		      {
                 		    	
                    			   implementerAssign = new Assinedissuesdetail();
                    			   implementerAssign.setIssueMasterId(issueDetails.getId());
                    			   implementerAssign.setEmployee_id(daob.getEmployee_id());
                    			   implementerAssign.setEmployee_type((long) 4);
                    			   implementerAssign.setDuedate(issueDetails.getDue_date());
                    			   implementerAssign.setCreated_datetime(new Date());
                                  baseRepository.persist(implementerAssign);
                        	}     
                    		   
                    		   else{        
                    			   implementerAssign = new Assinedissuesdetail();
                    			   implementerAssign.setId(daob.getId());
                    			   implementerAssign.setIssueMasterId(issueDetails.getId());
                    			   implementerAssign.setEmployee_id(daob.getEmployee_id());
                    			   implementerAssign.setDuedate(issueDetails.getDue_date());
                    			   implementerAssign.setCreated_datetime(new Date());
                    			   implementerAssign.setEmployee_type((long) 4);
                    	                      baseRepository.update(implementerAssign);
                    			
                    			       
                    			 }
                    			
                        	
                        	}  
                    	
		      }	
		
		
		
		
	IssueMaster issueMaster  = (IssueMaster) baseRepository.find(IssueMaster.class, issueDetails.getId());
		issueMaster.setIssuestatus(issueDetails.getIssuestatus());
		issueMaster.setPriority(issueDetails.getPriority());
		issueMaster.setTester_date(issueDetails.getTester_date());
		issueMaster.setDeveloper_date(issueDetails.getDeveloper_date());
		issueMaster.setExpectedReleasedate(issueDetails.getExpectedReleasedate());
		
	issueDetails.setCreatedDatetime(new Date());		
		baseRepository.update(issueMaster);
		
		response.put("response", "Sucess");
		//send firebase notification
		String title ="Life crm : New Work Assigned";
		String body =issueMaster.getModule() + " : "+issueMaster.getIssuecode();
//		if(!BeanUtils.isNullOrZero(issueDetails.getDeveloper()))
//		{
//			FcmTokenMap fcmTokenMap =(FcmTokenMap) fcmtokenMapRepository.findByUserid(issueDetails.getDeveloper()).orElse(new FcmTokenMap());
//			if(!BeanUtils.isNullOrZero(fcmTokenMap.getId()))
//			{
//				mobileFireBaseNotification.pushFCMNotification(fcmTokenMap.getFcmtoken(),title,body);
//			}
//			
//		}
//		if(!BeanUtils.isNullOrZero(issueDetails.getTester())) {
//			FcmTokenMap fcmTokenMap =(FcmTokenMap) fcmtokenMapRepository.findByUserid(issueDetails.getTester()).orElse(new FcmTokenMap());
//			if(!BeanUtils.isNullOrZero(fcmTokenMap.getId()))
//			{
//				mobileFireBaseNotification.pushFCMNotification(fcmTokenMap.getFcmtoken(),title,body);
//			}
//		}
//		if(!BeanUtils.isNullOrZero(issueDetails.getImplementer()))
//		{
//			FcmTokenMap fcmTokenMap =(FcmTokenMap) fcmtokenMapRepository.findByUserid(issueDetails.getImplementer()).orElse(new FcmTokenMap());
//			if(!BeanUtils.isNullOrZero(fcmTokenMap.getId()))
//			{
//				mobileFireBaseNotification.pushFCMNotification(fcmTokenMap.getFcmtoken(),title,body);
//			}
		
	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}
	
	
	
	
	
	
	
	
	
	
	private String loadIssuesQueryByAssignedUser = "SELECT \r\n"
			+ "  i.id,\r\n"
			+ "  s.id AS siteid,\r\n"
			+ "  s.sitename,\r\n"
			+ "  i.description,\r\n"
			+ "  i.issuetype,\r\n"
			+ "  le1.lookupvalue,\r\n"
			+ "  i.priority,\r\n"
			+ "  CONCAT(le2.lookupvalue),\r\n"
			+ "  u.usertype,\r\n"
			+ "  CONCAT(hc.first_name, ' ', hc.last_name) AS clientname,\r\n"
			+ "  CONCAT(he.first_name, ' ', he.last_name) AS employeename,\r\n"
			+ "  i.issuecode,\r\n"
			+ "  CONCAT(le3.lookupvalue, \"\"),\r\n"
			+ "  i.createddatetime ,i.issuestatus,i.module , i.`developer_date`,i.`tester_date`,iad.duedate \r\n"
			+ "FROM\r\n"
			+ "  issue_master i \r\n"
			+ "  LEFT JOIN assigned_issue_details iad\r\n"
			+ "	ON iad.issue_master_id = i.id\r\n"
			+ "  LEFT JOIN lookupentity le1 \r\n"
			+ "    ON le1.lookupid = i.issuetype \r\n"
			+ "  LEFT JOIN lookupentity le2 \r\n"
			+ "    ON le2.lookupid = i.priority \r\n"
			+ "  LEFT JOIN sites s \r\n"
			+ "    ON s.id = i.siteid \r\n"
			+ "  LEFT JOIN crmuser u \r\n"
			+ "    ON u.user_id = i.createdby \r\n"
			+ "  LEFT JOIN his_clients hc \r\n"
			+ "    ON hc.userid = u.user_id \r\n"
			+ "  LEFT JOIN his_employee he \r\n"
			+ "    ON he.userid = u.user_id \r\n"
			+ "  LEFT JOIN lookupentity le3 \r\n"
			+ "    ON le3.lookupid = i.issuestatus \r\n"
			+ "WHERE u.usertype IS NOT NULL AND (iad.employee_id = ? )\r\n"
			+ "AND i.issuestatus = ? AND i.`isactive`=1";
	private String countworkspaceheadersvalue ="SELECT COUNT((SELECT im.id FROM issue_master im WHERE im.id = i.id AND i.issuestatus = 7)) AS assignedcount,\r\n"
			+ "COUNT((SELECT im.id FROM issue_master im WHERE im.id = i.id AND i.issuestatus = 9)) AS underdevelopment,\r\n"
			+ "COUNT((SELECT im.id FROM issue_master im WHERE im.id = i.id AND i.issuestatus = 10)) AS developmentcompleted,\r\n"
			+ "COUNT((SELECT im.id FROM issue_master im WHERE im.id = i.id AND i.issuestatus = 13)) AS undertesting,\r\n"
			+"COUNT((SELECT im.id FROM issue_master im WHERE im.id = i.id AND i.issuestatus IN (11,22))) AS underfinal,\r\n"
			+ "COUNT((SELECT im.id FROM issue_master im WHERE im.id = i.id AND i.issuestatus IN (14))) AS forrelease FROM issue_master i \r\n"
			+ "LEFT JOIN assigned_issue_details iad ON iad.issue_master_id = i.id WHERE (iad.employee_id = ?)\r\n AND i.`isactive`=1"
			+ " LIMIT 1";
	@PostMapping(value = "/loadWorkspaceDetails",produces = "application/json")
	public ResponseEntity<String> loadIssuesByAssignedUser(@RequestBody IssueSearchCriteria searchCriteria) throws JSONException
	{		
		JSONObject response = new JSONObject();
		JSONArray responseArray = new JSONArray();
		JSONObject output = new JSONObject();

		try {
			String appendQuery = " ";
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getSiteid()))
			{
				appendQuery += " and s.id = " + searchCriteria.getSiteid();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getDescription()))
			{
				appendQuery += " and i.description like \"%" + searchCriteria.getDescription()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuetype()))
			{
				appendQuery += " and i.issuetype = " + searchCriteria.getIssuetype();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getPriority()))
			{
				appendQuery += " and i.priority = " + searchCriteria.getPriority();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getCreatedby()))
			{
				appendQuery += " and u.user_id = " + searchCriteria.getCreatedby();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuecode()))
			{
				appendQuery += " and i.issuecode like  \"%" + searchCriteria.getIssuecode()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getYear()))
			{
				appendQuery += " and YEAR(i.createddatetime) = " + searchCriteria.getYear();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getMonth()))
			{
				appendQuery += " and MONTH(i.createddatetime) = " + searchCriteria.getMonth();
			}
			if(!BeanUtils.isNull(searchCriteria.getCreatedon()))
			{
				appendQuery += " and DATE_FORMAT(i.createddatetime,'%Y-%m-%d')= DATE_FORMAT( " + searchCriteria.getCreatedon() + ",'%Y-%m-%d') ";
			}
//			if(!BeanUtils.isNullOrZero(searchCriteria.getIssuestatus()))
//			{
//				appendQuery += " and i.issuestatus = " + searchCriteria.getIssuestatus();
//			}
			 appendQuery += " order by i.id desc ";
			List <Object []> loadIssues = baseRepository.findQuery(loadIssuesQueryByAssignedUser + appendQuery,
					new Object[]{searchCriteria.getUserid(),searchCriteria.getIssuestatus()});
			for(Object[] issuesListOb : loadIssues)
			{
				response = new JSONObject();
				response.put("id",issuesListOb[0] );
				response.put("siteid",issuesListOb[1]);
				response.put("sitename",issuesListOb[2]);
				response.put("description",issuesListOb[3]);
				response.put("issuetypeId",issuesListOb[4]);
				response.put("issuetype",issuesListOb[5]);
				response.put("priorityid",issuesListOb[6]);
				response.put("priority",issuesListOb[7]);
				response.put("usertype",issuesListOb[8]);
				/// usertype 2 ::: client 
				if(Integer.parseInt(issuesListOb[8].toString()) == 1)
				{				
					response.put("name",issuesListOb[10]);
				}

				else if(Integer.parseInt(issuesListOb[8].toString()) == 2)
				{
					response.put("name",issuesListOb[9]);
				}
				response.put("issuecode",issuesListOb[11]);
				response.put("issuestatus",issuesListOb[12]);
				response.put("module",issuesListOb[15]);
				response.put("devdate",issuesListOb[16]);
				response.put("testdate",issuesListOb[17]);
				response.put("expdate",issuesListOb[18]);		
				responseArray.put(response);
				
		
			
			}
			List <HISSites> sites = baseRepository.find(HISSites.class);
			JSONObject site = new JSONObject();
			JSONArray siteArray = new JSONArray();
			for(HISSites siteOb : sites)
			{
				site = new JSONObject();
				site.put("id", siteOb.getId());
				site.put("name", siteOb.getSitename());
				site.put("code", siteOb.getSitecode());
				siteArray.put(site);				
			}
			
			JSONObject issueTypeOb = new JSONObject();
			JSONArray issueTypeArray = new JSONArray();
			List <Object []> issueType = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_TYPE"});
			for(Object [] issueTypeObj : issueType)
			{
				issueTypeOb = new JSONObject();
				issueTypeOb.put("id", issueTypeObj[0]);
				issueTypeOb.put("code", issueTypeObj[1]);
				issueTypeOb.put("value", issueTypeObj[2]);
				issueTypeArray.put(issueTypeOb);				
			}
			JSONObject priorityOb = new JSONObject();
			JSONArray priorityArray = new JSONArray();
			List <Object []> priority = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"PRIORITY"});
			for(Object [] priorityObj : priority)
			{
				priorityOb = new JSONObject();
				priorityOb.put("id", priorityObj[0]);
				priorityOb.put("code", priorityObj[1]);
				priorityOb.put("value", priorityObj[2]);
				priorityArray.put(priorityOb);				
			}
			JSONObject issuestatusOb = new JSONObject();
			JSONArray issuestatusArray = new JSONArray();
			List <Object []> issuestatus = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_STATUS"});
			for(Object [] priorityObj : issuestatus)
			{
				issuestatusOb = new JSONObject();
				issuestatusOb.put("id", priorityObj[0]);
				issuestatusOb.put("code", priorityObj[1]);
				issuestatusOb.put("value", priorityObj[2]);
				issuestatusArray.put(issuestatusOb);				
			}
			
		
			
			
			output.put("remarks_list", responseArray);
			output.put("Issue_List", responseArray);
			output.put("Issue_type_list", issueTypeArray);
			output.put("Priority", priorityArray);
			output.put("Issue_Status", issuestatusArray);
			output.put("sitelist", siteArray);		
			List<Object[]> totalCountList = baseRepository.findQuery(countworkspaceheadersvalue, new Object[] {searchCriteria.getUserid()});
			output.put("totalAssignedCountList", totalCountList.get(0)[0]);
			output.put("totalUnderDevelopmentCountList", totalCountList.get(0)[1]);
			output.put("totalDevelopmentCompletedCountList", totalCountList.get(0)[2]);
			output.put("totalUnderTestingCountList", totalCountList.get(0)[3]);
			output.put("totalForreleaseCountList", totalCountList.get(0)[4]);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}
	
	@GetMapping(value = "/changeIssueStatus",produces = "application/json")
	public ResponseEntity<String> changeissueStatus(@RequestParam(name = "id") Long issueId,@RequestParam(name = "status") Long status) throws JSONException,IOException
	{
		JSONObject output = new JSONObject();
		IssueMaster issueMaster  = (IssueMaster) baseRepository.find(IssueMaster.class, issueId);
		issueMaster.setIssuestatus(status);
		baseRepository.update(issueMaster);
		output.put("response", "Sucess");
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);
	}
	
	private String loadIssuesForGrid = 
			
			"SELECT i.id,s.id AS siteid,s.sitename,i.description,i.issuetype,le1.lookupvalue,\r\n"
			+ "  i.priority,CONCAT(le2.lookupvalue),u.usertype,CONCAT(hc.first_name, ' ', hc.last_name) AS clientname,\r\n"
			+ "  CONCAT(he.first_name, ' ', he.last_name) AS employeename,i.issuecode,\r\n"
			+ "  CONCAT(le3.lookupvalue, \"\"),i.createddatetime ,"
//			+ " CONCAT(he1.first_name, ' ', he1.last_name) AS dev,\r\n"
//			+ "  CONCAT(he2.first_name, ' ', he2.last_name) AS test,"
//			+ "CONCAT(he3.first_name, ' ', he3.last_name) AS imp,\r\n"
			+ "  i.expected_release_date,i.module\r\n"
			+ "FROM issue_master i"
//			+ " LEFT JOIN `assigned_issue_details` ia ON ia.`issue_master_id` = i.`id`\r\n"
			+ "  LEFT JOIN lookupentity le1 ON le1.lookupid = i.issuetype \r\n"
			+ "  LEFT JOIN lookupentity le2 ON le2.lookupid = i.priority \r\n"
			+ "  LEFT JOIN sites s ON s.id = i.siteid \r\n"
			+ "  LEFT JOIN crmuser u ON u.user_id = i.createdby \r\n"
//			+ "  LEFT JOIN crmuser u1 ON u1.user_id = ia.developer\r\n"
//			+ "  LEFT JOIN crmuser u2 ON u2.user_id = ia.tester\r\n"
//			+ "  LEFT JOIN crmuser u3 ON u3.user_id = ia.implementer\r\n"
			+ "  LEFT JOIN his_clients hc ON hc.userid = u.user_id \r\n"
			+ "  LEFT JOIN his_employee he ON he.userid = u.user_id    \r\n"
//			+ "  LEFT JOIN his_employee he1 ON he1.userid = u1.user_id \r\n"
//			+ "  LEFT JOIN his_employee he2 ON he2.userid = u2.user_id \r\n"
//			+ "  LEFT JOIN his_employee he3 ON he3.userid = u3.user_id \r\n"
			+ "  LEFT JOIN lookupentity le3 ON le3.lookupid = i.issuestatus \r\n"
			+ "WHERE u.usertype IS NOT NULL AND i.`isactive`=1  ";
	@PostMapping(value = "/loadgridissues",produces = "application/json")
	public ResponseEntity<String> loadIssuesForGrid(@RequestBody IssueSearchCriteria searchCriteria) throws JSONException
	{		
		JSONObject response = new JSONObject();
		JSONArray responseArray = new JSONArray();
		JSONObject output = new JSONObject();
		try {
			String appendQuery = " ";
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getSiteid()))
			{
				appendQuery += " and s.id = " + searchCriteria.getSiteid();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getDescription()))
			{
				appendQuery += " and i.description like \"%" + searchCriteria.getDescription()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuetype()))
			{
				appendQuery += " and i.issuetype = " + searchCriteria.getIssuetype();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getPriority()))
			{
				appendQuery += " and i.priority = " + searchCriteria.getPriority();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getCreatedby()))
			{
				appendQuery += " and u.user_id = " + searchCriteria.getCreatedby();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuecode()))
			{
				appendQuery += " and i.issuecode like  \"%" + searchCriteria.getIssuecode()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getYear()))
			{
				appendQuery += " and YEAR(i.createddatetime) = " + searchCriteria.getYear();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getMonth()))
			{
				appendQuery += " and MONTH(i.createddatetime) = " + searchCriteria.getMonth();
			}
			if(!BeanUtils.isNull(searchCriteria.getCreatedon()))
			{
				appendQuery += " and DATE_FORMAT(i.createddatetime,'%Y-%m-%d')= DATE_FORMAT( " + searchCriteria.getCreatedon() + ",'%Y-%m-%d') ";
			}
			if(!BeanUtils.isNullOrZero(searchCriteria.getIssuestatus()))
			{
				appendQuery += " and i.issuestatus = " + searchCriteria.getIssuestatus();
			}
			 appendQuery += " order by i.id desc ";
			List <Object []> loadIssues = baseRepository.findQuery(loadIssuesForGrid + appendQuery,new Object[]{});
			for(Object[] issuesListOb : loadIssues)
			{
				response = new JSONObject();
				response.put("id",issuesListOb[0] );
				response.put("siteid",issuesListOb[1]);
				response.put("sitename",issuesListOb[2]);
				response.put("description",issuesListOb[3]);
				response.put("issuetypeId",issuesListOb[4]);
				response.put("issuetype",issuesListOb[5]);
				response.put("priorityid",issuesListOb[6]);
				response.put("priority",issuesListOb[7]);
				response.put("usertype",issuesListOb[8]);
				/// usertype 2 ::: client 
				if(Integer.parseInt(issuesListOb[8].toString()) == 1)
				{				
					response.put("name",issuesListOb[10]);
				}

				else if(Integer.parseInt(issuesListOb[8].toString()) == 2)
				{
					response.put("name",issuesListOb[9]);
				}
				response.put("issuecode",issuesListOb[11]);
				response.put("issuestatus",issuesListOb[12]);
				response.put("createddate",issuesListOb[13]);
//				response.put("developer",issuesListOb[14]);
//				response.put("tester",issuesListOb[15]);
//				response.put("implementer",issuesListOb[16]);
				response.put("expectedDate",issuesListOb[14]);
//				response.put("duedate",issuesListOb[18]);
				response.put("isToday",false);
//				if(!BeanUtils.isNull(issuesListOb[18]))
//				{
//					Calendar calEnd = new GregorianCalendar();
//					calEnd.setTime(new Date());
//					calEnd.set(Calendar.DAY_OF_YEAR, calEnd.get(Calendar.DAY_OF_YEAR)+1);
//					calEnd.set(Calendar.HOUR_OF_DAY, 0);
//					calEnd.set(Calendar.MINUTE, 0);
//					calEnd.set(Calendar.SECOND, 0);
//					calEnd.set(Calendar.MILLISECOND, 0);
//					Date midnightTonight = calEnd.getTime();
//					{
//						Date duedate =(Date) issuesListOb[18];
//						if(duedate.before(midnightTonight))
//						{
//							response.put("isToday",true);
//						}
//					}
//				}
				response.put("module",issuesListOb[15]);
				responseArray.put(response);
			}
			
			List <HISSites> sites = baseRepository.find(HISSites.class);
			JSONObject site = new JSONObject();
			JSONArray siteArray = new JSONArray();
			for(HISSites siteOb : sites)
			{
				site = new JSONObject();
				site.put("id", siteOb.getId());
				site.put("name", siteOb.getSitename());
				site.put("code", siteOb.getSitecode());
				siteArray.put(site);				
			}
			
			JSONObject issueTypeOb = new JSONObject();
			JSONArray issueTypeArray = new JSONArray();
			List <Object []> issueType = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_TYPE"});
			for(Object [] issueTypeObj : issueType)
			{
				issueTypeOb = new JSONObject();
				issueTypeOb.put("id", issueTypeObj[0]);
				issueTypeOb.put("code", issueTypeObj[1]);
				issueTypeOb.put("value", issueTypeObj[2]);
				issueTypeArray.put(issueTypeOb);				
			}
			JSONObject priorityOb = new JSONObject();
			JSONArray priorityArray = new JSONArray();
			List <Object []> priority = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"PRIORITY"});
			for(Object [] priorityObj : priority)
			{
				priorityOb = new JSONObject();
				priorityOb.put("id", priorityObj[0]);
				priorityOb.put("code", priorityObj[1]);
				priorityOb.put("value", priorityObj[2]);
				priorityArray.put(priorityOb);				
			}
			JSONObject issuestatusOb = new JSONObject();
			JSONArray issuestatusArray = new JSONArray();
			List <Object []> issuestatus = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_STATUS"});
			for(Object [] priorityObj : issuestatus)
			{
				issuestatusOb = new JSONObject();
				issuestatusOb.put("id", priorityObj[0]);
				issuestatusOb.put("code", priorityObj[1]);
				issuestatusOb.put("value", priorityObj[2]);
				issuestatusArray.put(issuestatusOb);				
			}
			output.put("Issue_List", responseArray);
			output.put("Issue_type_list", issueTypeArray);
			output.put("Priority", priorityArray);
			output.put("Issue_Status", issuestatusArray);
			output.put("sitelist", siteArray);
			
			List<Object[]> totalCountList = issueMasterRepository.countIssueMaster();
			List<Object[]> totalCountMonthList = issueMasterRepository.countIssueMasterThisMonth();
			List<Object[]> totalCountRequirementList = issueMasterRepository.countIssueMasterTotalRequirements();
			List<Object[]> totalCountBugsList = issueMasterRepository.countIssueMasterTotalBugs();
			List<Object[]> totalCountResolvedList = issueMasterRepository.countIssueMasterTotalResolved();
			output.put("totalCountList", totalCountList.get(0)[0]);
			output.put("totalCountMonthList", totalCountMonthList.get(0)[0]);
			output.put("totalCountRequirementList", totalCountRequirementList.get(0)[0]);
			output.put("totalCountBugsList", totalCountBugsList.get(0)[0]);
			output.put("totalCountResolvedList", totalCountResolvedList.get(0)[0]);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}
	
	
	
	
	
	
	private String loadIssuesQueryNotAssigned =
			"SELECT i.id as isuue,s.id AS siteid,s.sitename,i.description,i.issuetype,le1.lookupvalue,i.priority,CONCAT(le2.lookupvalue), u.usertype,"
			+ "CONCAT(hc.first_name,' ',hc.last_name) AS clientname,CONCAT(he.first_name,' ',he.last_name) AS employeename, i.issuecode,CONCAT(le3.lookupvalue,\"\"), "
			+ " trunc(i.createddatetime),"
			+ "aid.`employee_id`,aid.`id`"
			+ " FROM issue_master i "
			+ "LEFT JOIN `assigned_issue_details` aid ON i.id = aid.issue_master_id"
			+ " LEFT JOIN lookupentity le1 ON le1.lookupid = i.issuetype LEFT JOIN lookupentity le2 ON le2.lookupid "
			+ "= i.priority LEFT JOIN sites s ON s.id = i.siteid LEFT JOIN crmuser u ON u.user_id = i.createdby LEFT JOIN his_clients hc"
			+ " ON hc.userid = u.user_id LEFT JOIN his_employee he ON he.userid = u.user_id LEFT JOIN lookupentity le3 ON le3.lookupid = i.issuestatus where u.usertype is not null"
			+ " AND (((aid.`employee_type` = 3 OR aid.`employee_type` IS NULL) AND aid.`employee_id` IS NULL)"
			+ " OR ((aid.`employee_type` = 2 OR aid.`employee_type` IS NULL) AND aid.`employee_id` IS NULL) )AND i.`issuestatus`!=15 AND i.`isactive`=1";
	@PostMapping(value = "/loadissuesNotAssigned",produces = "application/json")
	public ResponseEntity<String> loadIssuesNotAssigned(@RequestBody IssueSearchCriteria searchCriteria) throws JSONException
	{
		JSONObject response = new JSONObject();
		JSONArray responseArray = new JSONArray();
		JSONObject output = new JSONObject();
		try {
			String appendQuery = " ";
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getSiteid()))
			{
				appendQuery += " and s.id = " + searchCriteria.getSiteid();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getDescription()))
			{
				appendQuery += " and i.description like \"%" + searchCriteria.getDescription()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuetype()))
			{
				appendQuery += " and i.issuetype = " + searchCriteria.getIssuetype();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getPriority()))
			{
				appendQuery += " and i.priority = " + searchCriteria.getPriority();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getCreatedby()))
			{
				appendQuery += " and u.user_id = " + searchCriteria.getCreatedby();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuecode()))
			{
				appendQuery += " and i.issuecode like  \"%" + searchCriteria.getIssuecode()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getYear()))
			{
				appendQuery += " and YEAR(i.createddatetime) = " + searchCriteria.getYear();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getMonth()))
			{
				appendQuery += " and MONTH(i.createddatetime) = " + searchCriteria.getMonth();
			}
			if(!BeanUtils.isNull(searchCriteria.getCreatedon()))
			{
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			    String createdon = df.format(searchCriteria.getCreatedon());
				appendQuery += " and DATE_FORMAT(i.createddatetime,'%Y-%m-%d')= DATE_FORMAT( '" + createdon +"','%Y-%m-%d') ";
			}
//			else
//			{
//				appendQuery += " and DATE_FORMAT(i.createddatetime,'%Y-%m-%d')= DATE_FORMAT(NOW(),'%Y-%m-%d') ";
//			}
			if(!BeanUtils.isNullOrZero(searchCriteria.getIssuestatus()))
			{
				appendQuery += " and i.issuestatus = " + searchCriteria.getIssuestatus();
			}
			 appendQuery += " order by i.id desc ";
			List <Object []> loadIssues = baseRepository.findQuery(loadIssuesQueryNotAssigned + appendQuery,new Object[]{});
			for(Object[] issuesListOb : loadIssues)
			{
				response = new JSONObject();
				response.put("id",issuesListOb[0] );
				response.put("siteid",issuesListOb[1]);
				response.put("sitename",issuesListOb[2]);
				response.put("description",issuesListOb[3]);
				response.put("issuetypeId",issuesListOb[4]);
				response.put("issuetype",issuesListOb[5]);
				response.put("priorityid",issuesListOb[6]);
				response.put("priority",issuesListOb[7]);
				response.put("usertype",issuesListOb[8]);
				/// usertype 2 ::: client 
				if(Integer.parseInt(issuesListOb[8].toString()) == 1)
				{				
					response.put("name",issuesListOb[10]);
				}

				else if(Integer.parseInt(issuesListOb[8].toString()) == 2)
				{
					response.put("name",issuesListOb[9]);
				}
				response.put("issuecode",issuesListOb[11]);
				response.put("issuestatus",issuesListOb[12]);
				response.put("created_on", issuesListOb[13]);
				response.put("developer",issuesListOb[14]);
				response.put("tester",issuesListOb[15]);
				
				responseArray.put(response);
			}
			
			List <HISSites> sites = baseRepository.find(HISSites.class);
			JSONObject site = new JSONObject();
			JSONArray siteArray = new JSONArray();
			for(HISSites siteOb : sites)
			{
				site = new JSONObject();
				site.put("id", siteOb.getId());
				site.put("name", siteOb.getSitename());
				site.put("code", siteOb.getSitecode());
				siteArray.put(site);				
			}
			
			JSONObject issueTypeOb = new JSONObject();
			JSONArray issueTypeArray = new JSONArray();
			List <Object []> issueType = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_TYPE"});
			for(Object [] issueTypeObj : issueType)
			{
				issueTypeOb = new JSONObject();
				issueTypeOb.put("id", issueTypeObj[0]);
				issueTypeOb.put("code", issueTypeObj[1]);
				issueTypeOb.put("value", issueTypeObj[2]);
				issueTypeArray.put(issueTypeOb);				
			}
			JSONObject priorityOb = new JSONObject();
			JSONArray priorityArray = new JSONArray();
			List <Object []> priority = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"PRIORITY"});
			for(Object [] priorityObj : priority)
			{
				priorityOb = new JSONObject();
				priorityOb.put("id", priorityObj[0]);
				priorityOb.put("code", priorityObj[1]);
				priorityOb.put("value", priorityObj[2]);
				priorityArray.put(priorityOb);				
			}
			JSONObject issuestatusOb = new JSONObject();
			JSONArray issuestatusArray = new JSONArray();
			List <Object []> issuestatus = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_STATUS"});
			for(Object [] priorityObj : issuestatus)
			{
				issuestatusOb = new JSONObject();
				issuestatusOb.put("id", priorityObj[0]);
				issuestatusOb.put("code", priorityObj[1]);
				issuestatusOb.put("value", priorityObj[2]);
				issuestatusArray.put(issuestatusOb);				
			}
			output.put("Issue_List", responseArray);
			output.put("Issue_type_list", issueTypeArray);
			output.put("Priority", priorityArray);
			output.put("Issue_Status", issuestatusArray);
			output.put("sitelist", siteArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}
	
	
	
	
	
	
	String loadNotAssignedIssuesCount= " SELECT COUNT(i.id) FROM issue_master i "
			+ "LEFT JOIN `assigned_issue_details` ia ON ia.issue_master_id = i.id "
			+ " WHERE   (((ia.`employee_type` = 3 OR ia.`employee_type` IS NULL) AND ia.`employee_id` IS NULL) "
			+ "OR ((ia.`employee_type` = 2 OR ia.`employee_type` IS NULL) AND ia.`employee_id` IS NULL) ) AND i.`isactive`=1";
			
//			
//			"SELECT COUNT(i.id) FROM issue_master i\r\n"
//			+ "LEFT JOIN issue_assign_details ia ON ia.issue_master_id = i.id\r\n"
//			+ " WHERE (ia.developer IS NULL OR ia.tester IS NULL)";
	@GetMapping(value = "/loadDashBoardValues",produces = "application/json")
	public ResponseEntity<String> loadDashboardValues() throws JSONException
	{		
		JSONObject output = new JSONObject();
		try {
			List<Object[]> totalUnassignedCountList = issueMasterRepository.countIssueMasterTotalIssuesToday();
			List <Object> loadNotAssignedIssuesCounts = baseRepository.findQuery(loadNotAssignedIssuesCount);
			output.put("unassigned_count", loadNotAssignedIssuesCounts.get(0));
			output.put("issuestoday", totalUnassignedCountList.get(0)[0]);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}
	
	
	String loadUrgentticketDashboard="SELECT s.sitename,i.issuecode,l.lookupvalue,i.module,i.description, "
			+ "i.createddatetime,i.issuetype,CONCAT(l1.lookupvalue) AS issuetypevalue,i.id FROM issue_master i \r\n"
			+ "LEFT JOIN sites s ON s.id = i.siteid\r\n"
			+ "LEFT JOIN lookupentity l ON l.lookupid = i.issuestatus \r\n"
			+ "LEFT JOIN lookupentity l1 ON l1.lookupid = i.issuetype "
			+ "WHERE i.priority = 6 AND i.issuestatus NOT IN (15) AND i.`isactive`=1 ORDER BY i.createddatetime DESC LIMIT 30";
	@GetMapping(value = "/loadUrgentticketDashboard",produces = "application/json")
	public ResponseEntity<String> loadUrgentticketDashboard() throws JSONException
	{		
		JSONObject output = new JSONObject();
		JSONObject outputObj = new JSONObject();
		JSONArray outputArray = new JSONArray();
		try {
			List <Object[]> loadUrgentticketDashboards = baseRepository.findQuery(loadUrgentticketDashboard);
			for(Object[] ticket : loadUrgentticketDashboards)
			{
				output = new JSONObject();
				output.put("sitename", ticket[0]);
				output.put("issuecode", ticket[1]);
				output.put("issuestatus", ticket[2]);
				output.put("module", ticket[3]);
				output.put("description", ticket[4]);
				output.put("createddatetime", ticket[5]);
				output.put("issuetype", ticket[6]);
				output.put("issuetypevalue", ticket[7]);
				output.put("id", ticket[8]);
				outputArray.put(output);
			}
			outputObj.put("urgent_tickets",outputArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(outputObj.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(outputObj.toString(), HttpStatus.OK);

	}
	
	private String loadIssuesAllIssuesAndReleased = "SELECT i.id,s.id AS siteid,s.sitename,i.description,i.issuetype,le1.lookupvalue,\r\n"
			+ "  i.priority,CONCAT(le2.lookupvalue),u.usertype,CONCAT(hc.first_name, ' ', hc.last_name) AS clientname,\r\n"
			+ "  CONCAT(he.first_name, ' ', he.last_name) AS employeename,i.issuecode,\r\n"
			+ "  CONCAT(le3.lookupvalue, \"\"),i.createddatetime , "
//			+ "CONCAT(he1.first_name, ' ', he1.last_name) AS dev,\r\n"
//			+ "  CONCAT(he2.first_name, ' ', he2.last_name) AS test,"
//			+ "CONCAT(he3.first_name, ' ', he3.last_name) AS imp,\r\n"
			+ "  i.expected_release_date,"
//			+ "ia.duedate,"
			+ "i.module\r\n"
			+ "FROM issue_master i "
//			+ "LEFT JOIN `assigned_issue_details` ia ON ia.`issue_master_id` = i.`id`\r\n"
			+ "  LEFT JOIN lookupentity le1 ON le1.lookupid = i.issuetype \r\n"
			+ "  LEFT JOIN lookupentity le2 ON le2.lookupid = i.priority \r\n"
			+ "  LEFT JOIN sites s ON s.id = i.siteid \r\n"
			+ "  LEFT JOIN crmuser u ON u.user_id = i.createdby \r\n"
//			+ "  LEFT JOIN crmuser u1 ON u1.user_id = ia.developer\r\n"
//			+ "  LEFT JOIN crmuser u2 ON u2.user_id = ia.tester\r\n"
//			+ "  LEFT JOIN crmuser u3 ON u3.user_id = ia.implementer\r\n"
			+ "  LEFT JOIN his_clients hc ON hc.userid = u.user_id \r\n"
			+ "  LEFT JOIN his_employee he ON he.userid = u.user_id    \r\n"
//			+ "  LEFT JOIN his_employee he1 ON he1.userid = u1.user_id \r\n"
//			+ "  LEFT JOIN his_employee he2 ON he2.userid = u2.user_id \r\n"
//			+ "  LEFT JOIN his_employee he3 ON he3.userid = u3.user_id \r\n"
			+ "  LEFT JOIN lookupentity le3 ON le3.lookupid = i.issuestatus \r\n"
			+ "WHERE u.usertype IS NOT NULL AND i.`issuestatus`= 15 AND i.`isactive`=1 ";
	@PostMapping(value = "/loadIssuesAllIssuesAndReleased",produces = "application/json")
	public ResponseEntity<String> loadIssuesAllIssuesAndReleased(@RequestBody IssueSearchCriteria searchCriteria) throws JSONException
	{		
		JSONObject response = new JSONObject();
		JSONArray responseArray = new JSONArray();
		JSONObject output = new JSONObject();
		try {
			String appendQuery = " ";
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getSiteid()))
			{
				appendQuery += " and s.id = " + searchCriteria.getSiteid();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getDescription()))
			{
				appendQuery += " and i.description like \"%" + searchCriteria.getDescription()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuetype()))
			{
				appendQuery += " and i.issuetype = " + searchCriteria.getIssuetype();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getPriority()))
			{
				appendQuery += " and i.priority = " + searchCriteria.getPriority();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getCreatedby()))
			{
				appendQuery += " and u.user_id = " + searchCriteria.getCreatedby();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuecode()))
			{
				appendQuery += " and i.issuecode like  \"%" + searchCriteria.getIssuecode()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getYear()))
			{
				appendQuery += " and YEAR(i.createddatetime) = " + searchCriteria.getYear();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getMonth()))
			{
				appendQuery += " and MONTH(i.createddatetime) = " + searchCriteria.getMonth();
			}
			if(!BeanUtils.isNull(searchCriteria.getCreatedon()))
			{
				appendQuery += " and DATE_FORMAT(i.createddatetime,'%Y-%m-%d')= DATE_FORMAT( " + searchCriteria.getCreatedon() + ",'%Y-%m-%d') ";
			}
			if(!BeanUtils.isNullOrZero(searchCriteria.getIssuestatus()))
			{
				appendQuery += " and i.issuestatus = " + searchCriteria.getIssuestatus();
			}
			 appendQuery += " order by i.id desc ";
			List <Object []> loadIssues = baseRepository.findQuery(loadIssuesAllIssuesAndReleased + appendQuery,new Object[]{});
			for(Object[] issuesListOb : loadIssues)
			{
				response = new JSONObject();
				response.put("id",issuesListOb[0] );
				response.put("siteid",issuesListOb[1]);
				response.put("sitename",issuesListOb[2]);
				response.put("description",issuesListOb[3]);
				response.put("issuetypeId",issuesListOb[4]);
				response.put("issuetype",issuesListOb[5]);
				response.put("priorityid",issuesListOb[6]);
				response.put("priority",issuesListOb[7]);
				response.put("usertype",issuesListOb[8]);
				/// usertype 2 ::: client 
				if(Integer.parseInt(issuesListOb[8].toString()) == 1)
				{				
					response.put("name",issuesListOb[10]);
				}

				else if(Integer.parseInt(issuesListOb[8].toString()) == 2)
				{
					response.put("name",issuesListOb[9]);
				}
				response.put("issuecode",issuesListOb[11]);
				response.put("issuestatus",issuesListOb[12]);
				response.put("createddate",issuesListOb[13]);
//				response.put("developer",issuesListOb[14]);
//				response.put("tester",issuesListOb[15]);
//				response.put("implementer",issuesListOb[16]);
				response.put("expectedDate",issuesListOb[14]);
//				response.put("duedate",issuesListOb[18]);
//				response.put("isToday",false);
//				if(!BeanUtils.isNull(issuesListOb[18]))
//				{
//					Calendar calEnd = new GregorianCalendar();
//					calEnd.setTime(new Date());
//					calEnd.set(Calendar.DAY_OF_YEAR, calEnd.get(Calendar.DAY_OF_YEAR)+1);
//					calEnd.set(Calendar.HOUR_OF_DAY, 0);
//					calEnd.set(Calendar.MINUTE, 0);
//					calEnd.set(Calendar.SECOND, 0);
//					calEnd.set(Calendar.MILLISECOND, 0);
//					Date midnightTonight = calEnd.getTime();
//					{
//						Date duedate =(Date) issuesListOb[18];
//						if(duedate.before(midnightTonight))
//						{
//							response.put("isToday",true);
//						}
//					}
//				}
				response.put("module",issuesListOb[15]);
				responseArray.put(response);
			}
			
			List <HISSites> sites = baseRepository.find(HISSites.class);
			JSONObject site = new JSONObject();
			JSONArray siteArray = new JSONArray();
			for(HISSites siteOb : sites)
			{
				site = new JSONObject();
				site.put("id", siteOb.getId());
				site.put("name", siteOb.getSitename());
				site.put("code", siteOb.getSitecode());
				siteArray.put(site);				
			}
			
			JSONObject issueTypeOb = new JSONObject();
			JSONArray issueTypeArray = new JSONArray();
			List <Object []> issueType = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_TYPE"});
			for(Object [] issueTypeObj : issueType)
			{
				issueTypeOb = new JSONObject();
				issueTypeOb.put("id", issueTypeObj[0]);
				issueTypeOb.put("code", issueTypeObj[1]);
				issueTypeOb.put("value", issueTypeObj[2]);
				issueTypeArray.put(issueTypeOb);				
			}
			JSONObject priorityOb = new JSONObject();
			JSONArray priorityArray = new JSONArray();
			List <Object []> priority = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"PRIORITY"});
			for(Object [] priorityObj : priority)
			{
				priorityOb = new JSONObject();
				priorityOb.put("id", priorityObj[0]);
				priorityOb.put("code", priorityObj[1]);
				priorityOb.put("value", priorityObj[2]);
				priorityArray.put(priorityOb);				
			}
			JSONObject issuestatusOb = new JSONObject();
			JSONArray issuestatusArray = new JSONArray();
			List <Object []> issuestatus = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_STATUS"});
			for(Object [] priorityObj : issuestatus)
			{
				issuestatusOb = new JSONObject();
				issuestatusOb.put("id", priorityObj[0]);
				issuestatusOb.put("code", priorityObj[1]);
				issuestatusOb.put("value", priorityObj[2]);
				issuestatusArray.put(issuestatusOb);				
			}
			output.put("Issue_List", responseArray);
			output.put("Issue_type_list", issueTypeArray);
			output.put("Priority", priorityArray);
			output.put("Issue_Status", issuestatusArray);
			output.put("sitelist", siteArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}
	
	
	
	private String loadeveryIssuesQuery ="SELECT i.`id`,i.`issuecode`,le1.`lookupvalue`,CONCAT(le2.lookupvalue),i.`description`"
			+ ",CONCAT(le3.lookupvalue),s.`sitename`,"
			+ "i.`priority`,i.`issuestatus`,i.`issuetype` "
			+ "FROM `issue_master` i"
			+ " LEFT JOIN `lookupentity` le1 ON i.`issuestatus`=le1.`lookupid` "
			+ "LEFT JOIN `lookupentity` le2 ON i.`issuetype`=le2.`lookupid`  "
			+ "LEFT JOIN `lookupentity` le3 ON i.`priority`=le3.`lookupid` "
			+ "LEFT JOIN `sites` s ON i.`siteid`=s.`id`";
	@PostMapping(value = "/loadeveryissues",produces = "application/json")
	public ResponseEntity<String> loadeveryIssues(@RequestBody IssueSearchCriteria searchCriteria) throws JSONException
	{
		
		JSONObject response = new JSONObject();
		JSONArray responseArray = new JSONArray();
		JSONObject output = new JSONObject();
		try {
			String appendQuery = " ";
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getSiteid()))
			{
				appendQuery += " and s.id = " + searchCriteria.getSiteid();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getDescription()))
			{
				appendQuery += " and i.description like \"%" + searchCriteria.getDescription()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuetype()))
			{
				appendQuery += " and i.issuetype = " + searchCriteria.getIssuetype();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getPriority()))
			{
				appendQuery += " and i.priority = " + searchCriteria.getPriority();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getCreatedby()))
			{
				appendQuery += " and u.user_id = " + searchCriteria.getCreatedby();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuecode()))
			{
				appendQuery += " and i.issuecode like  \"%" + searchCriteria.getIssuecode()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getYear()))
			{
				appendQuery += " and YEAR(i.createddatetime) = " + searchCriteria.getYear();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getMonth()))
			{
				appendQuery += " and MONTH(i.createddatetime) = " + searchCriteria.getMonth();
			}
//			else{
//				appendQuery += " and MONTH(i.createddatetime) = " + today.getMonthValue();
//			}
			if(!BeanUtils.isNull(searchCriteria.getCreatedon()))
			{
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			    String createdon = df.format(searchCriteria.getCreatedon());
				appendQuery += " and DATE_FORMAT(i.createddatetime,'%Y-%m-%d')= DATE_FORMAT( '" + createdon +"','%Y-%m-%d') ";
			}
//			else
//			{
//				appendQuery += " and DATE_FORMAT(i.createddatetime,'%Y-%m-%d')= DATE_FORMAT(NOW(),'%Y-%m-%d') ";
//			}
			if(!BeanUtils.isNullOrZero(searchCriteria.getIssuestatus()))
			{
				appendQuery += " and i.issuestatus = " + searchCriteria.getIssuestatus();
			}
			 appendQuery += " order by i.id desc ";
			List <Object []> loadIssues = baseRepository.findQuery(loadeveryIssuesQuery + appendQuery,new Object[]{});
			for(Object[] issuesListOb : loadIssues)
			{
				response = new JSONObject();
				response.put("id",issuesListOb[0] );
				response.put("issuecode",issuesListOb[1]);
				response.put("issuestatus",issuesListOb[2]);
				response.put("issuetype",issuesListOb[3]);
				response.put("description",issuesListOb[4]);
				response.put("priority",issuesListOb[5]);
				response.put("sitename",issuesListOb[6]);
				response.put("priorityid",issuesListOb[7]);
				
				
				response.put("issuestatusid",issuesListOb[8]);
				response.put("issuetypeid",issuesListOb[9]);
				responseArray.put(response);
			}
			
			List <HISSites> sites = baseRepository.find(HISSites.class);
			JSONObject site = new JSONObject();
			JSONArray siteArray = new JSONArray();
			for(HISSites siteOb : sites)
			{
				site = new JSONObject();
				site.put("id", siteOb.getId());
				site.put("name", siteOb.getSitename());
				site.put("code", siteOb.getSitecode());
				siteArray.put(site);				
			}
			
			JSONObject issueTypeOb = new JSONObject();
			JSONArray issueTypeArray = new JSONArray();
			List <Object []> issueType = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_TYPE"});
			for(Object [] issueTypeObj : issueType)
			{
				issueTypeOb = new JSONObject();
				issueTypeOb.put("id", issueTypeObj[0]);
				issueTypeOb.put("code", issueTypeObj[1]);
				issueTypeOb.put("value", issueTypeObj[2]);
				issueTypeArray.put(issueTypeOb);				
			}
			JSONObject priorityOb = new JSONObject();
			JSONArray priorityArray = new JSONArray();
			List <Object []> priority = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"PRIORITY"});
			for(Object [] priorityObj : priority)
			{
				priorityOb = new JSONObject();
				priorityOb.put("id", priorityObj[0]);
				priorityOb.put("code", priorityObj[1]);
				priorityOb.put("value", priorityObj[2]);
				priorityArray.put(priorityOb);				
			}
			JSONObject issuestatusOb = new JSONObject();
			JSONArray issuestatusArray = new JSONArray();
			List <Object []> issuestatus = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_STATUS"});
			for(Object [] priorityObj : issuestatus)
			{
				issuestatusOb = new JSONObject();
				issuestatusOb.put("id", priorityObj[0]);
				issuestatusOb.put("code", priorityObj[1]);
				issuestatusOb.put("value", priorityObj[2]);
				issuestatusArray.put(issuestatusOb);				
			}
			output.put("Issue_List", responseArray);
			output.put("Issue_type_list", issueTypeArray);
			output.put("Priority", priorityArray);
			output.put("Issue_Status", issuestatusArray);
			output.put("sitelist", siteArray);
			
//			List<Object[]> totalCountList = issueMasterRepository.countIssueMaster();
//			List<Object[]> totalCountMonthList = issueMasterRepository.countIssueMasterThisMonth();
//			List<Object[]> totalCountRequirementList = issueMasterRepository.countIssueMasterTotalRequirements();
//			List<Object[]> totalCountBugsList = issueMasterRepository.countIssueMasterTotalBugs();
//			List<Object[]> totalCountResolvedList = issueMasterRepository.countIssueMasterTotalResolved();
//			output.put("totalCountList", totalCountList.get(0)[1]);
//			output.put("totalCountMonthList", totalCountMonthList.get(0)[1]);
//			output.put("totalCountRequirementList", totalCountRequirementList.get(0)[1]);
//			output.put("totalCountBugsList", totalCountBugsList.get(0)[1]);
//			output.put("totalCountResolvedList", totalCountResolvedList.get(0)[1]);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}
	
	
	
	
	
	private String assignedquery="SELECT i.`id`,i.`siteid`,i.`issuecode`,i.`issuestatus`,i.`issuetype`,"
			+ "i.`description`,i.`priority`,i.`expected_release_date`,i.`module`,"
			+ "s.`sitename`,"
			+ " le1.`lookupvalue`,CONCAT(le2.lookupvalue),CONCAT(le3.lookupvalue),ia.`employee_type`,ia.`employee_id`"
			+ "FROM `issue_master` i "
			+ "INNER JOIN `assigned_issue_details` ia ON i.`id`=ia.`issue_master_id`"
			+ " LEFT JOIN `lookupentity` le1 ON i.`issuestatus`=le1.`lookupid` "
	+ "LEFT JOIN `lookupentity` le2 ON i.`issuetype`=le2.`lookupid`  "
	+ "LEFT JOIN `lookupentity` le3 ON i.`priority`=le3.`lookupid` "
	+ "LEFT JOIN `sites` s ON i.`siteid`=s.`id` WHERE i.`issuestatus`!=15";
	@PostMapping(value = "/loadallAssignedissues",produces = "application/json")
	public ResponseEntity<String> loadeallasigned() throws JSONException
	{
//		List<String> a= new ArrayList<String>();
//		a.add("sidhiq");
//		a.add("sinan");
//		a.stream().distinct();
		JSONObject response = new JSONObject();
		
		JSONArray devresponseArray = new JSONArray();
		JSONArray testresponseArray = new JSONArray();
		JSONArray impresponseArray = new JSONArray();
		JSONObject output = new JSONObject();
		List <Object []> assignedissue = baseRepository.findQuery(assignedquery );
		
		try{
			
			
			
			
			
			
			
			
			
			for(Object[] assignedissueob : assignedissue)
			{
					response = new JSONObject();
					response.put("id",assignedissueob[0] );
					response.put("siteid",assignedissueob[1]);
					response.put("issuecode",assignedissueob[2]);
					response.put("issuestatusid",assignedissueob[3]);
					response.put("issuetypeid",assignedissueob[4]);
					response.put("description",assignedissueob[5]);
					response.put("priorityid",assignedissueob[6]);
					response.put("expected_release_date",assignedissueob[7]);
					response.put("module",assignedissueob[8]);
					
					
//					response.put("developer",assignedissueob[9]);
//					response.put("tester",assignedissueob[10]);
//					response.put("implementer",assignedissueob[11]);
					response.put("sitename",assignedissueob[9]);
					response.put("issuestatus",assignedissueob[10]);
					response.put("issuetype",assignedissueob[11]);
					response.put("priority",assignedissueob[12]);
					response.put("employee_id",assignedissueob[14]);
				
					if(assignedissueob[13] != null && assignedissueob[13].toString().equals("2")){
				
					devresponseArray.put(response);
						
					}
					if(assignedissueob[13] != null && assignedissueob[13].toString().equals("3")){
						
						testresponseArray.put(response);
							
						}
					if(assignedissueob[13] != null && assignedissueob[13].toString().equals("4")){
						
						impresponseArray.put(response);
							
						}
				
				
			
			}
			List <HISEmployee> devList = hisEmployeeRepository.findByemployeetype(2l);

			List <HISEmployee> testList = hisEmployeeRepository.findByemployeetype(3l);
			List <HISEmployee> impList = hisEmployeeRepository.findByemployeetype(4l);

			output.put("dev_List",convertHisEmployeetoJson(devList));
			output.put("testList",convertHisEmployeetoJson(testList));
			output.put("impList", convertHisEmployeetoJson(impList));
			output.put("devAssignedIssue", devresponseArray);
			output.put("testAssignedIssue", testresponseArray);
			output.put("impAssignedIssue", impresponseArray);
		}
		 catch (JSONException e) {
				e.printStackTrace();
				return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
			}
		
		
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);
	}
	
	
	private String deleteissueQuery="UPDATE `issue_master` i SET i.`isactive`=0 WHERE i.id=?";
	@PostMapping(value = "/deleteissue",produces = "application/json")
	public ResponseEntity<String> deleteIssue(@RequestParam  (name = "id") Long issueId) throws JSONException {
		
		int rowsaffected;
		JSONObject response = new JSONObject();
		try{
		rowsaffected=baseRepository.updateQuery(deleteissueQuery, new Object[] {issueId});
		
		response.put("rowsaffected",rowsaffected);
		
		}
	
		catch(JSONException e){
			e.printStackTrace();
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
		}
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	}
	
	
	private String updateissueQuery="UPDATE `issue_master` i SET i.`issuestatus`=15 WHERE i.id=?";
	@PostMapping(value = "/updateissue",produces = "application/json")
	public ResponseEntity<String> updateIssue(@RequestBody  List <Object []> updateissue) throws JSONException {
		
		int rowsaffected;
		JSONObject response = new JSONObject();
		try{
			for(Object[] issue:updateissue){
				
		rowsaffected=baseRepository.updateQuery(updateissueQuery, new Object[] {issue});
		
		response.put("rowsaffected",rowsaffected);
			}
		}
	
		catch(JSONException e){
			e.printStackTrace();
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
		}
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	}
	
	
	
	
	
	private String assignedqueryuserwise="SELECT i.`id`,i.`siteid`,i.`issuecode`,i.`issuestatus`,i.`issuetype`,"
			+ "i.`description`,i.`priority`,i.`expected_release_date`,i.`module`,"
			+ "s.`sitename`,"
			+ " le1.`lookupvalue`,CONCAT(le2.lookupvalue),CONCAT(le3.lookupvalue),ia.`employee_type`,ia.`employee_id` ,"
			+ "i.`developer_date`,i.`tester_date`,i.`createddatetime`"
			+ "FROM `issue_master` i "
			+ "INNER JOIN `assigned_issue_details` ia ON i.`id`=ia.`issue_master_id`"
			+ " LEFT JOIN `lookupentity` le1 ON i.`issuestatus`=le1.`lookupid` "
	+ "LEFT JOIN `lookupentity` le2 ON i.`issuetype`=le2.`lookupid`  "
	+ "LEFT JOIN `lookupentity` le3 ON i.`priority`=le3.`lookupid` "
	+ "LEFT JOIN `sites` s ON i.`siteid`=s.`id` WHERE i.`issuestatus`!=15";
	
	@PostMapping(value = "/loadIssuesNotAssigneduserwise",produces = "application/json")
	public ResponseEntity<String> loadIssuesNotAssigneduserwise(@RequestBody IssueSearchCriteria searchCriteria) throws JSONException
	{
		JSONObject response = new JSONObject();
		JSONArray responseArray = new JSONArray();
		JSONObject output = new JSONObject();
		try {
			String appendQuery = " ";
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getSiteid()))
			{
				appendQuery += " and s.id = " + searchCriteria.getSiteid();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getDescription()))
			{
				appendQuery += " and i.description like \"%" + searchCriteria.getDescription()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuetype()))
			{
				appendQuery += " and i.issuetype = " + searchCriteria.getIssuetype();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getPriority()))
			{
				appendQuery += " and i.priority = " + searchCriteria.getPriority();
			}
		
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getIssuecode()))
			{
				appendQuery += " and i.issuecode like  \"%" + searchCriteria.getIssuecode()+"%\"";
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getYear()))
			{
				appendQuery += " and YEAR(i.createddatetime) = " + searchCriteria.getYear();
			}
			if(!BeanUtils.isNullOrEmpty(searchCriteria.getMonth()))
			{
				appendQuery += " and MONTH(i.createddatetime) = " + searchCriteria.getMonth();
			}
			if(!BeanUtils.isNull(searchCriteria.getCreatedon()))
			{
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			    String createdon = df.format(searchCriteria.getCreatedon());
				appendQuery += " and DATE_FORMAT(i.createddatetime,'%Y-%m-%d')= DATE_FORMAT( '" + createdon +"','%Y-%m-%d') ";
			}
			if(!BeanUtils.isNullOrZero(searchCriteria.getIssuestatus()))
			{
				appendQuery += " and i.issuestatus = " + searchCriteria.getIssuestatus();
			}
			if(!BeanUtils.isNullOrZero(searchCriteria.getEmpid()))
			{
			
				appendQuery += " and ia.`employee_id`= " + searchCriteria.getEmpid();
			}
			if(!BeanUtils.isNullOrZero(searchCriteria.getEmptype()))
			{
				appendQuery += " and ia.`employee_type`= " + searchCriteria.getEmptype();
			}						
			 appendQuery += " GROUP BY i.id  ORDER BY i.id DESC ";
			List <Object []> loadIssues = baseRepository.findQuery(assignedqueryuserwise + appendQuery,new Object[]{});
			for(Object[] issuesListOb : loadIssues)
			{
				response = new JSONObject();
				response.put("id",issuesListOb[0] );
				response.put("siteid",issuesListOb[1]);
				response.put("issuecode",issuesListOb[2]);
				response.put("issuestatusid",issuesListOb[3]);
				response.put("issuetypeid",issuesListOb[4]);
				response.put("description",issuesListOb[5]);
				response.put("priorityid",issuesListOb[6]);
				response.put("expected_release_date",issuesListOb[7]);
				response.put("module",issuesListOb[8]);			
				response.put("sitename",issuesListOb[9]);
				response.put("issuestatus",issuesListOb[10]);
				response.put("issuetype", issuesListOb[11]);
				response.put("priority",issuesListOb[12]);
				response.put("emptype",issuesListOb[13]);
				response.put("emptype",issuesListOb[14]);
				response.put("developerdate",issuesListOb[15]);
				response.put("createdon",issuesListOb[17]);
				response.put("testerdate",issuesListOb[16]);
				
				
				
				responseArray.put(response);
			}
			
			List <HISSites> sites = baseRepository.find(HISSites.class);
			JSONObject site = new JSONObject();
			JSONArray siteArray = new JSONArray();
			for(HISSites siteOb : sites)
			{
				site = new JSONObject();
				site.put("id", siteOb.getId());
				site.put("name", siteOb.getSitename());
				site.put("code", siteOb.getSitecode());
				siteArray.put(site);				
			}
			
			JSONObject issueTypeOb = new JSONObject();
			JSONArray issueTypeArray = new JSONArray();
			List <Object []> issueType = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_TYPE"});
			for(Object [] issueTypeObj : issueType)
			{
				issueTypeOb = new JSONObject();
				issueTypeOb.put("id", issueTypeObj[0]);
				issueTypeOb.put("code", issueTypeObj[1]);
				issueTypeOb.put("value", issueTypeObj[2]);
				issueTypeArray.put(issueTypeOb);				
			}
			JSONObject priorityOb = new JSONObject();
			JSONArray priorityArray = new JSONArray();
			List <Object []> priority = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"PRIORITY"});
			for(Object [] priorityObj : priority)
			{
				priorityOb = new JSONObject();
				priorityOb.put("id", priorityObj[0]);
				priorityOb.put("code", priorityObj[1]);
				priorityOb.put("value", priorityObj[2]);
				priorityArray.put(priorityOb);				
			}
			JSONObject issuestatusOb = new JSONObject();
			JSONArray issuestatusArray = new JSONArray();
			List <Object []> issuestatus = baseRepository.findQuery(createIssueDefaultValues,new Object[] {"ISSUE_STATUS"});
			for(Object [] priorityObj : issuestatus)
			{
				issuestatusOb = new JSONObject();
				issuestatusOb.put("id", priorityObj[0]);
				issuestatusOb.put("code", priorityObj[1]);
				issuestatusOb.put("value", priorityObj[2]);
				issuestatusArray.put(issuestatusOb);				
			}
			
			List <HISEmployee> devList = hisEmployeeRepository.findByemployeetype(2l);

			List <HISEmployee> testList = hisEmployeeRepository.findByemployeetype(3l);
			List <HISEmployee> impList = hisEmployeeRepository.findByemployeetype(4l);

			output.put("dev_List",convertHisEmployeetoJson(devList));
			output.put("testList",convertHisEmployeetoJson(testList));
			output.put("impList", convertHisEmployeetoJson(impList));
			output.put("Issue_List", responseArray);
			output.put("Issue_type_list", issueTypeArray);
			output.put("Priority", priorityArray);
			output.put("Issue_Status", issuestatusArray);
			output.put("sitelist", siteArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}
	
	
	@PostMapping(value = "/saveremarks",produces = "application/json")
	public ResponseEntity<String> saveRemarks(@RequestBody Remarks remarksob) throws JSONException
	{		JSONObject response = new JSONObject();	
	
	
	try {
		Remarks remarks=new Remarks();
			
		remarks.setCreatedDatetime(new Date());
		remarks.setIsactive(1l);
		if(!BeanUtils.isNullOrEmpty(remarksob.getRemarks()))
		{
		remarks.setRemarks(remarksob.getRemarks());
		}
		
		if(!BeanUtils.isNullOrEmpty(remarksob.getCreatedBy()))
		{
		remarks.setCreatedBy(remarksob.getCreatedBy());
		}
		
		if(!BeanUtils.isNullOrEmpty(remarksob.getIssueid()))
		{
		remarks.setIssueid(Long.valueOf(remarksob.getIssueid()));
		}

		
		
		baseRepository.persist(remarks);
		
		response.put("response", "Sucess");

		
	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}
	
	
	
	private String getremarksquery=
	"SELECT r.id ,r.`issueid`,r.`remarks`,r.`createdby`,c.fullname,r.`createddatetime` FROM remarks r "+
	"INNER JOIN crmuser c ON c.user_id= r.`createdby` WHERE r.`issueid`=? AND r.`isactive`=1";
	
	@PostMapping(value = "/getremarks",produces = "application/json")
	public ResponseEntity<String> getRemarks(@RequestParam (name = "issueid") Long issueid) throws JSONException
	{		
	
	
	JSONObject remarksOb = new JSONObject();
	JSONArray remarksArray = new JSONArray();
	JSONObject output = new JSONObject();
	try {

		List <Object []> remarkslist = baseRepository.findQuery(getremarksquery,new Object[] {Long.valueOf(issueid)});
		for(Object [] remarksObj : remarkslist)
		{
			remarksOb = new JSONObject();
			remarksOb.put("id", remarksObj[0]);
			remarksOb.put("issueid", remarksObj[1]);
			remarksOb.put("remarks", remarksObj[2]);
			remarksOb.put("userid", remarksObj[3]);
			remarksOb.put("username", remarksObj[4]);
			remarksOb.put("createddate", remarksObj[5]);
			
			remarksArray.put(remarksOb);				
		}
		
				
		output.put("remarkslist", remarksArray);

		
	} catch (Exception e) {
		e.printStackTrace();
		output.put("response", "Failed");
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

	}
	
	
	
}
	
	

