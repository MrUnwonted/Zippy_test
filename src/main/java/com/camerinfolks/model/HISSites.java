package com.camerinfolks.model;

import com.camerinfolks.model.core.BaseDomain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
/**
 * @author Arjun
 */
@Entity
@Getter
@Setter
@Table(name = "SITES")
public class HISSites extends BaseDomain{

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SITECODE")
	private String sitecode;
	
	@Column(name = "SITENAME")
	private String sitename;
	
}

