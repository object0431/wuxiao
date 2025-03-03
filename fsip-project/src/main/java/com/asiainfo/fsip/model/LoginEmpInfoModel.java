package com.asiainfo.fsip.model;

import com.asiainfo.mcp.tmc.common.annotation.ExcelField;
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
@ApiModel(description = "登录数统计模型（包括立项数和成果数）")
public class LoginEmpInfoModel {

    @ApiModelProperty("登录用户")
    private String loginAccount;

    @ApiModelProperty("公司编码")
    private String companyId;

    @ApiModelProperty(value = "公司名称", name = "companyName")
    @ExcelField(name = "公司名称",columnWidth = 8000)
    private String companyName;

    @ApiModelProperty("部门编码")
    private String deptId;

    @ApiModelProperty(value = "部门名称", name = "deptName")
    @ExcelField(name = "部门名称",columnWidth = 8000)
    private String deptName;

    @ApiModelProperty("员工名称")
    @ExcelField(name = "员工名称",columnWidth = 4000)
    private String staffName;

    @ApiModelProperty("最近登录时间")
    @ExcelField(name = "最近登录时间",columnWidth = 8000)
    private String loginTime;

}
