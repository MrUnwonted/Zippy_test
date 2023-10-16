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
@Table(name = "LEAVE_MASTER")
public class Leavemaster {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "EMPLOYEE_ID")
	private Long userid;
	
	@Column(name = "LEAVE_TYPE")
	private Long leave_type;
	
	@Column(name = "LEAVE_STATUS")
	private Long leave_status;

	@Column(name = "REASON")
	private String reason;
	
	@Column(name = "REPORTING_MANAGER")
	private Long reporting_manager;
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	@Column(name = "START_DATE")
	private Date startdate;
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	@Column(name = "END_DATE")
	private Date enddate;
	

}
