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
@ApiModel(description = "回复咨询请求报文")
public class ConsultationReplyReq {

    @ApiModelProperty(value = "咨询工单编码")
    private String orderId;

    @ApiModelProperty(value = "回复内容")
    private String replyContent;

}
