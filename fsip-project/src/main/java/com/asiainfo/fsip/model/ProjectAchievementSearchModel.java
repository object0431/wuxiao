package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "项目检索model")
public class ProjectAchievementSearchModel {

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目编号")
    private String projectId;

    @ApiModelProperty(value = "项目类型")
    private String projectType;

    @ApiModelProperty(value = "项目状态")
    private String state;

    @ApiModelProperty(value = "所属类别")
    private String innovationType;

    @ApiModelProperty(value = "申请人部门公司")
    private String applierCompanyId;

    @ApiModelProperty(value = "申请人部门编码")
    private String applierDeptId;

    @ApiModelProperty(value = "是否查自己")
    private String isOwn;

    @ApiModelProperty(value = "成果等级 PROV:省级, CITY:市级, DEPT:部门级")
    private String achievementType;

    @ApiModelProperty(value = "经济效益(用减号分隔，如 1.0-2.0)")
    private String benefit;
    @ApiModelProperty(hidden = true)
    private BigDecimal benefitMin;
    @ApiModelProperty(hidden = true)
    private BigDecimal benefitMax;

    @ApiModelProperty(value = "获奖等级 CITY_1:市级一等奖,CITY_2:市级二等奖,CITY_3:市级三等奖,PROV_1:省级一等奖,PROV_2:省级二等奖,PROV_3:省级三等奖")
    private String awardLevel;

    @ApiModelProperty(value = "转省级成果审批结果 空-不限 1审批中 2审批同意 3审批不同意")
    private String sjcgsp;
}
