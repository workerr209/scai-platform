package com.springcore.ai.scai_platform.factory.query;

import com.springcore.ai.scai_platform.entity.RecordType;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

final class RecordTypePropertyProcessor {

    private final RecordType recordType;

    public RecordTypePropertyProcessor(RecordType recordType) {
        this.recordType = recordType;
        process();
    }

    @Getter
    private WhereFilter whereFilter;

    @Getter
    private QueryMapping queryMapping;

    @Getter
    private UsernameFilter usernameFilter;
    
    @Getter
    private ConJunction conJunction;


    public void process() {
        String queryMappingClass = "GeneralMapping";
        String whereFilterCode = null;
        String whereFilterName = null;
        String whereUserName = null;
        String conJunctionCri = null;
        final String prop = this.recordType.getProp();

        if(StringUtils.isNotEmpty(prop)) {
            for(String props : prop.split("\\[SEP.]")) {
                if(props.startsWith("whereFilter")){
                    for(String whereFilter : props.split("=")[1].split(",")) {
                        if(whereFilter.startsWith("code")) {
                            whereFilterCode = whereFilter.split(":")[1];
                        } else if(whereFilter.startsWith("name")) {
                            whereFilterName = whereFilter.split(":")[1];
                        }
                    }
                }else if(props.startsWith("RoleValueMapping")){
                    queryMappingClass = props.split("=")[1];
                }else if(props.startsWith("whereUserName")) {
                    whereUserName = props.split("=")[1];
                }else if(props.startsWith("conJunction")) {
                	conJunctionCri = props.split("=")[1];
                }
            }
        }

        WhereFilter whereFilter = new WhereFilter();
        whereFilter.code = whereFilterCode;
        whereFilter.name = whereFilterName;
        this.whereFilter = whereFilter;

        QueryMapping queryMapping = new QueryMapping();
        queryMapping.className = queryMappingClass;
        this.queryMapping = queryMapping;

        UsernameFilter usernameFilter = new UsernameFilter();
        usernameFilter.username = whereUserName;
        this.usernameFilter = usernameFilter;
        
        ConJunction conJunctionObj = new ConJunction();
        conJunctionObj.conJunction = conJunctionCri;
        this.conJunction = conJunctionObj;
    }

    @Getter
    static class WhereFilter {
        private String code;
        private String name;
    }

    @Getter
    static class UsernameFilter {
        private String username;
    }

    @Getter
    static class QueryMapping {
        private String className;
        public String getFullClassName() {
            return "com.springcore.ai.scai_platform.domain.mapping." + this.className;
        }
    }
    
    @Getter
    static class ConJunction {
        private String conJunction;
    }
}
