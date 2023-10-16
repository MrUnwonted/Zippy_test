package com.camerinfolks.model.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryParameters {

	private Integer offset;
	private Integer pageSize;
	private Integer sortIndex;
	private Integer sortAsc;
	private String sortBy;

}
