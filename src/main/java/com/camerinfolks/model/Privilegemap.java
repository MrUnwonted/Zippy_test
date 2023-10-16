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
@Table(name="PRIVILEGE_MAP")
public class Privilegemap {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private Long id;
	@Column(name="EMPLOYEE_CAT_ID")
	private Long empactid;
	@Column(name="PRIVILEGE_ID")
	private Long privilegeid;
	
	@Transient
	private Boolean isAdd = Boolean.FALSE;

}
