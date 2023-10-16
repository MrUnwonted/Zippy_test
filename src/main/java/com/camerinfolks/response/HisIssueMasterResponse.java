package com.camerinfolks.response;

import com.camerinfolks.model.IssueMaster;
import com.camerinfolks.model.IssueMaster;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class HisIssueMasterResponse {

private List<IssueMaster> content = new ArrayList<IssueMaster>();
	
	private int pageNo;
	private int pageSize;
	private Long totalElement;
	private int totalPages;
	private Boolean isLast;

}
