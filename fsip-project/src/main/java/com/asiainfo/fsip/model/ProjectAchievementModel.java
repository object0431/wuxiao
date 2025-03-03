package com.asiainfo.fsip.model;

import com.asiainfo.mcp.tmc.common.annotation.ExcelField;
import com.baomidou.mybatisplus.generator.config.po.TableField;
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
@ApiModel(description = "项目成果model")
public class ProjectAchievementModel {

    @ApiModelProperty(value = "项目名称")
    @ExcelField(name = "项目名称",columnWidth = 8000)
    private String projectName;

    @ApiModelProperty(value = "项目编号")
    private String projectId;

    @ApiModelProperty(value = "项目开始时间")
    private String startDate;
    private Date startDateDate;

    @ApiModelProperty(value = "项目结束时间")
    private String endDate;
    private Date endDateDate;

    @ApiModelProperty(value = "所属类别：（小发明、小创造、小革新、小设计、小建议）")
    @ExcelField(name = "所属类别",columnWidth = 3000)
    private String innovationType;

    @ApiModelProperty(value = "所属项目：（系统类、流程类、成本与管理类、服务支撑类）")
    @ExcelField(name = "所属项目",columnWidth = 4000)
    private String projectType;

    @ApiModelProperty(value = "申请人")
    private String applierId;

    @ApiModelProperty(value = "市评分")
    @ExcelField(name = "市级评分",columnWidth = 2000)
    private String cityScore;

    @ApiModelProperty(value = "省评分")
    @ExcelField(name = "省级评分",columnWidth = 2000)
    private String provScore;

    @ApiModelProperty(value = "成果类型名称")
    @ExcelField(name = "成果等级",columnWidth = 3000)
    private String achievementTypeName;

    @ApiModelProperty(value = "获奖等级名称")
    @ExcelField(name = "获奖等级",columnWidth = 3100)
    private String awardLevelName;

    @ApiModelProperty(value = "经济效益")
    @ExcelField(name = "经济效益(万元)",columnWidth = 2500)
    private String benefit;
    private float benefitFloat;

    @ApiModelProperty(value = "申请人名称")
    @ExcelField(name = "申请人",columnWidth = 3000)
    private String applierName;

    @ApiModelProperty(value = "申请人部门公司")
    @ExcelField(name = "公司",columnWidth = 8000)
    private String applierCompanyId;

    @ApiModelProperty(value = "申请人部门编码")
    @ExcelField(name = "部门",columnWidth = 8000)
    private String applierDeptId;

    @ApiModelProperty(value = "申请时间")
    @ExcelField(name = "申请时间",columnWidth = 6000)
    private String applyDate;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "状态名称")
    @ExcelField(name = "状态",columnWidth = 4000)
    private String statusName;

    @ApiModelProperty(value = "转省级成果审批结果 空-不限 1审批中 2审批同意 3审批不同意")
    private String sjcgsp;
    @ApiModelProperty(value = "转省级成果审批结果 审批中 审批同意 审批不同意")
    @ExcelField(name = "转省级成果审批结果",columnWidth = 4000)
    private String sjcgspName;

    @ApiModelProperty(value = "背景图片")
    private String backImage;

    @ApiModelProperty(value = "已评分的数量")
    private Integer scoreCount;

    @ApiModelProperty(value = "总评分的数量")
    private Integer scoreTotal;

    @ApiModelProperty(value = "平均分")
    private String avgScore;

    @ApiModelProperty(value = "当前评分状态 ZC暂存、00已提交、为空表示未进行任何评分")
    private String scoreStatus;

    @ApiModelProperty(value = "评分")
    private String score;

    @ApiModelProperty(value = "成果类型")
    private String achievementType;

    @ApiModelProperty(value = "获奖等级")
    private String awardLevel;

    @ApiModelProperty(value = "项目属性信息")
    private List<AchievementItem> itemList;

    @ApiModelProperty(value = "审批信息")
    private List<ApprovalInfo> approvalInfoList;

    @ApiModelProperty(value = "附件列表",required = true)
    private List<FileModel> appendixList;

    @ApiModelProperty(value = "评级附件")
    private List<AchievementItem> awardAttachList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(description = "项目属性信息")
    public static class AchievementItem {
        @ApiModelProperty(value = "属性类型")
        private String itemType;

        @ApiModelProperty(value = "属性编码")
        private String itemCode;

        @ApiModelProperty(value = "属性名称")
        private String itemName;

        @ApiModelProperty(value = "属性值")
        private String itemValue;

        @ApiModelProperty(value = "排序")
        private Integer sort;

        @ApiModelProperty(value = "评分")
        private String score;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(description = "项目审批信息")
    public static class ApprovalInfo {
        @ApiModelProperty(value = "节点名称")
        private String nodeName;

        @ApiModelProperty(value = "审批人")
        private String dealStaffName;

        @ApiModelProperty(value = "审批时间")
        private String dealTime;

        @ApiModelProperty(value = "意见")
        private String remark;
    }
}
