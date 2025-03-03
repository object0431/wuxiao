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
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fsip_issues_partner_apply_log")
@ApiModel(value="FsipIssuesPartnerApplyLog对象", description="")
public class FsipIssuesPartnerApplyLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键编码")
    @TableId(value = "ID")
    private String id;

    @ApiModelProperty(value = "议题编码")
    @TableField(value = "ISSUES_ID")
    private String issuesId;

    @ApiModelProperty(value = "合伙人编码")
    @TableField(value = "PARTNER_ID")
    private String partnerId;

    @ApiModelProperty(value = "合伙人名称")
    @TableField(value = "PARTNER_NAME")
    private String partnerName;

    @ApiModelProperty(value = "申请人公司编码")
    @TableField(value = "COMPANY_ID")
    private String companyId;

    @ApiModelProperty(value = "申请人部门编码")
    @TableField(value = "DEPT_ID")
    private String deptId;

    @ApiModelProperty(value = "加入原因")
    @TableField(value = "JOIN_REASON")
    private String joinReason;

    @ApiModelProperty(value = "申请时间")
    @TableField(value = "APPLY_TIME")
    private Date applyTime;

    @ApiModelProperty(value = "申请状态：01=待处理、02=拒绝、00=同意")
    @TableField(value = "APPLY_STATE")
    private String applyState;

    @ApiModelProperty(value = "申请回复")
    @TableField(value = "REPLY_CONTENT")
    private String replyContent;

    @ApiModelProperty(value = "回复时间")
    @TableField(value = "REPLY_TIME")
    private Date replyTime;
}

