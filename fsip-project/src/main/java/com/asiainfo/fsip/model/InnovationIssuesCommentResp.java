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
@ApiModel(value = "议题评论信息请求")
public class InnovationIssuesCommentResp {

    @ApiModelProperty(value = "返回编码",name = "respCode")
    public String respCode;

    @ApiModelProperty(value = "返回消息",name = "respMsg")
    public String respMsg;
}
