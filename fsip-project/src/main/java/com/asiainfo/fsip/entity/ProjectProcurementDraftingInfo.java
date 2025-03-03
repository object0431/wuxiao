package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description project_procurement_drafting_info
 * @author BEJSON
 * @date 2024-04-18
 */
@Data
public class ProjectProcurementDraftingInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
    * 项目采购ID
    */
    private Integer infoId;

    /**
    * 内容
    */
    private String content;

    /**
    * 编制时间
    */
    private String time;
}