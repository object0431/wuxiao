package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("fsip_innovation_issues_partner")
@ApiModel(value="FsipInnovationIssuesPartner对象", description="")
public class FsipInnovationIssuesPartnerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "议题编码")
    @MppMultiId
    @TableField(value = "ISSUES_ID")
    private String issuesId;

    @ApiModelProperty(value = "合伙人编码")
    @MppMultiId
    @TableField(value = "PARTNER_ID")
    private String partnerId;

    @ApiModelProperty(value = "合伙人名称")
    @TableField(value = "PARTNER_NAME")
    private String partnerName;

    @ApiModelProperty(value = "合伙人公司编码")
    @TableField(value = "COMPANY_ID")
    private String companyId;

    @ApiModelProperty(value = "合伙人部门编码")
    @TableField(value = "DEPT_ID")
    private String deptId;
}

