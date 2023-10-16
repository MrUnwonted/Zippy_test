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
@Table(name = "IMPLEMENTER_ASSIGNEDISSUE")
public class ImplementerAssigned {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "IMPLEMENTER")
	private Long implementer;
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	@Column(name = "DUEDATE")
	private Date duedate;
	
	
	@Column(name = "ISSUE_MASTER_ID")
	private Long issueMasterId;
	
	@Transient
	@JsonFormat(pattern = "yyyy/MM/dd")
	private Date tester_date;
	
	@Transient
	@JsonFormat(pattern = "yyyy/MM/dd")
	private Date developer_date;
	
	@Transient
	@JsonFormat(pattern = "yyyy/MM/dd")
	private Date expectedReleasedate;

}