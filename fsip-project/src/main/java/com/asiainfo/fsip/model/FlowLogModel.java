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
@ApiModel(description = "流转意见model")
public class FlowLogModel {

    @ApiModelProperty(value = "任务编码")
    private String extId;

    @ApiModelProperty(value = "节点编码")
    private String nodeCode;

    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    @ApiModelProperty(value = "操作时间")
    private String operateTime;

    @ApiModelProperty(value = "操作员")
    private String operateId;

    @ApiModelProperty(value = "操作员名称")
    private String operateName;

    @ApiModelProperty(value = "审核结果")
    private String approvalRet;

    @ApiModelProperty(value = "是否完成")
    private String isComplete;

    @ApiModelProperty(value = "审批意见")
    private String remark;
}