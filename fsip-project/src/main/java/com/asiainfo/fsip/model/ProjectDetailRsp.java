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
public class ProjectDetailRsp {
    private String projectId;
    private String projectName;
    private String startTime;
    private String endTime;
    private String economicBenefit;
    private String typeAttrCode;
    private String typeAttrName;
    private String projectAttrCode;
    private String projectAttrName;
    private String menberNames;
    private String status;
    private String applierId;
    private String applierName;
    private String applyDeptId;
    private String applyDeptName;
    private String applyCompanyId;
    private String applyCompanyName;
    private List<ProjectAttr> projectAttrList;
    private List<ApprovalFlow> approvalFlowList;
    private List<ExpertAdvice> expertAdviceList;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ApprovalFlow{
        private String nodeCode;
        private String nodeName;
        private String operateTime;
        private String operateId;
        private String operateName;
        private String approvalRet;
        private String isComplete;
        private String remark;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExpertAdvice{
        private String suggestion;
        private String score;
        private String reqTime;
        private String respTime;
        private String state;
    }
}
