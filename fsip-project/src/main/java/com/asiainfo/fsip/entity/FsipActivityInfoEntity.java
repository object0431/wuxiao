package com.asiainfo.fsip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("FSIP_ACTIVITY_INFO")
@ApiModel(value="FsipActivityInfo对象", description="活动信息")
public class FsipActivityInfoEntity {

    @ApiModelProperty(value = "编码")
    @TableId("ID")
    private String id;

    @ApiModelProperty(value = "类型:HD=互动、XW=新闻")
    @TableField("TYPE")
    private String type;

    @ApiModelProperty(value = "标题")
    @TableField("TITLE")
    private String title;

    @ApiModelProperty(value = "活动内容")
    @TableField("CONTENT")
    private String content;

    @ApiModelProperty(value = "活动时间")
    @TableField("ACTIVITY_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date activityTime;

    @ApiModelProperty(value = "操作员编码")
    @TableField("STAFF_ID")
    private String staffId;

    @ApiModelProperty(value = "操作员名称")
    @TableField("STAFF_NAME")
    private String staffName;

    @ApiModelProperty(value = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty("截取后内容")
    @TableField(exist = false)
    private String introduction;

}
