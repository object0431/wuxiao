package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("FSIP_STATIC_PARAM")
public class FsipStaticParamEntity {

    @TableField("ATTR_TYPE")
    private String attrType;

    @TableId("ATTR_CODE")
    private String attrCode;

    @TableField("ATTR_VALUE")
    private String attrValue;

    @TableField("ATTR_DESC")
    private String attrDesc;

    @TableField("EXT_CODE")
    private String extCode;

    @TableField("EXT_VALUE")
    private String extValue;

    @TableField("SORT")
    private int sort;

    @TableField("STATE")
    private String state;

    @TableField("UPDATE_TIME")
    private Timestamp updateTime;

    @TableField("OPERATOR_ID")
    private String operatorId;

}
