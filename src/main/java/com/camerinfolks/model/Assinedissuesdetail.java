package com.camerinfolks.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "ASSIGNED_ISSUE_DETAILS")
public class Assinedissuesdetail {

	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "EMPLOYEE_TYPE")
	private Long employee_type;
	
	@Column(name = "EMPLOYEE_ID")
	private Long employee_id;
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	@Column(name = "DUEDATE")
	private Date duedate;
	
	@Column(name = "CREATEDDATETIME")
	private Date created_datetime;
	
	@Column(name = "ISSUE_MASTER_ID")
	private Long issueMasterId;

}
