package com.camerinfolks.controller;

import com.camerinfolks.dto.CreateUserVO;
import com.camerinfolks.dto.UploadProfileImageVO;
import com.camerinfolks.model.*;
import com.camerinfolks.model.core.StartupProperties;
import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.repository.FcmtokenMapRepository;
import com.camerinfolks.repository.LifeCrmRepository;
import com.camerinfolks.service.IUserControllerServices;
import com.camerinfolks.utils.BeanUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//@CrossOrigin(maxAge = 3600,origins="*")
@RestController
@RequestMapping(value = "/user")
@EntityScan(basePackages = {"com.lifehis.*"})
@XmlRootElement
public class UserController {


	@Autowired
	BaseRepository baserepository;
	@Autowired
	IUserControllerServices userControllerServices;
	
	@Autowired
	FcmtokenMapRepository fcmtokenMapRepository;
	
	@Autowired
	private StartupProperties startupProperties;
	
	String existUsername = "SELECT c.user_id,c.usertype FROM crmuser c WHERE c.user_name = ?";
	@PostMapping(value = "/createuser",produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> createuser(@RequestBody CreateUserVO user) throws JSONException
	{
		JSONObject obj = new JSONObject();
		List<?> existuser = baserepository.findQuery(existUsername,new Object[] {user.getUsername()});
		if(BeanUtils.isNullOrEmpty(existuser))
		{

			try {
				User newuser = new User();
			newuser.setCategory(user.getCategory());	
				newuser.setCreatedBy(user.getCreatedBy());
				newuser.setCreatedDatetime(new Date());
				newuser.setUsername(user.getUsername());
				String md5Password = user.getPassword();
				byte[] defaultBytes = md5Password.getBytes();
					MessageDigest algorithm = MessageDigest.getInstance("MD5");
					algorithm.reset();
					algorithm.update(defaultBytes);
					byte messageDigest[] = algorithm.digest();
					StringBuffer hexString = new StringBuffer();
					for (int i = 0; i < messageDigest.length; i++) {
						String hex = Integer.toHexString(0xff & messageDigest[i]);
						if (hex.length() == 1)
							hexString.append('0');
						hexString.append(hex);
					}
					md5Password = hexString + "";
				String encryptedPassword=md5Password;
				newuser.setPassword(encryptedPassword);
				newuser.setUsertype(user.getUsertype());
				newuser.setCreatedDatetime(new Date());
				newuser.setFullname(user.getFirstname() + " " + user.getLastname());
				baserepository.persist(newuser);
				if(user.getUsertype() == 1l)
				{
					HISEmployee hisEmployee= new HISEmployee();
					hisEmployee.setFirstname(user.getFirstname());
					hisEmployee.setCreatedBy(user.getCreatedBy());
					hisEmployee.setCreatedDatetime(new Date());
					hisEmployee.setDob(user.getDob());
					hisEmployee.setEmail(user.getEmail());
					hisEmployee.setMobile(user.getMobile());
					hisEmployee.setEmployeetype(user.getEmployee_type());
					hisEmployee.setReportingEmployee(user.getReportingEmployee());
					hisEmployee.setLastname(user.getLastname());
					hisEmployee.setSiteid(user.getSiteid());
					hisEmployee.setUserid(newuser.getUserid());
//					hisEmployee.setUser(newuser);
					baserepository.persist(hisEmployee);
				}
				else
				{
					HISClients hisClients = new HISClients();
					hisClients.setFirstname(user.getFirstname());
					hisClients.setCreatedBy(user.getCreatedBy());
					hisClients.setCreatedDatetime(new Date());
					hisClients.setDob(user.getDob());
					hisClients.setMobile(user.getMobile());
					hisClients.setEmail(user.getEmail());
					hisClients.setEmployee_type(user.getEmployee_type());
					hisClients.setLastname(user.getLastname());
					hisClients.setSiteid(user.getSiteid());
					hisClients.setUserid(newuser.getUserid());
					baserepository.persist(hisClients);
				}
				obj.put("Status", "Sucess");
			}catch (Exception e) {
				obj = new JSONObject();
				obj.put("Status", "Failed");
				obj.put("Message", e.getMessage());
				return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
			}
		}
		else
		{
			obj = new JSONObject();
			obj.put("Status", "Failed");
			obj.put("Message", "user with username already exists");
		}
		return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	}

	String gerUserQuery = "SELECT * FROM `crmuser` u WHERE u.`user_id` = ?";
	@PostMapping(value = "/user",produces = "application/json")
	public ResponseEntity<String> getUser(@RequestParam(name = "id") String id) throws JSONException
	{			
		List <Object []> newdad = baserepository.findQuery(gerUserQuery,new Object[] {id});
		System.out.println(newdad.size());
		JSONObject obj = new JSONObject();
		try {
			obj.put("a",newdad.get(0)[1]);
			System.out.println(newdad.size());
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);

	}

	String loginDetails = "SELECT u.user_id,u.usertype,u.category FROM crmuser u WHERE u.user_name = ? AND u.password = ?";
	String loginEmployeeDetails = "SELECT  c.user_id,h.first_name,h.last_name,h.employee_type,h.reporting_employee,c.usertype,c.user_name FROM crmuser c "
			+ "INNER JOIN his_employee h ON h.userid = c.user_id WHERE c.isActive = 1 AND h.userid = ?";
	String loginClientDetails = "SELECT c.user_id,h.`first_name`,h.`last_name`,h.`employee_type`,c.usertype,c.user_name FROM crmuser c "
			+ "INNER JOIN his_clients h ON h.userid = c.user_id WHERE c.isActive = 1 AND h.userid = ?";
	@PostMapping(value = "/loginauth",produces = "application/json")
	public ResponseEntity<String> loginauth(@RequestParam(name = "username") String username,@RequestParam(name = "password") String password) throws JSONException, NoSuchAlgorithmException
	{	
		JSONObject userDetails = new JSONObject();
		String md5Password = password;
		byte[] defaultBytes = md5Password.getBytes();
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xff & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			md5Password = hexString + "";
		String encryptedPassword=md5Password;
		List <Object []> userDetailslist = baserepository.findQuery(loginDetails,new Object[] {username,encryptedPassword});
		if(!BeanUtils.isNullOrEmpty(userDetailslist))
		{
			Object[] user =  userDetailslist.get(0);
			try {
				if(Integer.parseInt(user[1].toString()) == 1)
				{
					// employee user
					List <Object []> employeeDetailslist = baserepository.findQuery(loginEmployeeDetails,new Object[] {Long.valueOf(user[0].toString())});
					for(Object[] userDetailsobj : employeeDetailslist)
					{
						userDetails = new JSONObject();
						userDetails.put("id", userDetailsobj[0]);
						userDetails.put("firstname", userDetailsobj[1]);
						userDetails.put("lastname", userDetailsobj[2]);
						userDetails.put("employeetype", userDetailsobj[3]);
						userDetails.put("reportingemployee", userDetailsobj[4]);
						userDetails.put("usertype", userDetailsobj[5]);
						userDetails.put("username", userDetailsobj[6]);
						break;
					}
				}
				else
				{
					// client user
					List <Object []> employeeDetailslist = baserepository.findQuery(loginClientDetails,new Object[] {Long.valueOf(user[0].toString())});
					for(Object[] userDetailsobj : employeeDetailslist)
					{
						userDetails = new JSONObject();
						userDetails.put("id", userDetailsobj[0]);
						userDetails.put("firstname", userDetailsobj[1]);
						userDetails.put("lastname", userDetailsobj[2]);
						userDetails.put("employeetype", userDetailsobj[3]);
						//				userDetails.put("reportingemployee", userDetailsobj[4]);
						userDetails.put("usertype", userDetailsobj[4]);
						userDetails.put("username", userDetailsobj[5]);
						break;
					}
				}
				userDetails.put("emp_category", user[2]);
				String jwtToken ="";
				String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
				Key hmacKey = new SecretKeySpec(DatatypeConverter.parseBase64Binary(secret), 
						SignatureAlgorithm.HS256.getJcaName());

				Calendar cal = Calendar.getInstance();
				Date date = cal.getTime();
				cal.add(Calendar.HOUR_OF_DAY, 5);
				Date expiry = cal.getTime();
				jwtToken = Jwts.builder()
						.claim("userDetails", userDetails.toString())
						.claim("date", new Date())
						.setSubject(username)
						.setId(UUID.randomUUID().toString())
						.setIssuedAt(date)
						.setExpiration(expiry)
						.signWith(hmacKey)
						.compact();				
				userDetails.put("token", jwtToken);
				userDetails.put("Status", "Sucess");
				userDetails.put("Flag", "1");
				/// get privileges
				JSONObject privilegeObject = new JSONObject();
				JSONArray privilegearray = new JSONArray();
			
				
				
				
				if(!BeanUtils.isNull((user[2]) ))  {
					List<Object[]> privilegeList = baserepository.findQuery(privilegeQuery, new Object[] {Long.valueOf(user[2].toString())});
					
					
				for(Object[] obj : privilegeList)
				{
					privilegeObject = new JSONObject();	
					privilegeObject.put("id",obj[0]);
					privilegeObject.put("privilege",obj[1]);
					privilegearray.put(privilegeObject);
				}
				
				
				List <HISSites> sites = baserepository.find(HISSites.class);
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
				userDetails.put("PrevilegeList", privilegearray);
				
				userDetails.put("sites",siteArray );
				}
				
//				else{
//					List <HISSites> sites = baserepository.find(HISSites.class);
//					JSONObject site = new JSONObject();
//					JSONArray siteArray = new JSONArray();
//					
//					
//					for(HISSites siteOb : sites)
//					{
//						site = new JSONObject();
//						site.put("id", siteOb.getId());
//						site.put("name", siteOb.getSitename());
//						site.put("code", siteOb.getSitecode());
//						siteArray.put(site);
//					}
//					userDetails.put("sites",siteArray );
//				}
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<String>(e.getMessage().toString(), HttpStatus.OK);			
			}
		}
		else
		{
			userDetails = new JSONObject();
			userDetails.put("id", "User not found");
		}

		return new ResponseEntity<String>(userDetails.toString(), HttpStatus.OK);

	}

//	@PostMapping(value = "/getSitesList",produces = "application/json")
//	public ResponseEntity<String> getSitesList(@RequestBody QueryParameters queryParam) throws JSONException
//	{			
//		List <HISSites> sites = baserepository.listForSuggesstion(HISSites.class, null, queryParam.getOffset(), queryParam.getPageSize());
//		JSONObject site = new JSONObject();
//		JSONArray siteArray = new JSONArray();
//		JSONObject response = new JSONObject();
//		try {
//			for(HISSites siteOb : sites)
//			{
//				site = new JSONObject();
//				site.put("id", siteOb.getId());
//				site.put("sitename", siteOb.getSitename());
//				site.put("sitecode", siteOb.getSitecode());
//				siteArray.put(site);
//			}
//			response.put("Site_List", siteArray);
//			response.put("totalRows", siteArray.length());
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
//		}
//		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
//
//	}
	@Autowired
	LifeCrmRepository lifeCrmRepository;
	
//	@PostMapping(value = "/getSitesList",produces = "application/json")
//	public ResponseEntity<HisSiteResponse> getAllSite(@RequestBody QueryParameters queryParam)
//	{
//		return new ResponseEntity<HisSiteResponse>(getAllPost(queryParam.getOffset(),queryParam.getPageSize(),queryParam.getSortBy(),queryParam.getSortAsc()),HttpStatus.OK);
//	}
//	
//	public HisSiteResponse getAllPost(int pageNo,int pageSize,String sortBy,int sortAsc) {
//		Sort sort = sortAsc == 1 ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//		Pageable pageble = PageRequest.of(pageNo, pageSize,sort);
//		Page<HISSites> posts =  lifeCrmRepository.findAll(pageble);
//		//get Content From page Object
//		List<HISSites> listPost = posts.getContent();
//		HisSiteResponse response = new HisSiteResponse();
//		response.setContent(listPost);
//		response.setPageNo(posts.getNumber());
//		response.setPageSize(posts.getSize());
//		response.setTotalElement(posts.getTotalElements());
//		response.setTotalPages(posts.getTotalPages());
//		response.setIsLast(posts.isLast());
//		return response;
//	}
	
	@PostMapping(value = "/getSitesList",produces = "application/json")
	public ResponseEntity<String> getSitesList() throws JSONException
	{			
		List <HISSites> sites = baserepository.find(HISSites.class);
		JSONObject site = new JSONObject();
		JSONArray siteArray = new JSONArray();
		JSONObject response = new JSONObject();
		try {
			for(HISSites siteOb : sites)
			{
				site = new JSONObject();
				site.put("id", siteOb.getId());
				site.put("sitename", siteOb.getSitename());
				site.put("sitecode", siteOb.getSitecode());
				siteArray.put(site);
			}
			response.put("Site_List", siteArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}

	@PostMapping(value = "/createSite",produces = "application/json")
	public ResponseEntity<String> createSite(@RequestBody HISSites site) throws JSONException
	{		JSONObject response = new JSONObject();	
	try {
		if(!BeanUtils.isNullOrEmpty(site.getId()))
		{
			HISSites loadSite = (HISSites) baserepository.find(HISSites.class, site.getId());
			site.setCreatedBy(loadSite.getCreatedBy());
			site.setCreatedDatetime(loadSite.getCreatedDatetime());
			site.setUpdatedDatetime(new Date());
			site.setVersionNo(loadSite.getVersionNo());
			baserepository.update(site);
			site= new HISSites();

		}
		else
		{
			site.setCreatedDatetime(new Date());
			baserepository.persist(site);
			site= new HISSites();
		}

		response.put("response", "Sucess");

	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}


//	String loadIssuesQuery = "SELECT i.id,s.id AS siteid,s.sitename,i.description,i.issuetype,le1.lookupvalue,i.priority,CONCAT(le2.lookupvalue), u.usertype,"
//			+ "CONCAT(hc.first_name,' ',hc.last_name) AS clientname,CONCAT(he.first_name,' ',he.last_name) AS employeename"
//			+ " FROM issue_master i LEFT JOIN lookupentity le1 ON le1.lookupid = i.issuetype LEFT JOIN lookupentity le2 ON le2.lookupid "
//			+ "= i.priority LEFT JOIN sites s ON s.id = i.siteid LEFT JOIN crmuser u ON u.user_id = i.createdby LEFT JOIN his_clients hc"
//			+ " ON hc.userid = u.user_id LEFT JOIN his_employee he ON he.userid = u.user_id where u.usertype is not null";
//	@PostMapping(value = "/loadissues",produces = "application/json")
//	public ResponseEntity<String> loadIssues() throws JSONException
//	{		
//		JSONObject response = new JSONObject();
//		JSONArray responseArray = new JSONArray();
//		JSONObject output = new JSONObject();
//		try {
//			List <Object []> newdad = baserepository.findQuery(loadIssuesQuery,new Object[] {});
//			for(Object[] issuesListOb : newdad)
//			{
//				response = new JSONObject();
//				response.put("id",issuesListOb[0] );
//				response.put("siteid",issuesListOb[1]);
//				response.put("sitename",issuesListOb[2]);
//				response.put("description",issuesListOb[3]);
//				response.put("issuetypeId",issuesListOb[4]);
//				response.put("issuetype",issuesListOb[5]);
//				response.put("priorityid",issuesListOb[6]);
//				response.put("priority",issuesListOb[7]);
//				response.put("usertype",issuesListOb[8]);
//				/// usertype 2 ::: client 
//				if(Integer.parseInt(issuesListOb[8].toString()) == 1)
//				{				
//					response.put("name",issuesListOb[10]);
//				}
//
//				else if(Integer.parseInt(issuesListOb[8].toString()) == 2)
//				{
//					response.put("name",issuesListOb[9]);
//				}
//				responseArray.put(response);
//			}
//			output.put("Issue_List", responseArray);
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
//		}
//		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);
//
//	}


	@PostMapping(value = "/saveprivilege",produces = "application/json")
	public ResponseEntity<String>  saveprivilege(@RequestBody Privilege privilege) throws JSONException
	{		JSONObject response = new JSONObject();	
	try {
		if(!BeanUtils.isNullOrEmpty(privilege.getId()))
		{
			privilege.setUpdatedDatetime(new Date());
			baserepository.update(privilege);

		}
		else
		{
			privilege.setCreatedDatetime(new Date());
			baserepository.persist(privilege);
		}
		response.put("response", "Sucess");

	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}
	
	String privilegeQuery = "SELECT p.id,p.privilegename FROM privilege p WHERE p.employee_cat_id = ?";
	@GetMapping(value = "/getprivilege",produces = "application/json")
	public ResponseEntity<String>  getprivilege(@RequestParam(name = "userid") String id) throws JSONException
	{		JSONObject response = new JSONObject();	
	JSONObject responseData = new JSONObject();	
	JSONArray responsearray = new JSONArray();
	try {
		List<Object[]> privilegeList = baserepository.findQuery(privilegeQuery, new Object[] {id});
		for(Object[] obj : privilegeList)
		{
			response = new JSONObject();	
			response.put("id",obj[0]);
			response.put("privilege",obj[1]);
			responsearray.put(response);
		}
		responseData.put("response", "Sucess");
		responseData.put("PrivilegeList", responsearray);
	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(responseData.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(responseData.toString(), HttpStatus.OK);

	}
	
	@PostMapping(value="/getemployeedesignation", produces="appliation/json")
	public ResponseEntity<String> getUsertpe() throws JSONException{
		List <EmployeeDesignation> usertype = baserepository.find(EmployeeDesignation.class);
		JSONObject emdtype = new JSONObject();
		JSONArray siteArray = new JSONArray();
		JSONObject response = new JSONObject();
		try {
			for(EmployeeDesignation isempdes : usertype)
			{
				emdtype = new JSONObject();
				
				emdtype.put("id", isempdes.getId());
				emdtype.put("employee_type", isempdes.getEmployee_type());
				
				siteArray.put(emdtype);
			}
			response.put("Utype_List", siteArray);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("response", "Failed");
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);		
		}
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);	
	}
	
	
	
	
	
	@PostMapping(value = "/updateProfileImage",produces = "application/json")
	public ResponseEntity<String> createIssue(@RequestBody UploadProfileImageVO image) throws JSONException
	{		JSONObject response = new JSONObject();	
	try {
		System.out.println("haidhai");
		System.out.println(image.getUserid());
			if(!BeanUtils.isNullOrEmpty(image.getUserid()))
			{
			User crmuser = (User) baserepository.find(User.class, image.getUserid());
			String filename = System.currentTimeMillis() +"_"+image.getFilename() ;
			crmuser.setFilename(filename);
			byte[] bytes = image.getUploadFileData();
			writeBytesToFileApache(startupProperties.getprofileImages() + filename , bytes);
			response.put("path", startupProperties.getprofileImages());
			baserepository.update(crmuser);
			response.put("Status", "Success");
				}
			
		
			else
			{
				response.put("response", "Failed");
			}
		}catch (Exception e) {
			response.put("response", "Failed");
		}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	}
	
    private static void writeBytesToFileApache(String fileOutput, byte[] bytes)
            throws IOException {

            FileUtils.writeByteArrayToFile(new File(fileOutput), bytes);

        }
	
    
    
    
    
    
	String idcheck = "SELECT c.user_id,c.usertype FROM crmuser c WHERE c.user_name = ?";
	@PostMapping(value = "/updateuser",produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> updateUser(@RequestBody CreateUserVO useredit)throws JSONException, NoSuchAlgorithmException{
		JSONObject obj = new JSONObject();
		List<?> idfetch = baserepository.findQuery(idcheck,new Object[] {useredit.getUser_id()});
		
		if(idfetch!=null)
		{
		
			try {
				System.out.println(useredit.getUser_id());
				User newuser =(User) baserepository.find(User.class, useredit.getUser_id())	;
				newuser.setUserid(useredit.getUser_id());
			newuser.setCategory(useredit.getCategory());	
				newuser.setCreatedBy(useredit.getCreatedBy());
				newuser.setUsername(useredit.getUsername());
				
				newuser.setUsertype(useredit.getUsertype());
				newuser.setCreatedDatetime(new Date());
				newuser.setFullname(useredit.getFirstname() + " " + useredit.getLastname());
				baserepository.update(newuser);
				if(useredit.getUsertype() == 1l)
				{
					
					HISEmployee hisEmployee= (HISEmployee) baserepository.find(HISEmployee.class,useredit.getId())	;
					hisEmployee.setId(useredit.getId());
					hisEmployee.setFirstname(useredit.getFirstname());
					hisEmployee.setCreatedBy(useredit.getCreatedBy());
					hisEmployee.setCreatedDatetime(new Date());
					hisEmployee.setDob(useredit.getDob());
					hisEmployee.setEmail(useredit.getEmail());
					hisEmployee.setMobile(useredit.getMobile());
					hisEmployee.setEmployeetype(useredit.getEmployee_type());
					hisEmployee.setReportingEmployee(useredit.getReportingEmployee());
					hisEmployee.setLastname(useredit.getLastname());
					hisEmployee.setSiteid(useredit.getSiteid());
					hisEmployee.setUserid(useredit.getUser_id());
//					hisEmployee.getUser().setUserid(useredit.getUser_id());
					baserepository.update(hisEmployee);
				}
				else
				{
					HISClients hisClients =(HISClients) baserepository.find(HISClients.class,useredit.getId())	;
					hisClients.setId(useredit.getId());
					hisClients.setFirstname(useredit.getFirstname());
					hisClients.setCreatedBy(useredit.getCreatedBy());
					hisClients.setCreatedDatetime(new Date());
					hisClients.setDob(useredit.getDob());
					hisClients.setEmail(useredit.getEmail());
					hisClients.setEmployee_type(useredit.getEmployee_type());
					hisClients.setLastname(useredit.getLastname());
					hisClients.setMobile(useredit.getMobile());
					hisClients.setSiteid(useredit.getSiteid());
					hisClients.setUserid(useredit.getUser_id());
					baserepository.update(hisClients);
				}
				obj.put("Status", "Sucess");
			}catch (Exception e) {
				obj = new JSONObject();
				obj.put("Status", "Failed");
				obj.put("Message", e.getMessage());
				return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
			}
			

		}
		else
		{
			obj = new JSONObject();
			obj.put("Status", "Failed");
			obj.put("Message", "user with username already exists");
		}
		return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/updateFCMtoken",produces = "application/json")
	public ResponseEntity<String> updateUserWithFCMtoken(@RequestParam(name = "userid") Long id,@RequestParam(name = "fcmtoken") String fcmtoken) throws JSONException
	{	
		FcmTokenMap fcmTokenMap =(FcmTokenMap) fcmtokenMapRepository.findByUserid(id).orElse(new FcmTokenMap());
		if(BeanUtils.isNullOrZero(fcmTokenMap.getId()) && BeanUtils.isNullOrZero(fcmTokenMap.getUserid()))
		{
			fcmTokenMap.setUserid(id);
		}
		fcmTokenMap.setFcmtoken(fcmtoken);
		fcmtokenMapRepository.saveAndFlush(fcmTokenMap);
		JSONObject obj = new JSONObject();
		return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);

	}
	
	
	String getWorkspaceTicketCount = "SELECT COUNT(i.id) FROM issue_master i LEFT JOIN \r\n"
			+ "assigned_issue_details ia ON ia.issue_master_id = i.id\r\n"
			+ "WHERE (ia.employee_id= ?) AND i.issuestatus IN (7,9,10,11,13,14,22)";
	@GetMapping(value = "/getWorkspaceTicketCount",produces = "application/json")
	public ResponseEntity<String>  getWorkspaceTicketCount(@RequestParam(name = "userid") Long id) throws JSONException
	{
	JSONObject responseData = new JSONObject();	
	try {
		List<Object[]> privilegeList = baserepository.findQuery(getWorkspaceTicketCount, new Object[] {id});
		if(!BeanUtils.isNullOrEmpty(privilegeList))
		{
			responseData.put("wrokspaceCount", privilegeList.get(0));
		}
		else
		{
			responseData.put("wrokspaceCount", 0);
		}
	} catch (Exception e) {
		e.printStackTrace();
		responseData.put("wrokspaceCount", 0);
		return new ResponseEntity<String>(responseData.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(responseData.toString(), HttpStatus.OK);

	}
	
	

	@PostMapping(value = "/updatepassword",produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> updatepassword(@RequestBody User user)throws JSONException, NoSuchAlgorithmException{
		JSONObject obj = new JSONObject();
		List<?> idfetch = baserepository.findQuery(idcheck,new Object[] {user.getUserid()});
		
		if(idfetch!=null)
		{
			
		try{
				User newuser =(User) baserepository.find(User.class,user.getUserid())	;
				String md5Password = user.getPassword();
				byte[] defaultBytes = md5Password.getBytes();
					MessageDigest algorithm = MessageDigest.getInstance("MD5");
					algorithm.reset();
					algorithm.update(defaultBytes);
					byte messageDigest[] = algorithm.digest();
					StringBuffer hexString = new StringBuffer();
					for (int i = 0; i < messageDigest.length; i++) {
						String hex = Integer.toHexString(0xff & messageDigest[i]);
						if (hex.length() == 1)
							hexString.append('0');
						hexString.append(hex);
					}
					md5Password = hexString + "";
				String encryptedPassword=md5Password;
				newuser.setPassword(encryptedPassword);
				newuser.setUserid(user.getUserid());
				newuser.setCreatedDatetime(new Date());
				newuser.setCreatedBy((long) 1);
				baserepository.update(newuser);
				obj.put("Status", "Sucess");
		}
		catch (Exception e) {
			obj = new JSONObject();
			obj.put("Status", "Failed");
			obj.put("Message", e.getMessage());
			return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
		}
			}
			else{
				obj = new JSONObject();
				obj.put("Status", "Failed");
				obj.put("Message", "user with username already exists");
			}
			return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
	}
	
	String loaduserquery ="SELECT h.`id`,c.`fullname`, c.`user_name`,e.`employee_type`,h.`email`,c.`filename` FROM `crmuser` c LEFT JOIN `his_employee` h ON h.`userid`=c.`user_id` INNER JOIN `employee_type` e ON h.`employee_type`=e.`id` WHERE c.`user_id`=?";
	
	@PostMapping(value = "/loaduserprofile",produces = "application/json")
	public ResponseEntity<String> loaduserprofile(@RequestBody User user) throws JSONException
	{		JSONObject response = new JSONObject();	
	
	

	JSONArray responseArray = new JSONArray();
	JSONObject output = new JSONObject();
	try {
		List <Object []> userlist = baserepository.findQuery(loaduserquery,new Object[] {user.getUserid()});
		for(Object[] userListOb : userlist)
		{
			response = new JSONObject();
			response.put("id",userListOb[0] );
			response.put("fullname",userListOb[1]);
			response.put("username",userListOb[2]);
			response.put("designation",userListOb[3]);
			response.put("email",userListOb[4]);
			response.put("filename",userListOb[5]);
		
			
			responseArray.put(response);
		}
		output.put("User_profile", responseArray);
	} catch (JSONException e) {
		e.printStackTrace();
		return new ResponseEntity<String>(output.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(output.toString(), HttpStatus.OK);

}

}
