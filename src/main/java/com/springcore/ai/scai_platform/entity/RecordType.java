package com.springcore.ai.scai_platform.entity;

import com.springcore.ai.scai_platform.domain.extend.GenericPersistentObject;
import com.springcore.ai.scai_platform.domain.type.QueryType;
import com.springcore.ai.scai_platform.domain.type.QueryTypeAttributeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the ac_recordtype database table.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "ac_recordtype")
@NamedQuery(name = "AcRecordtype.findAll", query = "SELECT a FROM RecordType a")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RecordType extends GenericPersistentObject {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AC_RECORDTYPE_GENERATOR")
    @SequenceGenerator(name = "AC_RECORDTYPE_GENERATOR", sequenceName = "AC_RECORDTYPE_ID_GENERATOR", allocationSize = 1)
    private Long id;

    private String className;
    private String customActions;

   	@Lob
    @Column(length = 20000)
    private String customFrom;

   	@Lob
    @Column(length = 20000)
    private String customGroup;

    private String customOrder;

    @Convert(converter = QueryTypeAttributeConverter.class)
    private QueryType customQueryType;

   	@Lob
    @Column(length = 20000)
    private String customSelect;

   	@Lob
    @Column(length = 20000)
    private String customWhere;

   	@Lob
    @Column(length = 20000)
    private String description;

    private String eacFilter;

    private String entityType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expDate;

    private Long groupMenu;

    private String groupMenuCode;

    private String hbmFile;
    @Column(length = 2000)
    private String helpText;
    private String hint;
    private boolean inactive;

    private boolean custom;

//    @Column(name = "SCHEMA_FIELD"/*, columnDefinition = "boolean default false "*/)
    private boolean schemaField;

	/*enum TypeOfRecordType {
		SCHEMA, CUSTOM;
	}

	@Enumerated(EnumType.ORDINAL)
	private TypeOfRecordType typeOfRecordType*/

    private String tableName;

    private String indexField;

    @NotNull
    private String label;

    private String licType;
    private boolean loadOnInit;
    private Integer menuOrder;

    @NotNull
    private String name;

    private String namespace;
    private String onAfterQuery;
    private String onBeforeDelete;
    private String onBeforeQuery;
    private String onBeforeSave;
    private String onAfterSave;
    private String formDTOClass;
    private String overrideRecordType;
    private String overrideByRecordType;

    @ToString.Exclude
   	@Lob
    @Column(length = 20000)
    private String patFilter;

    private Long patId;

    @ToString.Exclude
   	@Lob
    @Column(length = 20000)
    private String prop;

    private String refRecordType;

    @ToString.Exclude
   	@Lob
    @Column(length = 20000)
    private String remarks;

    private String reportFilename;
    private String scripts;
    private String scriptStat;
    private String serviceClass;
    private String tab;
    private String unity;
    private String valstr;

    @ToString.Exclude
    //@OneToMany(fetch = FetchType.EAGER, mappedBy="acRecordtype", cascade = CascadeType.ALL)
    //@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    //@JoinColumn(name="parent_id", nullable=false)

    /*@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    private List<RecordTypeField> recordtypeFields = new ArrayList<>();*/
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "parent_id")
    @BatchSize(size = 20)
    private List<RecordTypeField> recordtypeFields = new ArrayList<>();

}
