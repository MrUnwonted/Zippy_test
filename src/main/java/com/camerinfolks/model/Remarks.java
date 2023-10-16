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
@Table(name = "Remarks")
public class Remarks extends BaseDomain{

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "ISSUEID")
	private Long issueid;
		
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "ISACTIVE")
	private Long isactive;

}
