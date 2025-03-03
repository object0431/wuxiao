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
@ApiModel("员工查询请求信息")
public class QryEmployeeReq {

    @ApiModelProperty("公司编码")
    private String orgCode;

    @ApiModelProperty("部门编码")
    private String deptCode;

    @ApiModelProperty("员工姓名")
    private String staffName;

    @ApiModelProperty("手机号码")
    private String serialNumber;

    @ApiModelProperty("专业线")
    private String majorLine;
}
