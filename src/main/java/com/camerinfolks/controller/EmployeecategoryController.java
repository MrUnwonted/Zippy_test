package com.camerinfolks.controller;
import com.camerinfolks.repository.EmployeecategoryRepository;
import org.json.JSONArray;
import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.model.EmployeeCategory;
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
@RequestMapping(value = "/empcategory")
@EntityScan(basePackages = {"com.lifehis.*"})
public class EmployeecategoryController {
	@Autowired
	EmployeecategoryRepository employeecategoryRepository;
	
	
	@Autowired BaseRepository baserepository;
	
	@PostMapping(value = "/saveCategory",produces = "application/json")
	public ResponseEntity<String> saveCategory(@RequestBody EmployeeCategory empCategory) throws JSONException
	{		JSONObject response = new JSONObject();
	try {
		
//		if(!BeanUtils.isNullOrEmpty(site.getId()))
//		{
//			HISSites loadSite = (HISSites) employeecategoryRepository.find(HISSites.class, site.getId());
//			site.setCreatedBy(loadSite.getCreatedBy());
//			site.setCreatedDatetime(loadSite.getCreatedDatetime());
//			site.setUpdatedDatetime(new Date());
//			site.setVersionNo(loadSite.getVersionNo());
//			employeecategoryRepository.update(site);
//			site= new HISSites();
//
//		}
//		else
//		{
//			site.setCreatedDatetime(new Date());
			employeecategoryRepository.saveAndFlush(empCategory);
//			site= new HISSites();
//		}

		response.put("response", "Sucess");

	} catch (Exception e) {
		e.printStackTrace();
		response.put("response", "Failed");
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
	}
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}



@PostMapping(value = "/getEmpcat",produces = "application/json")
public ResponseEntity<String> getEmpcat() throws JSONException
{			
	List <EmployeeCategory> empcat = baserepository.find(EmployeeCategory.class);
	JSONObject site = new JSONObject();
	JSONArray siteArray = new JSONArray();
	JSONObject response = new JSONObject();
	try {
		for(EmployeeCategory empob : empcat)
		{
			site = new JSONObject();
			site.put("id", empob.getId());
			site.put("empcatname", empob.getEmpcatname());
			site.put("level", empob.getLevel());
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

//@PostMapping(value = "/getEmpcat",produces = "application/json")
//public ResponseEntity<HisempcatMasterResponse> getEmpcat(@RequestBody QueryParameters queryParam)
//{
//	return new ResponseEntity<HisempcatMasterResponse>(getAllIssues(queryParam.getOffset(),queryParam.getPageSize(),queryParam.getSortBy(),queryParam.getSortAsc()),HttpStatus.OK);
//}
//
//public HisempcatMasterResponse getAllIssues(int pageNo,int pageSize,String sortBy,int sortAsc) {
//	Sort sort = sortAsc == 1 ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//	Pageable pageble = PageRequest.of(pageNo, pageSize,sort);
//	Page<EmployeeCategory> posts =  employeecategoryRepository.findAll(pageble);
//	//get Content From page Object
//	List<EmployeeCategory> listPost = posts.getContent();
//	HisempcatMasterResponse response = new HisempcatMasterResponse();
//	response.setContent(listPost);
//	response.setPageNo(posts.getNumber());
//	response.setPageSize(posts.getSize());
//	response.setTotalElement(posts.getTotalElements());
//	response.setTotalPages(posts.getTotalPages());
//	response.setIsLast(posts.isLast());
//	return response;
//}
//
//}
