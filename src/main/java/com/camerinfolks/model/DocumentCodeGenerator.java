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
@Table(name = "DOCUMENT_CODE_GENERATOR")
public class DocumentCodeGenerator {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SITEID")
	private Long siteid;
	
	@Column(name = "CURRENTNUM")
	private Long currentnum;
	
	@Column(name = "DOCTYPENUM")
	private Long docTypenum;

}

