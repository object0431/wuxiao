package com.asiainfo.fsip.model;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "新闻查询请求参数")
public class FsipActivityInfoReq {

    @ApiModelProperty(value = "类型:HD=互动、XW=新闻")
    private String type;

    @ApiModelProperty(value = "标题")
    private String title;
}
