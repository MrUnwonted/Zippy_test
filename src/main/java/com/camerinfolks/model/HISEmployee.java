package com.camerinfolks.model;

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
@Table(name = "HIS_EMPLOYEE")
public class HISEmployee extends BaseDomain{

	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "FIRST_NAME")
	private String firstname;
	
	@Column(name = "LAST_NAME")
	private String lastname;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "DOB")
	private Date dob;
	
	@Column(name = "REPORTING_EMPLOYEE")
	private Long reportingEmployee;
	
	@Column(name = "EMPLOYEE_TYPE")
	private Long employeetype;
	
	@Column(name = "SITEID")
	private Long siteid;

	@Column(name = "USERID")
	private Long userid;
	
	@Column(name="MOBILE")
	private String mobile;

//	@ManyToOne
//	@JoinColumn(name="USER")
//	private User user = new User();
	
//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}

}
