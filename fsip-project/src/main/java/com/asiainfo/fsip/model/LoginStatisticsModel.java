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
/**
 * 增加 @ExcelField 注解可能会对 LoginStatModel 原有类造成影响
 * 所以新增一个类作为登录数统计数据模型
 */
public class LoginStatisticsModel {

    @ApiModelProperty("统计月份")
    private String statMonth;

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

    @ApiModelProperty(value = "部门总人数")
    private Integer staffTotal;

    @ApiModelProperty("登录数")
    @ExcelField(name = "登录数",columnWidth = 2000)
    private int loginNum;

    @ApiModelProperty("立项数")
    @ExcelField(name = "立项数",columnWidth = 2000)
    private int initiateNum;

    @ApiModelProperty("成果数")
    @ExcelField(name = "成果数",columnWidth = 2000)
    private int achievementNum;

}
