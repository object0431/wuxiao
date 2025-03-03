package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("fsip_innovation_issues")
@ApiModel(value="FsipInnovationIssues对象", description="")
public class FsipInnovationIssuesEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "议题编码")
    @TableId(value = "ISSUES_ID", type = IdType.INPUT)
    private String issuesId;

    @ApiModelProperty(value = "议题标题")
    @TableField(value = "ISSUES_TITLE")
    private String issuesTitle;

    @ApiModelProperty(value = "议题内容")
    @TableField(value = "CONTENT")
    private String content;

    @ApiModelProperty(value = "申请人")
    @TableField(value = "APPLIER_ID")
    private String applierId;

    @ApiModelProperty(value = "申请人姓名")
    @TableField(value = "APPLIER_NAME")
    private String applierName;

    @ApiModelProperty(value = "申请人部门公司")
    @TableField(value = "APPLIER_COMPANY_ID")
    private String applierCompanyId;

    @ApiModelProperty(value = "申请人部门编码")
    @TableField(value = "APPLIER_DEPT_ID")
    private String applierDeptId;

    @ApiModelProperty(value = "申请时间")
    @TableField(value = "APPLY_DATE")
    private Date applyDate;

    @ApiModelProperty(value = "是否要求合伙")
    @TableField(value = "CAN_JOIN")
    private String canJoin;

    @ApiModelProperty(value = "合伙人人数")
    @TableField(value = "PARTNER_NUM")
    private Long partnerNum;

}

