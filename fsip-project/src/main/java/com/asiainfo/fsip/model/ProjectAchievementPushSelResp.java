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
@ApiModel(description = "项目成果库推送列表返回")
public class ProjectAchievementPushSelResp {

    @ApiModelProperty(value = "成果编码",name = "achievementId")
    private String achievementId;

    @ApiModelProperty(value = "项目名称",name = "projectName")
    @ExcelField(name = "项目名称", sort = 1)
    private String projectName;

    @ApiModelProperty(value = "开始时间",name = "startDate")
    private String startDate;

    @ApiModelProperty(value = "结束时间",name = "endDate")
    private String endDate;

    @ApiModelProperty(value = "经济效益/社会效益",name = "benefit")
    private String benefit;

    @ApiModelProperty(value = "所属类别",name = "innovationType")
    @ExcelField(name = "所属类别", sort = 2)
    private String innovationType;

    @ApiModelProperty(value = "所属项目",name = "projectType")
    @ExcelField(name = "所属项目", sort = 3)
    private String projectType;

    @ApiModelProperty(value = "申请人",name = "applierId")
    private String applierId;

    @ApiModelProperty(value = "申请人名称",name = "applierName")
    @ExcelField(name = "申请人", sort = 5)
    private String applierName;

    @ApiModelProperty(value = "申请人部门公司",name = "applierCompanyId")
    @ExcelField(name = "部门", sort = 4)
    private String applierCompanyId;

    @ApiModelProperty(value = "申请人部门编码",name = "applierDeptIid")
    private String applierDeptId;

    @ApiModelProperty(value = "申请时间",name = "applyDate")
    @ExcelField(name = "创建时间", sort = 7)
    private String applyDate;

    @ApiModelProperty(value = "平均评分",name = "avgScore")
    @ExcelField(name = "评分", sort = 6)
    private String avgScore;

    @ApiModelProperty(value = "获奖级别",name = "awardLevel")
    private String awardLevel;

    @ApiModelProperty(value = "成果类型",name = "achievementType")
    private String achievementType;

    @ApiModelProperty(value = "当前审批环节",name = "apprNodeCode")
    private String apprNodeCode;

    @ApiModelProperty(value = "状态",name = "status")
    private String status;

    @ApiModelProperty(value = "年月",name = "applyYearmon")
    private String applyYearmon;

    @ApiModelProperty(value = "地市转省级标识",name = "cityToProvFlag")
    private String cityToProvFlag;

    @ApiModelProperty(value = "OA待办编码",name = "pendingCode")
    private String pendingCode;

    @ApiModelProperty(value = "钉钉待办编码",name = "dingTaskId")
    private String dingTaskId;

}
