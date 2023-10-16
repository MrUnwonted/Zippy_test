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
@Table(name = "USER_TYPE")
public class HisUsertypes {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
private Long id;
	@Column(name = "USERTYPE_NAME")
	private String usertype_name;
}