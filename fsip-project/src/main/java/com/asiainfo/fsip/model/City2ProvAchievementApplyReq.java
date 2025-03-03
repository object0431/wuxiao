package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "市级成果转省级成果申请")
public class City2ProvAchievementApplyReq {

    @ApiModelProperty(value = "地市成果编码列表")
    private List<String> achievementIdList;

    @ApiModelProperty(value = "审批信息")
    private ApprovalApplyReq approvalReq;
}
