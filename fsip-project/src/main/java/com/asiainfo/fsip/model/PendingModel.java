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
@ApiModel(description = "待办信息")
public class PendingModel {

    @ApiModelProperty("操作类型")
    private String operType;

    @ApiModelProperty("审核信息")
    private ApprovalApplyReq approvalReq;

    @ApiModelProperty("待办编码")
    private String pendingCode;

    @ApiModelProperty("待办标题")
    private String pendingTitle;

    @ApiModelProperty("待办类型")
    private String taskType;

    @ApiModelProperty("待办ID")
    private String approvalId;

    @ApiModelProperty("待办url")
    private String pendingUrl;

    @ApiModelProperty("移动端Url")
    private String mobileUrl;

    @ApiModelProperty("待办状态")
    private String taskStatus;

    @ApiModelProperty("待办内容")
    private String content;

}
