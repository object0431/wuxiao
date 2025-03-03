package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 专家建议
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("FSIP_EXPERT_ADVICE")
public class FsipExpertAdviceEntity {
    /**
     * id
     */
    @TableId("ID")
    private Integer id;
    /**
     * 项目编码
     */
    @TableField("TARGET_ID")
    private String targetId;
    /**
     * 成果类型:立项  成果
     */
    @TableField("TARGET_TYPE")
    private String targetType;
    /**
     * 状态
     */
    @TableField("STATUS")
    private String status;
    /**
     * 建议内容
     */
    @TableField("SUGGESTION")
    private String suggestion;
    /**
     * 申请人
     */
    @TableField("APPLIER_ID")
    private String applierId;
    /**
     * 申请姓名
     */
    @TableField("APPLIER_NAME")
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
    @TableField("REQ_TIME")
    private Date reqTime;
    /**
     * 回复时间
     */
    @TableField("RESP_TIME")
    private Date respTime;
    /**
     * 评分
     */
    @TableField("SCORE")
    private Float score;
}
