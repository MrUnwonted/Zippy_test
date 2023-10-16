package com.camerinfolks.controller;

import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.model.core.LookupCategory;
import com.camerinfolks.model.core.LookupEntity;
import com.camerinfolks.repository.IssuetypeManageRepository;
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
@RequestMapping(value = "/issuetypemanage")
@EntityScan(basePackages = {"com.lifehis.*"})
public class IssuetypeManageContoller {
	@Autowired
	IssuetypeManageRepository issuetypeManageRepository;
	
	@Autowired BaseRepository baserepository;
	
	@PostMapping(value = "/getissuetypemanage",produces = "application/json")
	public ResponseEntity<String> getissuetypemanage() throws JSONException
	{			
		List <LookupCategory> issuetypemng = baserepository.find(LookupCategory.class);
		JSONObject site = new JSONObject();
		JSONArray siteArray = new JSONArray();
		JSONObject response = new JSONObject();
		try {
			for(LookupCategory ismob : issuetypemng)
			{
				site = new JSONObject();
				site.put("categoryId", ismob.getCategoryId());
				site.put("categoryName", ismob.getCategoryName());
				
				siteArray.put(site);
			}
			response.put("Site_List", siteArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
		}
		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

	}


@PostMapping(value = "/saveissuetypemanage",produces = "application/json")
public ResponseEntity<String> saveCategory(@RequestBody LookupEntity lookupEntity) throws JSONException
{		JSONObject response = new JSONObject();	
try {
	
//	if(!BeanUtils.isNullOrEmpty(site.getId()))
//	{
//		HISSites loadSite = (HISSites) employeecategoryRepository.find(HISSites.class, site.getId());
//		site.setCreatedBy(loadSite.getCreatedBy());
//		site.setCreatedDatetime(loadSite.getCreatedDatetime());
//		site.setUpdatedDatetime(new Date());
//		site.setVersionNo(loadSite.getVersionNo());
//		employeecategoryRepository.update(site);
//		site= new HISSites();
//
//	}
//	else
//	{
//		site.setCreatedDatetime(new Date());
	issuetypeManageRepository.saveAndFlush(lookupEntity);
//		site= new HISSites();
//	}

	response.put("response", "Sucess");

} catch (Exception e) {
	e.printStackTrace();
	response.put("response", "Failed");
	return new ResponseEntity<String>(response.toString(), HttpStatus.OK);			
}
return new ResponseEntity<String>(response.toString(), HttpStatus.OK);

}
}