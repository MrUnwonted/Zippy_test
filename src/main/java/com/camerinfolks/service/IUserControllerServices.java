package com.camerinfolks.service;

import com.camerinfolks.model.IssueMaster;

public interface IUserControllerServices{

	String createIssueCode(IssueMaster issue,Long docTypeId);
}
