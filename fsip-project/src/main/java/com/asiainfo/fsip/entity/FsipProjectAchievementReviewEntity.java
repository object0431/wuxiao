package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 项目成果评审信息
 * </p>
 *
 * @author author
 * @since 2023-08-08
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fsip_project_achievement_review")
@ApiModel(value="FsipProjectAchievementReview对象", description="项目成果评审信息")
public class FsipProjectAchievementReviewEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId("ID")
    private Long id;

    @ApiModelProperty(value = "成果编码")
    @TableField(value = "ACHIEVEMENT_ID")
    private String achievementId;

    @ApiModelProperty(value = "评审人")
    @TableField(value = "JUDGES_ID")
    private String judgesId;

    @ApiModelProperty(value = "评审人名称")
    @TableField(value = "JUDGES_NAME")
    private String judgesName;

    @ApiModelProperty(value = "评分")
    @TableField(value = "SCORE")
    private Float score;

    @ApiModelProperty(value = "地市成果转省级成果")
    @TableField(value = "CITY_TO_PROV_FLAG")
    private String cityToProvFlag;

    @ApiModelProperty(value = "评分状态:ZC=暂存、00=提交")
    @TableField(value = "STATUS")
    private String status;

    @ApiModelProperty(value = "评审时间")
    @TableField(value = "JUDGES_TIME")
    private Date judgesTime;

    @ApiModelProperty(value = "细项编码")
    @TableField(value = "ITEM_CODE")
    private String itemCode;

    @ApiModelProperty(value = "细项名称")
    @TableField(value = "ITEM_NAME")
    private String itemName;
}
