package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description project_procurement_info
 * @author BEJSON
 * @date 2024-04-15
 */
@Data
public class ProjectProcurementInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
    * 项目名称
    */
    private String name;

    /**
    * 所履区域
    */
    private String region;

    /**
    * 招标代理公司
    */
    private String tenderAgent;

    /**
    * 代理项目经理
    */
    private String projectManager;

    /**
    * 需求经理
    */
    private String requirementManager;

    /**
    * 采购经理
    */
    private String procurementManager;

    /**
    * 预算金额（万元）
    */
    private BigDecimal budgetAmount;

    /**
    * 采购方式
    */
    private String procurementMethod;

    /**
    * 代理服务费（万元）
    */
    private BigDecimal agencyServiceFee;

    /**
    * 与需求对接情况（采购需求时间）
    */
    private String requirementIntegration;

    /**
    * 需求审批情况
    */
    private String requirementApproval;

    /**
    * 履约保证金缴纳情况
    */
    private String performanceBondPayment;

    /**
    * 保证金退还情况
    */
    private String performanceBondRefund;

    /**
    * 是否招标/比选失败转谈判
    */
    private String negotiationFailureToTender;

    /**
     * 公示期异议情况
     */
    private String objectionPublicity;

    /**
     * 结果审批情况
     */
    private String resultApproval;

    /**
    * 备注
    */
    private String remarks;

    private Date createTime;

    private Date updateTime;

    private Long staffId;
}