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
@TableName("fsip_innovation_issues_scope")
@ApiModel(value="FsipInnovationIssuesScope对象", description="")
public class FsipInnovationIssuesScopeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "议题编码")
    @MppMultiId
    @TableField(value = "ISSUES_ID")
    private String issuesId;

    @ApiModelProperty(value = "议题范围类型")
    @MppMultiId
    @TableField(value = "SCOPE_TYPE")
    private String scopeType;

    @ApiModelProperty(value = "议题范围编码")
    @TableField(value = "SCOPE_ID")
    private String scopeId;

    @ApiModelProperty(value = "议题范围名称")
    @TableField(value = "SCOPE_NAME")
    private String scopeName;
}

