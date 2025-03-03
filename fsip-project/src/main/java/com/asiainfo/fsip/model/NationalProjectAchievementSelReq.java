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
@ApiModel(description = "国家级项目成果库推送请求")
public class NationalProjectAchievementSelReq {

    @ApiModelProperty(value = "项目编码",name = "achievementId")
    public String achievementId;

    @ApiModelProperty(value = "成果名称",name = "projectName")
    public String projectName;

    @ApiModelProperty(value = "获奖名称及等级",name = "awardsProjectNameLevel")
    public String awardsProjectNameLevel;

    @ApiModelProperty(value = "创造人名称",name = "otherCreateName")
    public String mainCreateName;

    @ApiModelProperty(value = "项目成果创建时间",name = "cycle")
    public String cycle;

}
