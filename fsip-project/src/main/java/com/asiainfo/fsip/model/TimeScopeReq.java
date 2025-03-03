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
@ApiModel(description = "时间范围查询请求体")
public class TimeScopeReq {

    @ApiModelProperty("开始时间")
    String startDate;

    @ApiModelProperty("截止时间")
    String endDate;

    @ApiModelProperty("公司编号")
    String companyId;

    @ApiModelProperty("部门编号")
    String deptId;

    @ApiModelProperty("员工名称")
    String staffName;

    /**
     * 在查询和导出 项目分类相关接口时使用（XMLX、CXLX）
     */
    @ApiModelProperty("分类类型（可为空）")
    String type;
}
