package com.asiainfo.fsip.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "国家级项目成果库推送返回")
public class NationalProjectAchievementResp {

    @ApiModelProperty(value = "返回编码",name = "respCode")
    public String respCode;

    @ApiModelProperty(value = "返回消息",name = "respMsg")
    public String respMsg;

    @ApiModelProperty(value = "项目编码",name = "projectId")
    public String projectId;


}
