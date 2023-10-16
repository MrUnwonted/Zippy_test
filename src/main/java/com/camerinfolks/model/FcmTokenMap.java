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
@Table(name = "FCMTOKEN_MAP")
public class FcmTokenMap {

	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "USERID")
	private Long userid;
	
	@Column(name = "FCMTOKEN")
	private String fcmtoken;

}
