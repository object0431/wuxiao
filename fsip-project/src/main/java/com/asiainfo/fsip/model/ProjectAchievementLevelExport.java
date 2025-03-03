package com.asiainfo.fsip.model;

import com.asiainfo.mcp.tmc.common.annotation.ExcelField;
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
@ApiModel(description = "市级成果评级导出专用")
public class ProjectAchievementLevelExport {

    @ApiModelProperty(value = "项目名称",name = "projectName")
    @ExcelField(name = "项目名称",columnWidth = 8000)
    private String projectName;

    @ApiModelProperty(value = "所属类别",name = "innovationType")
    @ExcelField(name = "所属类别",columnWidth = 3500)
    private String innovationType;

    @ApiModelProperty(value = "所属项目",name = "projectType")
    @ExcelField(name = "所属项目",columnWidth = 3500)
    private String projectType;

    @ApiModelProperty(value = "申请人名称",name = "applierName")
    @ExcelField(name = "申请人",columnWidth = 3500)
    private String applierName;

    @ApiModelProperty(value = "公司",name = "applierCompanyId")
    @ExcelField(name = "公司",columnWidth = 4500)
    private String applierCompanyId;

    @ApiModelProperty(value = "部门",name = "applierDeptId")
    @ExcelField(name = "部门",columnWidth = 4500)
    private String applierDeptId;

    @ApiModelProperty(value = "申请时间",name = "applyDate")
    @ExcelField(name = "创建时间",columnWidth = 4500)
    private String applyDate;

    @ApiModelProperty(value = "平均评分",name = "avgScore")
    @ExcelField(name = "评分",columnWidth = 3000)
    private String avgScore;

    @ApiModelProperty(value = "获奖级别",name = "awardLevel")
    @ExcelField(name = "评奖",columnWidth = 3500)
    private String awardLevel;

}
