package com.asiainfo.fsip.model;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InnovationIssuesPartnerApplyResp {

    @ApiModelProperty(value = "返回编码",name = "respCode")
    public String respCode;

    @ApiModelProperty(value = "返回消息",name = "respMsg")
    public String respMsg;

    @ApiModelProperty(value = "议题编码",name = "issuesId")
    private String issuesId;

    @ApiModelProperty(value = "合伙人编码",name = "partnerId")
    private String partnerId;

    @ApiModelProperty(value = "合伙人名称",name = "partnerName")
    private String partnerName;

    @ApiModelProperty(value = "申请人公司编码",name = "companyId")
    private String companyId;

    @ApiModelProperty(value = "申请人部门编码",name = "deptId")
    private String deptId;

    @ApiModelProperty(value = "加入原因",name = "joinReason")
    private String joinReason;

    @ApiModelProperty(value = "申请时间",name = "applyTime")
    private Date applyTime;



}
