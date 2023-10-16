package com.camerinfolks.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.camerinfolks.model.core.BaseDomain;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Arjun
 */
@Entity
@Getter
@Setter
@Table(name = "ISSUE_MASTER")
public class IssueMaster extends BaseDomain{

	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "SITEID")
	private Long siteid;
	
	@Column(name = "MODULE")
	private String module;
	
	@Column(name = "ISSUETYPE")
	private Long issuetype;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "PRIORITY")
	private Long priority;
	
	@Column(name = "ISSUECODE")
	private String issuecode;
	
	@Column(name = "ISSUESTATUS")
	private Long issuestatus;
	
	@Transient
	private byte[] uploadFileData;
	
	@Column(name = "UPLOADFILENAME")
	private String uploadFileName;
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	@Column(name = "EXPECTED_RELEASE_DATE")
	
	private Date expectedReleasedate;
	@JsonFormat(pattern = "yyyy/MM/dd")
	@Column(name = "DEVELOPER_DATE")
	private Date developer_date;
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	@Column(name = "TESTER_DATE")
	private Date tester_date;
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	@Transient
	private Date due_date;
	
	@Column(name="ISACTIVE")
	private Boolean isactive=Boolean.TRUE;

//	@OneToMany(cascade = CascadeType.ALL)
//	@JoinColumn(name= "ISSUE_MASTER_ID")
//	@OneToMany(mappedBy="ISSUE_MASTER_ID" ,cascade = CascadeType.ALL,fetch=FetchType.EAGER)
//	@ManyToOne
//	@JoinColumn(name="ISSUE_MASTER_ID",nullable=false)
	
//	@OneToMany(mappedBy="issuemaster" ,cascade = CascadeType.ALL,orphanRemoval=true)
//	private  Set<DeveloperAssign> dalist= new HashSet<DeveloperAssign>();

	@Transient
	private  List<Assinedissuesdetail> dalist;
	
	@Transient
	private  List<Assinedissuesdetail> testlist;
	
	@Transient
	private  List<Assinedissuesdetail> implist;
	
	@Transient
	private Long assigned_id;
	
	@Transient
	private Long tester_assigned_id;
	
	@Transient
	private Long implementer_assigned_id;


//	public Set<DeveloperAssign> getDalist() {
//		return dalist;
//	}
//
//	public void setDalist(Set<DeveloperAssign> dalist) {
//		this.dalist = dalist;
//	}

//	public Set<TesterAssign> getTalist() {
//		return talist;
//	}
//
//	public void setTalist(Set<TesterAssign> talist) {
//		this.talist = talist;
//	}
//
//	public Set<ImplementerAssigned> getImpalist() {
//		return impalist;
//	}
//
//	public void setImpalist(Set<ImplementerAssigned> impalist) {
//		this.impalist = impalist;
//	}
	
}
