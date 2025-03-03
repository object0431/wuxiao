package com.asiainfo.fsip.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InnovationIssuesPartnerApplyReq {


    @ApiModelProperty(value = "议题编码")
    public String issuesId;

    @ApiModelProperty(value = "申请状态：01=待处理、02=拒绝、00=同意")
    public String applyState;

    @ApiModelProperty(value = "合伙人编码")
    public String partnerId;

    @ApiModelProperty(value = "申请回复")
    public String replyContent;
//
//    @ApiModelProperty(value = "钉钉代办编码")
//    public String dingId;
}
