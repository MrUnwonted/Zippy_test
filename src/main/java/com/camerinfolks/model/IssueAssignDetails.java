package com.camerinfolks.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.camerinfolks.model.core.BaseDomain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
/**
 * @author Arjun
 */
@Entity
@Getter
@Setter
@Table(name = "ISSUE_ASSIGN_DETAILS")
public class IssueAssignDetails extends BaseDomain{

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	
	@Column(name = "ISSUE_MASTER_ID")
	private Long issueMasterId;
	
	@Column(name = "DEVELOPER")
	private Long developer;
	
	@Column(name = "TESTER")
	private Long tester;
	
	@Column(name = "IMPLEMENTER")
	private Long implementer;
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	@Column(name = "DUEDATE")
	private Date duedate;
	
	@Transient
	@JsonFormat(pattern = "yyyy/MM/dd")
	private Date tester_date;
	
	@Transient
	@JsonFormat(pattern = "yyyy/MM/dd")
	private Date developer_date;
	
	@Transient
	@JsonFormat(pattern = "yyyy/MM/dd")
	private Date expectedReleasedate;

	@Transient
	private Long issueStatusId;
	
	@Transient
	private Long issuePriorityId;

}
