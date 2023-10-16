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
@Table(name = "DEVELOPER_ASSIGNEDISSUE")
public class DeveloperAssign {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "DEVELOPER")
	private Long developer;
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	@Column(name = "DUEDATE")
	private Date duedate;
	
//	@OneToMany(mappedBy="DEVELOPER_ASSIGNEDISSUE" ,cascade = CascadeType.ALL,fetch=FetchType.EAGER)

//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="ISSUE_MASTER_ID",nullable=true)
//	private IssueMaster issuemaster = new IssueMaster();
	@Column(name = "ISSUE_MASTER_ID")
	private Long issuemasterid;

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
