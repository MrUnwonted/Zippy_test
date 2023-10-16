package com.camerinfolks.response;

import com.camerinfolks.model.HISSites;

import java.util.ArrayList;
import java.util.List;

public class HisSiteResponse {

	private List<HISSites> content = new ArrayList<HISSites>();
	
	private int pageNo;
	private int pageSize;
	private Long totalElement;
	private int totalPages;
	private Boolean isLast;
	public List<HISSites> getContent() {
		return content;
	}
	public void setContent(List<HISSites> content) {
		this.content = content;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public Long getTotalElement() {
		return totalElement;
	}
	public void setTotalElement(Long totalElement) {
		this.totalElement = totalElement;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public Boolean getIsLast() {
		return isLast;
	}
	public void setIsLast(Boolean isLast) {
		this.isLast = isLast;
	}
	
	
}
