package com.camerinfolks.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DevDto {

	private Long id;
	private Long developer;
	private Date duedate;
	private Date tester_date;
	private Date developer_date;
	private Date expectedReleasedate;

	
}
