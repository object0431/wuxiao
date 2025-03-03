package com.asiainfo.fsip.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
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
@TableName("fsip_project_achievement")
@ApiModel(value="FsipProjectAchievement对象", description="")
public class FsipProjectAchievementEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "成果编码")
    @TableId(value = "ACHIEVEMENT_ID", type = IdType.INPUT)
    private String achievementId;

    @ApiModelProperty(value = "项目名称")
    @TableField(value = "PROJECT_NAME")
    private String projectName;

    @ApiModelProperty(value = "项目开始时间")
    @TableField(value = "START_DATE")
    private Date startDate;

    @ApiModelProperty(value = "项目结束时间")
    @TableField(value = "END_DATE")
    private Date endDate;

    @ApiModelProperty(value = "经济效益/社会效益（万元）")
    @TableField(value = "BENEFIT")
    private Float benefit;

    @ApiModelProperty(value = "所属类别：（小发明、小创造、小革新、小设计、小建议）")
    @TableField(value = "INNOVATION_TYPE")
    private String innovationType;

    @ApiModelProperty(value = "所属项目：（系统类、流程类、成本与管理类、服务支撑类）")
    @TableField(value = "PROJECT_TYPE")
    private String projectType;

    @ApiModelProperty(value = "状态：ZC=暂存、01=部门领导审批、03=工会主席审批、04=评审委员会审批、00=审批完成、TH=退回修改")
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

    @ApiModelProperty(value = "申请人")
    @TableField(value = "APPLIER_ID")
    private String applierId;

    @ApiModelProperty(value = "申请人姓名")
    @TableField(value = "APPLIER_NAME")
    private String applierName;

    @ApiModelProperty(value = "申请人部门公司")
    @TableField(value = "APPLIER_COMPANY_ID")
    private String applierCompanyId;

    @ApiModelProperty(value = "申请人部门编码")
    @TableField(value = "APPLIER_DEPT_ID")
    private String applierDeptId;

    @ApiModelProperty(value = "申请时间")
    @TableField(value = "APPLY_DATE")
    private Date applyDate;

    @ApiModelProperty(value = "地市转省分成果")
    @TableField(value = "CITY_TO_PROV_FLAG")
    private String cityToProvFlag;

    @ApiModelProperty(value = "背景图片")
    @TableField(value = "BACK_IMAGE")
    private String backImage;

    @ApiModelProperty(value = "转省级成果审批结果 1审批中 2审批同意 3审批不同意")
    @TableField(value = "sjcgsp_status")
    private String sjcgspStatus;

    @TableField(exist = false)
    private Integer total;
}

