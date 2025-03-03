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
@ApiModel(description = "项目成果统计请求参数")
public class ProjectAchievementStatisticsReq {
    @ApiModelProperty(value = "申请人公司名称",name = "applierCompanyId")
    private String applierCompanyId;

    @ApiModelProperty(value = "申请部门",name = "applierDeptId")
    private String applierDeptId;

    @ApiModelProperty(value = "申请人",name = "applierId")
    private String applierId;
}
