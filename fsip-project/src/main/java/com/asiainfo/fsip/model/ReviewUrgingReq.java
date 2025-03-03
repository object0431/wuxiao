package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 项目成果评分催办
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewUrgingReq {

    @ApiModelProperty(value = "待通知员工编码列表")
    private List<String> staffIdList;

    @ApiModelProperty(value = "提醒内容")
    private String content;

}
