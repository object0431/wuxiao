package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @description fsip_staff_2_role_extend
 * @author BEJSON
 * @date 2024-04-30
 */
@Data
@TableName("fsip_staff_2_role_extend")
public class FsipStaff2RoleExtend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
    * id
    */
    private Integer id;

    /**
    * 角色编码
    */
    private String roleId;

    /**
    * 工号
    */
    private String staffId;

    /**
    * 姓名
    */
    private String staffName;

    /**
    * 参数类型
    */
    private String attrType;

    /**
    * 属性编码
    */
    private String attrCode;

    /**
    * 属性值
    */
    private String attrValue;

    /**
    * 属性描述
    */
    private String attrDesc;
}