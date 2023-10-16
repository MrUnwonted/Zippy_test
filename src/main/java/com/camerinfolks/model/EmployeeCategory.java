package com.camerinfolks.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.persistence.*;
/**
 * @author Arjun
 */
@Entity
@Getter
@Setter
@Table(name = "EMP_CATEGORY")
public class EmployeeCategory {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "EMP_CAT_NAME")
	private String empcatname;
	@Column(name = "LEVEL")
	private Long level;

}
