package com.asiainfo.fsip.model;

import com.asiainfo.mcp.tmc.common.annotation.ExcelField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "市级成果评分导出专用")
public class ProjectAchievementExportModel {

    @ApiModelProperty(value = "项目名称")
    @ExcelField(name = "项目名称",columnWidth = 8000)
    private String projectName;

    @ApiModelProperty(value = "所属类别：（小发明、小创造、小革新、小设计、小建议）")
    @ExcelField(name = "所属类别",columnWidth = 3000)
    private String innovationType;

    @ApiModelProperty(value = "所属项目：（系统类、流程类、成本与管理类、服务支撑类）")
    @ExcelField(name = "所属项目",columnWidth = 4000)
    private String projectType;

    @ApiModelProperty(value = "申请人部门公司")
    @ExcelField(name = "公司",columnWidth = 8000)
    private String applierCompanyId;

    @ApiModelProperty(value = "申请人部门编码")
    @ExcelField(name = "部门",columnWidth = 8000)
    private String applierDeptId;

    @ApiModelProperty(value = "申请人名称")
    @ExcelField(name = "申请人",columnWidth = 3000)
    private String applierName;

    @ApiModelProperty(value = "状态名称")
    @ExcelField(name = "状态",columnWidth = 4000)
    private String statusName;

    @ApiModelProperty(value = "评分")
    @ExcelField(name = "评分",columnWidth = 3000)
    private String score;

    @ApiModelProperty(value = "申请时间")
    @ExcelField(name = "创建时间",columnWidth = 6000)
    private String applyDate;
}
