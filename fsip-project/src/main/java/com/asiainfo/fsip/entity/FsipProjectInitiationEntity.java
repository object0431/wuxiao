package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目立项信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("FSIP_PROJECT_INITIATION")
public class FsipProjectInitiationEntity {
    /**
     * 项目编码
     */
    @TableId("PROJECT_ID")
    private String projectId;
    /**
     * 项目名称
     */
    @TableField("PROJECT_NAME")
    private String projectName;
    /**
     * 项目开始时间
     */
    @TableField("START_DATE")
    private Date startDate;
    /**
     * 项目结束时间
     */
    @TableField("END_DATE")
    private Date endDate;
    /**
     * 经济效益/社会效益（万元）
     */
    @TableField("EXPECTED_BENEFITS")
    private BigDecimal expectedBenefits;
    /**
     * 所属类别：（小发明、小创造、小革新、小设计、小建议）
     */
    @TableField("INNOVATION_TYPE")
    private String innovationType;
    /**
     * 所属项目：（系统类、流程类、成本与管理类、服务支撑类）
     */
    @TableField("PROJECT_TYPE")
    private String projectType;
    /**
     * 状态
     */
    @TableField("STATUS")
    private String status;
    /**
     * 当前审批环节
     */
    @TableField("APPR_NODE_CODE")
    private String apprNodeCode;
    /**
     * OA待办编码
     */
    @TableField("PENDING_CODE")
    private String pendingCode;
    /**
     * 钉钉待办编码
     */
    @TableField("DING_TASK_ID")
    private Long dingTaskId;
    /**
     * 申请人
     */
    @TableField("APPLIER_ID")
    private String applierId;

    /**
     * 团队成员名称多个用","分隔
     */
    @TableField("MEMBER_NAMES")
    private String memberNames;


    @ApiModelProperty(value = "申请人姓名")
    @TableField(value = "APPLIER_NAME")
    private String applierName;

    /**
     * 申请人部门公司
     */
    @TableField("APPLIER_COMPANY_ID")
    private String applierCompanyId;
    /**
     * 申请人部门编码
     */
    @TableField("APPLIER_DEPT_ID")
    private String applierDeptId;
    /**
     * 申请时间
     */
    @TableField("APPLY_DATE")
    private Date applyDate;

    @TableField(exist = false)
    private Integer total;
}
