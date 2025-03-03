package com.asiainfo.mcp.tmc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("TMC_TODO_TASK")
public class TmcTodoTaskEntity {

    /**
     * 待办信息的编号
     */
    @TableId("PENDING_CODE")
    private String pendingCode;

    /**
     * 待办信息的编号
     */
    @TableField("PENDING_TITLE")
    private String pendingTitle;

    /**
     * 工单类型
     */
    @TableField("TASK_TYPE")
    private String taskType;

    /**
     * 工单编码
     */
    @TableField("TASK_ID")
    private String taskId;

    /**
     * 待办人,统一邮箱前缀
     */
    @TableField("PENDING_USER_ID")
    private String pendingUserId;

    /**
     * 待办状态:‘0’待办；‘1’已办；‘d’删除
     */
    @TableField("PENDING_STATUS")
    private String pendingStatus;

    /**
     * 待办信息 URL
     */
    @TableField("PENDING_URL")
    private String pendingUrl;

    /**
     * 任务状态:JG=办结
     */
    @TableField("TASK_STATUS")
    private String taskStatus;

    /**
     * 发起人编码
     */
    @TableField("APPLIER_ID")
    private String applierId;

    /**
     * 发起人名称
     */
    @TableField("APPLIER_NAME")
    private String applierName;

    /**
     * 发起人所属公司
     */
    @TableField("APPLIER_COMPANY_ID")
    private String applierCompanyId;

    /**
     * 写入日志表时间
     */
    @TableField("CREATE_TIME")
    private Date createTime;
}