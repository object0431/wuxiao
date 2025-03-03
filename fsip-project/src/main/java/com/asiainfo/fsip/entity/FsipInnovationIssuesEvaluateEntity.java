package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 包括点赞、踩
 * </p>
 *
 * @author author
 * @since 2023-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fsip_innovation_issues_evaluate")
@ApiModel(value="FsipInnovationIssuesEvaluate对象", description="包括点赞、踩")
public class FsipInnovationIssuesEvaluateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编码")
    @TableId(value = "EVALUATE_ID", type = IdType.INPUT)
    private String evaluateId;

    @ApiModelProperty(value = "议题编码")
    @TableField(value = "ISSUES_ID")
    private String issuesId;

    @ApiModelProperty(value = "合伙人编码")
    @TableField(value = "STAFF_ID")
    private String staffId;

    @ApiModelProperty(value = "合伙人名称")
    @TableField(value = "STAFF_NAME")
    private String staffName;

    @ApiModelProperty(value = "评价类型:LIKE=点赞、DISLIKE=踩")
    @TableField(value = "EVALUATE_TYPE")
    private String evaluateType;

    @ApiModelProperty(value = "合伙人部门编码")
    @TableField(value = "COMMENT_TIME")
    private LocalDateTime commentTime;


}
