package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("fsip_innovation_issues_item")
@ApiModel(value="FsipInnovationIssuesItem对象", description="")
public class FsipInnovationIssuesItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "议题编码")
    @MppMultiId
    @TableField(value = "ISSUES_ID")
    private String issuesId;

    @ApiModelProperty(value = "属性类型")
    @MppMultiId
    @TableField(value = "ATTR_TYPE")
    private String attrType;

    @ApiModelProperty(value = "属性编码")
    @MppMultiId
    @TableField(value = "ATTR_CODE")
    private String attrCode;

    @ApiModelProperty(value = "属性值")
    @TableField(value = "ATTR_VALUE")
    private String attrValue;
}

