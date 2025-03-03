package com.asiainfo.fsip.model;

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
@ApiModel("静态参数信息")
public class StaticParamModel {
    @ApiModelProperty(value = "参数类型")
    private String attrType;

    @ApiModelProperty(value = "参数编码")
    private String attrCode;

    @ApiModelProperty(value = "参数值")
    private String attrValue;

    @ApiModelProperty(value = "参数描述")
    private String attrDesc;

    @ApiModelProperty(value = "扩展编码")
    private String extCode;

    @ApiModelProperty(value = "扩展值")
    private String extValue;

    @ApiModelProperty(value = "排序")
    private int sort;
}
