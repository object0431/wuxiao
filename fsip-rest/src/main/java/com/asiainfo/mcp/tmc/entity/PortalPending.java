package com.asiainfo.mcp.tmc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("TMC_PORTAL_PENDING")
public class PortalPending implements Serializable {
    /**
     * 待办信息的编号
     */
    @TableId("pendingCode")
    private String pendingCode;

    /**
     * 待办标题
     */
    @TableField("pendingTitle")
    private String pendingTitle;

    /**
     * 待办产生时间格式: yyyyMMddHHmmss
     */
    @TableField("pendingDate")
    private String pendingDate;

    /**
     * 待办人,统一邮箱前缀
     */
    @TableField("pendingUserID")
    private String pendingUserID;

    /**
     * 待办信息URL
     */
    @TableField("pendingURL")
    private String pendingURL;

    /**
     * 待办状态:‘0’待办；‘1’已办；‘d’删除
     */
    @TableField("pendingStatus")
    private String pendingStatus;

    /**
     * 待办等级:1.普通，2.重要，3 加急,默认为 0，级别最低
     */
    @TableField("pendingLevel")
    private Integer pendingLevel;

    /**
     * 待办省分代码
     */
    @TableField("pendingCityCode")
    private String pendingCityCode;

    /**
     * 待办上一步处理人
     */
    @TableField("pendingSourceUserID")
    private String pendingSourceUserID;

    /**
     * 待办上一步处理人
     */
    @TableField("pendingSource")
    private String pendingSource;

    /**
     * 操作类型:‘a’:新增待办;‘u’:更新待办;’t’:’更新待办标题’:’d’:删除待办
     */
    @TableField("operatorType")
    private String operatorType;

    /**
     * 写入日志表时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}