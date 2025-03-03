package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 项目采购-项目报名时间
 * @author BEJSON
 * @date 2024-04-19
 */
@Data
public class ProjectProcurementEnrollmentTime implements Serializable {

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
    * 开始时间
    */
    private String startTime;

    /**
    * 结束时间
    */
    private String endTime;
}