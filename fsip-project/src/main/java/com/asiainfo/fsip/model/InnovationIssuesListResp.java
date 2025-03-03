package com.asiainfo.fsip.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class InnovationIssuesListResp {

    @ApiModelProperty(value = "议题编码")
    private String issuesId;

    @ApiModelProperty(value = "议题标题")
    private String issuesTitle;

    @ApiModelProperty(value = "议题内容")
    private String content;

    @ApiModelProperty(value = "申请人")
    private String applierId;

    @ApiModelProperty(value = "申请人姓名")
    private String applierName;

    @ApiModelProperty(value = "申请人部门公司")
    private String applierCompanyId;

    @ApiModelProperty(value = "申请人部门编码")
    private String applierDeptId;

    @ApiModelProperty(value = "申请时间")
    private Date applyDate;

    @ApiModelProperty(value = "是否要求合伙")
    private String canJoin;

    @ApiModelProperty(value = "合伙人人数")
    private Long partnerNum;

    @ApiModelProperty(value = "关注人数")
    private Long followCount;

    @ApiModelProperty(value = "点赞人数")
    private Long likeCount;

    @ApiModelProperty(value = "踩人数")
    private Long disLikeCount;

    @ApiModelProperty(value = "评论人数")
    private Long commentCount;
}
