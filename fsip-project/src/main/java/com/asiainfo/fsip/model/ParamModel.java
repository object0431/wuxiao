package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("参数信息")
public class ParamModel {

    @ApiModelProperty("参数编码")
    private String code;

    @ApiModelProperty("参数名称")
    private String name;
}
