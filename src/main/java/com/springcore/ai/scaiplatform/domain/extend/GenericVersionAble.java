package com.springcore.ai.scaiplatform.domain.extend;

import java.util.Date;

public interface GenericVersionAble {

    void setCreateDate(Date createDate);

    String getCreateBy();

    void setCreateBy(String createBy);

    Date getUpdateDate();

    void setUpdateDate(Date updateDate);

    String getUpdateBy();

    void setUpdateBy(String updateBy);

    Long getVersion();

    void setVersion(Long version);

    Date getCreateDate();
}
