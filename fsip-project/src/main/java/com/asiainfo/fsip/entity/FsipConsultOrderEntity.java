package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 咨询工单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("FSIP_CONSULT_ORDER")
public class FsipConsultOrderEntity {
    /**
     * id
     */
    @TableId("ORDER_ID")
    @ApiModelProperty("工单编号")
    private String orderId;

    /**
     * 项目编码
     */
    @TableField("TARGET_ID")
    @ApiModelProperty("咨询对象编码")
    private String targetId;

    /**
     * 成果类型:NATIONAL=国家级、PROV=省级、CITY=市级
     */
    @TableField("TARGET_TYPE")
    @ApiModelProperty("咨询对象类型")
    private String targetType;

    /**
     * 状态
     */
    @TableField("STATUS")
    @ApiModelProperty("工单状态")
    private String status;

    /**
     * 咨询内容
     */
    @TableField("REQUEST")
    @ApiModelProperty("咨询内容")
    private String request;

    /**
     * 回复内容
     */
    @TableField("RESPONSE")
    @ApiModelProperty("回复内容")
    private String response;

    /**
     * 钉钉待办编码
     */
    @TableField("DING_TASK_ID")
    private Long dingTaskId;

    /**
     * 专家员工编码
     */
    @TableField("EXPERT_ID")
    @ApiModelProperty("专家员工编码")
    private String expertId;

    /**
     * 专家员工名称
     */
    @TableField("EXPERT_NAME")
    @ApiModelProperty("专家员工名称")
    private String expertName;


    /**
     * 申请时间
     */
    @TableField("REQ_TIME")
    @ApiModelProperty("申请时间")
    private Date reqTime;

    /**
     * 回复时间
     */
    @TableField("RESP_TIME")
    @ApiModelProperty("回复时间")
    private Date respTime;

    /**
     * 评分
     */
    @TableField("SCORE")
    @ApiModelProperty("评分")
    private String score;
}
