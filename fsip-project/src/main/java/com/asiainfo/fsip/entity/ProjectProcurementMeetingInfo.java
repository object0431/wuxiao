package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 项目采购-上会情况
 * @author BEJSON
 * @date 2024-04-18
 */
@Data
public class ProjectProcurementMeetingInfo implements Serializable {

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
    * 会议类型
    */
    private String type;

    /**
    * 会议时间
    */
    private String time;
}