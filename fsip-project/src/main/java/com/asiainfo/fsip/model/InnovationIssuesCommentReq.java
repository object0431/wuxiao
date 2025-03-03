package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "议题评论信息请求")
public class InnovationIssuesCommentReq {

    @ApiModelProperty(value = "议题编码",name = "issuesId")
    private String issuesId;

    @ApiModelProperty(value = "评论",name = "comment")
    private String comment;

    @ApiModelProperty(value = "删除评论的编码",name = "delCommentId")
    private String delCommentId;

    @ApiModelProperty(value = "点赞",name = "like")
    private String like;

    @ApiModelProperty(value = "取消点赞",name = "cancelLike")
    private String cancelLike;

    @ApiModelProperty(value = "踩",name = "disLike")
    private String disLike;

    @ApiModelProperty(value = "取消踩",name = "like")
    private String cancelDisLike;

    @ApiModelProperty(value = "关注",name = "follow")
    private String follow;

    @ApiModelProperty(value = "取消关注",name = "cancelFollow")
    private String cancelFollow;
}
