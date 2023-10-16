package com.camerinfolks.response;

import com.camerinfolks.model.HISSites;

import java.util.ArrayList;
import java.util.List;

public class HISEmployeeResponse {
	
	private List<HISSites> content = new ArrayList<HISSites>();

	public List<HISSites> getContent() {
		return content;
	}

	public void setContent(List<HISSites> content) {
		this.content = content;
	}
	
	

}
