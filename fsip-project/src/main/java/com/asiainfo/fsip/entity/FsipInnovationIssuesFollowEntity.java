package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 创新议题关注
 * </p>
 *
 * @author author
 * @since 2023-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fsip_innovation_issues_follow")
@ApiModel(value="FsipInnovationIssuesFollow对象", description="创新议题关注")
public class FsipInnovationIssuesFollowEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "议题编码")
    @TableId(value = "ISSUES_ID", type = IdType.INPUT)
    private String issuesId;

    @ApiModelProperty(value = "合伙人编码")
    @TableField(value = "STAFF_ID")
    private String staffId;

    @ApiModelProperty(value = "合伙人名称")
    @TableField(value = "STAFF_NAME")
    private String staffName;


}
