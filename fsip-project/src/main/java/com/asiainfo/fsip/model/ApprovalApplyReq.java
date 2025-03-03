package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "审批节点信息")
public class ApprovalApplyReq {

    @ApiModelProperty(value = "审批意见")
    private String opinion;

    @ApiModelProperty(value = "审批环节")
    private List<ApprovalNode> approvalNodeList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ApprovalNode {

        @ApiModelProperty(value = "环节编码")
        private String nodeCode;

        @ApiModelProperty(value = "环节名称")
        private String nodeName;

        @ApiModelProperty(value = "环节状态")
        private String nodeState;

        @ApiModelProperty(value = "序号")
        private int sort;

        @ApiModelProperty(value = "审批人类型")
        private String apprOfficer;

        @ApiModelProperty(value = "审批人编码")
        private String approveId;

        @ApiModelProperty(value = "审批人名称")
        private String approveName;

        @ApiModelProperty(value = "市转省")
        private String city2Prov;

        @ApiModelProperty(value = "扩展信息")
        private String extValue;
    }
}
