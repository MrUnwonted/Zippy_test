package com.camerinfolks.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "EMPLOYEE_TYPE")
public class EmployeeDesignation {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
private Long id;
	@Column(name = "EMPLOYEE_TYPE")
	private String employee_type;

}