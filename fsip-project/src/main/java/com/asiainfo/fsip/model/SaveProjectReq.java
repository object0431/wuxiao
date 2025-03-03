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
public class SaveProjectReq {
    private String operateType;
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
    private List<ProjectAttr> projectAttrList;
    private ApprovalApplyReq applyReq;

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
    public static class HandleInfo{
        private String opinion;
        private List<ApprovalInfo> approvalInfoList;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class ApprovalInfo{
            private String nodeCode;
            private String nodeName;
            private String apprType;
            private String approveId;
            private String approveName;
        }
    }
}
