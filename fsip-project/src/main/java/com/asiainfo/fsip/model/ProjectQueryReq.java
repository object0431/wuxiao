package com.asiainfo.fsip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectQueryReq {
    private String isSelf;
    private String typeAttrCode;
    private String projectAttrCode;
    private String projectName;
    private String projectState;
    private String applierCompanyId;
    private String applierDeptId;
    private int pageNum;
    private int pageSize;
}
