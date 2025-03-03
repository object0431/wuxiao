package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fsip_notice_log")
@ApiModel(value="FsipNoticeLog对象", description="")
public class FsipNoticeLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键编码")
    @TableId(value = "ID")
    private Long id;

    @ApiModelProperty(value = "议题编码")
    @TableField(value = "ISSUES_ID")
    private String issuesId;

    @ApiModelProperty(value = "消息类型:FBTZ=发布通知、SQTZ=申请加入通知、SHTZ=审核通知")
    @TableField(value = "MSG_TYPE")
    private String msgType;

    @ApiModelProperty(value = "员工编码")
    @TableField(value = "STAFF_ID")
    private String staffId;

    @ApiModelProperty(value = "钉钉消息编码")
    @TableField(value = "DING_TASK_ID")
    private Long dingTaskId;

    @ApiModelProperty(value = "OA待办编码")
    @TableField(value = "PENDING_CODE")
    private String pendingCode;

    @ApiModelProperty(value = "OA待办编码")
    @TableField(value = "APPLY_ID")
    private String applyId;
}

