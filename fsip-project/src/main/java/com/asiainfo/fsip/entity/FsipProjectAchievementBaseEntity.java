package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2023-08-08
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fsip_project_achievement_base")
@ApiModel(value="FsipProjectAchievementBase对象", description="")
public class FsipProjectAchievementBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "成果编码")
    @TableId(value = "ACHIEVEMENT_ID", type = IdType.INPUT)
    private String achievementId;

    @ApiModelProperty(value = "成果类型:NATIONAL=国家级、PROV=省级、CITY=市级")
    @TableField(value = "ACHIEVEMENT_TYPE")
    private String achievementType;

    @ApiModelProperty(value = "状态:03=工会主席审批、04=评审委员会审批、00=审批完成、TH=退回修改")
    @TableField(value = "STATUS")
    private String status;

    @ApiModelProperty(value = "当前审批环节")
    @TableField(value = "APPR_NODE_CODE")
    private String apprNodeCode;

    @ApiModelProperty(value = "OA待办编码")
    @TableField(value = "PENDING_CODE")
    private String pendingCode;

    @ApiModelProperty(value = "钉钉待办编码")
    @TableField(value = "DING_TASK_ID")
    private Long dingTaskId;

    @ApiModelProperty(value = "获奖级别")
    @TableField(value = "AWARD_LEVEL")
    private String awardLevel;

    @ApiModelProperty(value = "申请人")
    @TableField(value = "APPLIER_ID")
    private String applierId;

    @ApiModelProperty(value = "申请人中文名")
    @TableField(value = "APPLIER_NAME")
    private String applierName;

    @ApiModelProperty(value = "申请人部门编码")
    @TableField(value = "APPLIER_DEPT_ID")
    private String applierDeptId;

    @ApiModelProperty(value = "申请人部门公司")
    @TableField(value = "APPLIER_COMPANY_ID")
    private String applierCompanyId;

    @ApiModelProperty(value = "申请时间")
    @TableField(value = "APPLY_DATE")
    private Date applyDate;

    @ApiModelProperty(value = "申请年月：国家级、省级保留年份、市级保留年月")
    @TableField(value = "APPLY_YEARMON")
    private String applyYearmon;

    @ApiModelProperty(value = "地市转省成果")
    @TableField(value = "CITY_TO_PROV_FLAG")
    private String cityToProvFlag;

    @ApiModelProperty(value = "国家级奖项级别名称")
    @TableField(value = "AWARDS_PROJECT_NAME_LEVEL")
    private String awardsProjectNameLevel;

    @ApiModelProperty(value = "主创始人")
    @TableField(value = "MAIN_CREATE_NAME")
    private String mainCreateName;

    @ApiModelProperty(value = "其他创始人")
    @TableField(value = "OTHER_CREATE_NAME")
    private String otherCreateName;


}
