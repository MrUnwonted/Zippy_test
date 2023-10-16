package com.camerinfolks.model;

/**
 * @author Arjun
 */

import com.camerinfolks.model.core.BaseDomain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "PRIVILEGE")
public class Privilege extends BaseDomain{

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "PRIVILEGENAME")
	private String privilegename;
	
	@Column(name = "USERID")
	private Long userid;
	
	@Column(name = "EMPLOYEE_CAT_ID")
	private Long employeecat;
}
