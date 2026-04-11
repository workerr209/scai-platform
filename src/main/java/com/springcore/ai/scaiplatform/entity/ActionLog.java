package com.springcore.ai.scaiplatform.entity;

import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the as_actionlog database table.
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="as_actionlog")
@NamedQuery(name="AsActionlog.findAll", query="SELECT a FROM ActionLog a")
public class ActionLog extends GenericPersistentObject {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AS_ACTIONLOG_GENERATOR")
	@SequenceGenerator(name = "AS_ACTIONLOG_GENERATOR", sequenceName = "AS_ACTIONLOG_ID_GENERATOR", allocationSize = 1, initialValue = 1)
	private Long id;

	private String ip;

	@Column(length = 100)
	private String macaddress;

	@Column(length = 255)
	private String programname;

    @Lob
    @Column(length = 20000)
	private String remark;

	@Column(length = 255)
	private String username;


}
