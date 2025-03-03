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
@ApiModel(description = "审批节点信息l")
public class ApprovalNodeModel {

    @ApiModelProperty(value = "审批类型")
    private String apprOfficer;

    @ApiModelProperty(value = "环节编码")
    private String nodeCode;

    @ApiModelProperty(value = "环节名称")
    private String nodeName;

    @ApiModelProperty(value = "环节状态")
    private String nodeState;

    @ApiModelProperty(value = "下一环节状态")
    private String nextNodeState;

    @ApiModelProperty(value = "排序")
    private int sort;

    @ApiModelProperty(value = "选择人数：1=选择一个、ALL=选择全部")
    private String apprNumber;

    private List<Officer> officerList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(description = "审批人员信息Model")
    public static class Officer {
        @ApiModelProperty(value = "人员编码")
        private String staffId;

        @ApiModelProperty(value = "人员名称")
        private String staffName;

        @ApiModelProperty(value = "手机号码")
        private String mobilePhone;

        @ApiModelProperty(value = "职务")
        private String position;
    }
}
