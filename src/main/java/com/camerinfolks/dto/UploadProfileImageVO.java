package com.camerinfolks.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadProfileImageVO {

	private Long id;
	private Long userid;
	private String filename;
	private byte[] uploadFileData;


	
}
