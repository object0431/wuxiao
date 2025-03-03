package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 项目立项信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("FSIP_PROJECT_INITIATION_ITEM")
public class FsipProjectInitiationItemEntity {
    /**
     * 项目编码
     */
    @TableField("PROJECT_ID")
    private String projectId;

    @TableField("ITEM_TYPE")
    private String itemType;

    @TableField("ITEM_CODE")
    private String itemCode;

    @TableField("ITEM_NAME")
    private String itemName;

    @TableField("ITEM_VALUE")
    private String itemValue;

    @TableField("SORT")
    private Integer sort;
}
