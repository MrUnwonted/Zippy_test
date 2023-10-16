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
@Table(name = "MODULES")
public class ModuleMaster  {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "MODULE_NAME")
	private String module_name;

}
