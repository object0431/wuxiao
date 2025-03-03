package com.asiainfo.fsip.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAchievementAddReq {
    private ProjectAchievementModel projectModel;
    private ApprovalApplyReq applyReq;
  //  private HandleInfo handleInfo;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HandleInfo {
        private String opinion;
        @com.asiainfo.mcp.tmc.common.base.annotation.MustField(mustValidate = true, validateDesc = "审批步骤列表不能为空")
        List<ApprovalInfo> approvalInfoList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApprovalInfo {
        @com.asiainfo.mcp.tmc.common.base.annotation.MustField(mustValidate = true, validateDesc = "节点编码不能为空")
        private String nodeCode;
        private String nodeName;

        private String nodeState;
        private int sort;
        @com.asiainfo.mcp.tmc.common.base.annotation.MustField(mustValidate = true, validateDesc = "审批类型不能为空")
        private String apprOfficer;
        @com.asiainfo.mcp.tmc.common.base.annotation.MustField(mustValidate = true, validateDesc = "审批人编码不能为空")
        private String approveId;
        private String approveName;
    }
}
