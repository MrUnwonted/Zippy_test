package com.camerinfolks.model;

/**
 * @author Arjun
 */

import com.camerinfolks.model.core.BaseDomain;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

//import org.hibernate.envers.Audited;


//@Audited
@Entity
@Getter
@Setter
@Table(name = "CRMUSER")
public class User extends BaseDomain{

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "USER_ID")
	private Long userid;

	@Column(name = "USER_NAME")
	private String username;
	
	@Column(name = "PASSWORD")
	private String password;
	
	@Column(name = "USERTYPE")
	private Long usertype;
	
	@Column(name = "CATEGORY")
	private Long category;
	
	@Column(name = "FULLNAME")
	private String fullname;
	
	@Column(name = "FILENAME")
	private String filename;

}
