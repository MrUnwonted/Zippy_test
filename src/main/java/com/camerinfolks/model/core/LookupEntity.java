/**
 * For storing all Master field entries . Common entries like occupation,nationality , language , religion etc are fetched 
 * through this LookupEntity class 
 *  */

package com.camerinfolks.model.core;

//import com.lifehis.utils.BeanUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "LOOKUPENTITY")
public class LookupEntity extends BaseDomain {

	/**
	 * @author Arjun
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name = "LOOKUPID")
	private Long lookupId;
	@Column(name = "LOOKUPCODE")
	private String lookupCode;
	@Column(name = "LOOKUPVALUE")
	private String lookupValue;
	@ManyToOne
	@JoinColumn(name = "LOOKUPCATEGORY")
	private LookupCategory lookupCategory = new LookupCategory();
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "ACTIVE", nullable = false)
	private Boolean active;
	@Column(name = "SORTORDER")
	private Integer sortOrder;
	private Number BeanUtils;

	public LookupEntity(Long lookupId) {
		this.lookupId = lookupId;
	}

	public LookupEntity(Long lookupId, String lookupValue) {
		this(lookupId);
		this.lookupValue = lookupValue;
	}

	public LookupEntity(Long lookupId, String lookupValue, String lookupCode) {
		this(lookupId);
		this.lookupValue = lookupValue;
		this.lookupCode = lookupCode;
	}



	public LookupEntity(String lookupValue) {
		super();
		this.lookupValue = lookupValue;
	}
//Need to modify BeanUtils after improrting
	public boolean equals(Object object) {
		Long lookupId = 0l;
		if(object instanceof LookupEntity){
			if (!BeanUtils.equals(object)) {
				lookupId = ((LookupEntity) object).getLookupId();
			}
			if (!BeanUtils.equals(getLookupId())) {
				if (getLookupId().equals(lookupId))
					return true;
				return false;
			} else if (!BeanUtils.equals(lookupId)) {
				if (lookupId.equals(getLookupId()))
					return true;
				return false;
			}
		}else if (object instanceof Long){
			lookupId = (Long)object;
			if (!BeanUtils.equals(getLookupId())) {
				if (getLookupId().equals(lookupId))
					return true;
				return false;
			} else if (!BeanUtils.equals(lookupId)) {
				if (lookupId.equals(getLookupId()))
					return true;
				return false;
			}
		}
		return true;
	}
}
