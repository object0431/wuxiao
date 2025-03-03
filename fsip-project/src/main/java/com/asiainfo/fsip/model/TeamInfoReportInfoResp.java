package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamInfoReportInfoResp {

    @ApiModelProperty(value = "公司名称、部门编码")
    private String companyId;

    @ApiModelProperty(value = "战队名称")
    private String teamName;

    @ApiModelProperty(value = "立项数")
    private String achievementCount;

    @ApiModelProperty(value = "成果总数")
    private String achievementBaseCount;

    @ApiModelProperty(value = "闪耀值")
    private String shineValue;

}
