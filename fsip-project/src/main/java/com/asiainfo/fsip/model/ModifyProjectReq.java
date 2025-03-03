package com.asiainfo.fsip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModifyProjectReq {
    private String projectId;
    private String projectName;
    private String startTime;
    private String endTime;
    private String economicBenefit;
    private String typeAttrCode;
    private String typeAttrName;
    private String projectAttrCode;
    private String projectAttrName;
    private List<ProjectAttr> projectAttrList;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProjectAttr{
        private String attrType;
        private String attrCode;
        private String attrName;
        private String attrValue;
    }

}
