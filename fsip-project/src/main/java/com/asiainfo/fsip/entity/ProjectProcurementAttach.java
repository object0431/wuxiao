package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 项目采购-附件信息
 * @author BEJSON
 * @date 2024-04-19
 */
@Data
public class ProjectProcurementAttach implements Serializable {

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
     * 其他信息ID
     */
    private Integer otherId;

    /**
     * 附件所属类型
     */
    private String type;

    /**
    * 附件名
    */
    private String name;

    /**
    * 附件地址
    */
    private String path;
}