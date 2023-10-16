package com.camerinfolks.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
/**
 * @author Arjun
 */
@Entity
@Getter
@Setter
@Table(name = "EMP_CAT_PRIVILEGE_MAP")
public class EmployeeCategoryPrivilegeMap {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "PRIVILEGEID")
	private Long privilegeId;
	
	@Column(name = "EMP_CATEGORY")
	private Long empCategory;
	
	@Column(name = "ISACTIVE")
	private Boolean isActive;

	
}
