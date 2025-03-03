package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 项目成果属性信息
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
@TableName("fsip_project_achievement_item")
@ApiModel(value="FsipProjectAchievementItem对象", description="项目成果属性信息")
public class FsipProjectAchievementItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "成果编码")
    @MppMultiId(value = "ACHIEVEMENT_ID")
    private String achievementId;

    @ApiModelProperty(value = "属性类型")
    @MppMultiId(value = "ITEM_TYPE")
    private String itemType;

    @ApiModelProperty(value = "属性编码")
    @MppMultiId(value = "ITEM_CODE")
    private String itemCode;

    @ApiModelProperty(value = "属性名称")
    @TableField(value = "ITEM_NAME")
    private String itemName;

    @ApiModelProperty(value = "属性值")
    @TableField(value = "ITEM_VALUE")
    private String itemValue;

    @ApiModelProperty(value = "排序")
    @TableField(value = "SORT")
    private Integer sort;

    @TableField(exist = false)
    private String score;

    /**
     * 评分状态
     */
    @TableField(exist = false)
    private String scoreStatus;
}
