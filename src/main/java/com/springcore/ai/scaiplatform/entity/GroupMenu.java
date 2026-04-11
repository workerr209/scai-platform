package com.springcore.ai.scaiplatform.entity;

import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the as_groupmenu database table.
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Audited
@Table(name="as_groupmenu")
@NamedQuery(name="AsGroupmenu.findAll", query="SELECT a FROM GroupMenu a")
public class GroupMenu extends GenericPersistentObject implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AS_GROUPMENU_GENERATOR")
	@SequenceGenerator(name = "AS_GROUPMENU_GENERATOR", sequenceName = "AS_GROUPMENU_ID_GENERATOR", allocationSize = 1, initialValue = 1)
	private Long id;
	private String code;

	private int inactive;
	private BigDecimal masterGroupId;
	private String name;
	private BigDecimal parentId;
}
