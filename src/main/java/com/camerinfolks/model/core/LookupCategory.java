
package com.camerinfolks.model.core;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
/**
 * @author Arjun
 */

@Entity
@Getter
@Setter
@Table(name="LOOKUPCATEGORY")
public class LookupCategory extends BaseDomain {

	private static final long serialVersionUID = 1L;
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	
	@Column(name="CATEGORYID")
	private Long categoryId;
	@Column(name="CATEGORYNAME")
	private String categoryName;
	@Column(name="MODIFIABLE")
	private Boolean modifiable;	
	@Column(name="ACTIVE", nullable=false)
	private Boolean active;

}
