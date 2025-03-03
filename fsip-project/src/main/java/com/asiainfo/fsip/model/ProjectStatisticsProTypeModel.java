package com.asiainfo.fsip.model;

import com.asiainfo.mcp.tmc.common.annotation.ExcelField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: fsip-backend
 * @author: cnda
 * @create: 2024-01-02 14:39
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "项目分类统计数据模型类")
public class ProjectStatisticsProTypeModel {

    @ApiModelProperty(value = "公司编号", name = "companyId")
    private String companyId;

    @ApiModelProperty(value = "公司名称", name = "companyName")
    @ExcelField(name = "公司名称",columnWidth = 8000)
    private String companyName;

    @ApiModelProperty(value = "部门编号", name = "deptId")
    private String deptId;

    @ApiModelProperty(value = "部门名称", name = "deptName")
    @ExcelField(name = "部门名称",columnWidth = 8000)
    private String deptName;

    @ApiModelProperty(value = "项目分类", name = "statType")
    @ExcelField(name = "项目分类",columnWidth = 4000)
    private String statType;

    @ApiModelProperty(value = "立项数量", name = "initNum")
    @ExcelField(name = "立项数",columnWidth = 2000)
    private Integer initNum;

    @ApiModelProperty(value = "成果数量", name = "acheTotal")
    @ExcelField(name = "成果数",columnWidth = 2000)
    private Integer acheTotal;


}
