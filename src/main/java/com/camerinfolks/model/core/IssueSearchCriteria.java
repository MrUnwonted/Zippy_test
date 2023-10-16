package com.camerinfolks.model.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
/**
 * @author Arjun
 */

@Getter
@Setter
public class IssueSearchCriteria {
	
	private Long siteid;
	private Long createdby;
	private String issuename;
	private String description;
	private String issuecode;
	
	@JsonFormat(pattern = "dd-MM-yyyy")
	private Date createdon;
	private Long issuetype;
	private Long priority;
	private Long issuestatus;
	private Long month;
	private Long year;
	private Long userid;
	private Long empid;
	private Long emptype;

}
