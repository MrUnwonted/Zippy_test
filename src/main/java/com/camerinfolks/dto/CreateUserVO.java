package com.camerinfolks.dto;

import com.camerinfolks.model.core.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
/**
 * @author Arjun
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserVO extends BaseDomain{

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;	
	private Long usertype;
	private Long category;
	private String firstname;
	private String lastname;
	private String email;
	private String mobile;
	private Date dob;
	private Long reportingEmployee;
	private Long employee_type;
	private Long siteid;
	private Long user_id;
	private Long id;

}
