package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 项目采购-评审信息子表
 * @author BEJSON
 * @date 2024-04-19
 */
@Data
public class ProjectProcurementReviewSub implements Serializable {

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
    * 评审信息主表ID
    */
    private Integer reviewId;

    /**
    * 评委类型
    */
    private String judgeType;

    /**
    * 评委
    */
    private String judge;
}