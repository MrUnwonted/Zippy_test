package com.camerinfolks.model.core;

/**
 * @author Arjun
 */

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "CREATEDBY", nullable = false)
	private Long createdBy;

	@Column(name = "CREATEDDATETIME", nullable = false)
	@OrderBy("desc")
	private Date createdDatetime;

	@Column(name = "UPDATEDBY")
	private Long updatedBy;

	@Column(name = "UPDATEDDATETIME")
	private Date updatedDatetime;

	@Column(name = "HIBVERSION")
	@Version
	private int versionNo;

	public BaseDomain(Long createdBy, Date createdDatetime) {
		this.createdBy = createdBy;
		this.createdDatetime = createdDatetime == null ? null : new Timestamp(
				createdDatetime.getTime());
	}

	public BaseDomain(Long createdBy, Date createdDatetime,int versionNo) {
		this.createdBy = createdBy;
		this.createdDatetime = createdDatetime == null ? null : new Timestamp(
				createdDatetime.getTime());
		this.versionNo = versionNo;
	}

	public BaseDomain(Long createdBy, Date createdDatetime, Long updatedBy,
					  Date updatedDatetime) {
		this.createdBy = createdBy;
		this.createdDatetime = createdDatetime == null ? null : new Timestamp(
				createdDatetime.getTime());
		this.updatedBy = updatedBy;
		this.updatedDatetime = updatedDatetime == null ? null : new Timestamp(
				updatedDatetime.getTime());
	}



}
