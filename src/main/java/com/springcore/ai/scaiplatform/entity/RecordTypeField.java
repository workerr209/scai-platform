package com.springcore.ai.scaiplatform.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springcore.ai.scaiplatform.domain.converter.CustomPropertyAttributeConverter;
import com.springcore.ai.scaiplatform.domain.deserialiize.NumericBooleanDeserializer;
import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import com.springcore.ai.scaiplatform.domain.type.CustomProperty;
import com.springcore.ai.scaiplatform.domain.type.OptionMapLabel;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * The persistent class for the ac_recordtypefield database table.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "ac_recordtypefield")
@NamedQuery(name = "AcRecordtypefield.findAll", query = "SELECT a FROM RecordTypeField a")
public class RecordTypeField extends GenericPersistentObject {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AC_RECORDTYPEFIELD_GENERATOR")
    @SequenceGenerator(name = "AC_RECORDTYPEFIELD_GENERATOR", sequenceName = "AC_RECORDTYPEFIELD_ID_GENERATOR", allocationSize = 1)
    private Long id;

    private String col;

    @Column(name = "DATATYPE")
    private String dataType;

    @Column(name = "DEFAULTPATTERN")
    private String defaultPattern;

    @Lob
    @Column(name = "DEFAULTVALUE", length = 20000)
    private String defaultValue;

    @Lob
    @Column(length = 20000)
    private String description;

    @Column(name = "DISPLAYCOL")
    private Integer displayCol;

    @Column(name = "DISPLAYROW")
    private Integer displayRow;

    @Column(name = "DISPLAYSECTION")
    private String displaySection;

    @Column(name = "DISPLAYSEQ")
    private Integer displaySeq;

    @Column(name = "FIELDSEQ")
    private Integer fieldSeq;

    @Column(name = "FIELDTYPE")
    private Integer fieldType;

    @Column(name = "IS_ADVFILTEREN" /* , columnDefinition = "boolean default false" */)
    private Boolean advFilterEn;

    @Column(name = "FILTERFIELD")
    private String filterField;

    @Column(name = "FILTERKEY")
    private String filterKey;

    @Column(name = "FILTEROP")
    private String filterOp;

    @Column(name = "FILTERVAL")
    private String filterVal;

    @Column(name = "GROUPLEVEL")
    private Integer groupLevel;

    @Column(name = "HELP_TEXT")
    private String helpText;
    private String columnName;
    private String hint;

    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    @Column(name = "IS_INDEX"/* , columnDefinition = "boolean default false" */)
    private Boolean isIndex;

    private String indexValue;

    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    @Column(name = "IS_REQUIRED" /* , columnDefinition = "boolean default false " */)
    private Boolean isRequired;

    @Column(name = "SCHEMA_FIELD")
    private String schema;

    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    @Column(name = "IS_UNIQUE" /* , columnDefinition = "boolean default false " */)
    private Boolean isUnique;

    @Column(name = "ISVISIBLE")
    private Integer isVisible;

    private String label;
    private String name;
    private String namespace;
    private Integer numPrecision;
    private Integer numScale;
    private String onBlur;
    private String onChange;

    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    @Column(name = "IS_SEARCH_REQUIRED"/* ,  columnDefinition = "boolean default false" */)
    private Boolean isSearchRequired;

    @Lob
    @Column(length = 20000)
    private String optionLabels;

    @Lob
    @Column(length = 20000)
    private String optionValues;

	/*@Column(name = "parent_id", nullable = false)
	private Long parentId;*/
    public OptionMapLabel getOptionMapLabel() {
        String optionLabels = this.optionLabels;
        String optionValues = this.optionValues;
        if (StringUtils.isEmpty(optionLabels) || StringUtils.isEmpty(optionValues)) {
            return null;
        }

        OptionMapLabel optionMapLabel = new OptionMapLabel();
        String[] optionLabelsArr = optionLabels.split(";");
        String[] optionValuesArr = optionValues.split(";");
        for (int i = 0; i < optionValuesArr.length; i++) {
            optionMapLabel.put(optionValuesArr[i], optionLabelsArr[i]);
        }

        return optionMapLabel;
    }

    @ToString.Exclude
    @Lob
    @Convert(converter = CustomPropertyAttributeConverter.class)
    @Column(length = 20000)
    private CustomProperty prop;

    @Lob
    @Column(length = 20000)
    private String refRecordTypeFields;
    private String relateRecordBack;
    private Long relateRecordTypeId;
    private String relateRecordTypeName;
    private String scriptStat;
    private String scriptTable;
    private Integer txtLength;
    private String unity;
    private Integer visibleLines;
    private Integer visibleWidth;

}
