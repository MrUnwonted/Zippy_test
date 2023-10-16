package com.camerinfolks.service.impl;

import com.camerinfolks.repository.BaseRepository;
import com.camerinfolks.model.DocumentCodeGenerator;
import com.camerinfolks.model.HISSites;
import com.camerinfolks.model.IssueMaster;
import com.camerinfolks.service.IUserControllerServices;
import com.camerinfolks.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Component(value = "userControllerServices")
public class UserControllerServices  implements IUserControllerServices{

	@Autowired
	BaseRepository baserepository;
	
	String loadCodeGeneratorValue = "SELECT d.id,d.currentnum FROM document_code_generator d WHERE d.siteid = ? AND d.doctypenum = ?"; 
	@Override
	public String createIssueCode(IssueMaster issue,Long docTypeId) {
		String issueCode="";
		HISSites sites = (HISSites) baserepository.find(HISSites.class,issue.getSiteid());
		String sitecode =  sites.getSitecode();		
		List <Object[]> docTypeValues = baserepository.findQuery(loadCodeGeneratorValue,new Object[] {issue.getSiteid(),docTypeId});
		if(BeanUtils.isNullOrEmpty(docTypeValues))
		{
			issueCode = sitecode +"-"+"ERR"+ "0001";
			DocumentCodeGenerator docCode = new DocumentCodeGenerator();
			docCode.setSiteid(issue.getSiteid());
			docCode.setCurrentnum(2l);
			docCode.setDocTypenum(docTypeId);
			baserepository.persist(docCode);
		}
		else
		{
			Object[] docObj = docTypeValues.get(0);
			issueCode = sitecode +"-"+"ERR"+"000"+docObj[1];
			DocumentCodeGenerator docCode = (DocumentCodeGenerator) baserepository.find(DocumentCodeGenerator.class,Long.valueOf(docObj[0].toString()));
			Long currentNum = Long.valueOf(docObj[1].toString()) + 1;
			docCode.setCurrentnum(currentNum);
			docCode.setSiteid(issue.getSiteid());
			baserepository.update(docCode);
		}
		return issueCode;
	}
}