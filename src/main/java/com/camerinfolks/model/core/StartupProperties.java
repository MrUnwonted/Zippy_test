package com.camerinfolks.model.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.Serializable;

@Component
@Getter
@Setter
public class StartupProperties implements Serializable  {
	
	@Autowired
	static ServletContext context;

	private static String uploadPath;
	public static final String DEFAULT_IMAGES = "images/";
	public static final String PROFILE_IMAGES = "images/profileImages/";

	public static String getprofileImages() {
		String path = StartupProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String result = path.substring(0,path.lastIndexOf("/life_crm-1.0/"));
		return result+"/"+PROFILE_IMAGES;

	}
}
