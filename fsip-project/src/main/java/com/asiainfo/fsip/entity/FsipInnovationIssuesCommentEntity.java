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
@TableName("fsip_innovation_issues_comment")
@ApiModel(value="FsipInnovationIssuesComment对象", description="")
public class FsipInnovationIssuesCommentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编码")
    @TableId(value = "COMMENT_ID")
    private String commentId;

    @ApiModelProperty(value = "议题编码")
    @TableField(value = "ISSUES_ID")
    private String issuesId;

    @ApiModelProperty(value = "评论人编码")
    @TableField(value = "STAFF_ID")
    private String staffId;

    @ApiModelProperty(value = "评论人名称")
    @TableField(value = "STAFF_NAME")
    private String staffName;

    @ApiModelProperty(value = "评论内容")
    @TableField(value = "CONTENT")
    private String content;

    @ApiModelProperty(value = "评论人时间")
    @TableField(value = "COMMENT_TIME")
    private Date commentTime;
}

