package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "下个审批节点信息Model")
public class NextApprovalNodeModel {

    @ApiModelProperty(value = "是否有下个环节")
    private boolean hasNext;

    @ApiModelProperty(value = "审批类型")
    private String apprOfficer;

    @ApiModelProperty(value = "环节编码")
    private String nodeCode;

    @ApiModelProperty(value = "环节名称")
    private String nodeName;

    // @ApiModelProperty(value = "排序")
    // private int sort;


    @ApiModelProperty(value = "人员编码")
    private String staffId;

    @ApiModelProperty(value = "人员名称")
    private String staffName;

    @ApiModelProperty(value = "手机号码")
    private String mobilePhone;

    @ApiModelProperty(value = "职务")
    private String position;
}
