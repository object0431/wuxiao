package com.asiainfo.fsip.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "项目成果库地市转省分审核请求列表")
public class City2ProvAuditAchievementReq {

    @ApiModelProperty(value = "OA待办编码",name = "pendingCode")
    public String pendingCode;

}
