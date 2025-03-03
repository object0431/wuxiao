package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 项目采购-合同签订情况
 * @author BEJSON
 * @date 2024-04-19
 */
@Data
public class ProjectProcurementContractInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Integer id;

    /**
    * 项目采购ID
    */
    private Integer infoId;

    /**
    * 标段
    */
    private String section;

    /**
    * 份额
    */
    private String share;

    /**
    * 供应商名称
    */
    private String supplierName;

    /**
    * 签订情况
    */
    private String signingStatus;

    /**
    * 中标通知书发出日期
    */
    private Date bidNoticeDate;

    /**
    * 合同生效日期
    */
    private Date contractEffectiveDate;

    /**
    * 履约保证金缴纳日期
    */
    private Date performanceBondPaymentDate;
}