package com.asiainfo.fsip.model;

import com.asiainfo.mcp.tmc.common.annotation.ExcelField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "国家级项目成果库查询返回")
public class NationalProjectAchievementSelResp {

    @ApiModelProperty(value = "成果编码",name = "achievementId")
    public String achievementId;

    @ApiModelProperty(value = "成果名称",name = "projectName")
    @ExcelField(name = "项目名称",columnWidth = 8000)
    public String projectName;

    @ApiModelProperty(value = "获奖名称及等级",name = "awardsProjectNameLevel")
    @ExcelField(name = "获奖名称",columnWidth = 4000)
    public String awardsProjectNameLevel;

    @ApiModelProperty(value = "成果申请年",name = "applyYearmon")
    @ExcelField(name = "申请年份",columnWidth = 3000)
    private String applyYearmon;

    @ApiModelProperty(value = "成果简介",name = "projectIntroduce")
    public String projectIntroduce;

    @ApiModelProperty(value = "创造人名称",name = "otherCreateName")
    @ExcelField(name = "主要创造人",columnWidth = 4000)
    public String mainCreateName;

    @ApiModelProperty(value = "参与创造人",name = "otherCreateName")
    public String otherCreateName;

    @ApiModelProperty(value = "申请人",name = "applierId")
    private String applierId;

    @ApiModelProperty(value = "申请人姓名",name = "applierName")
    private String applierName;

    @ApiModelProperty(value = "申请人部门公司",name = "applierCompanyId")
    private String applierCompanyId;

    @ApiModelProperty(value = "申请人部门编码",name = "applierDeptId")
    private String applierDeptId;

    @ApiModelProperty(value = "申请时间",name = "applyDate")
    private String applyDate;

    @ApiModelProperty(value = "背景图片",name = "backImage")
    private String backImage;

    @ApiModelProperty(value = "成果级别",name = "achievementType")
    private String achievementType;

    @ApiModelProperty(value = "附件列表",name = "attachmentList")
    private List<String> attachmentList;

}
