package com.asiainfo.fsip.model;

import com.asiainfo.mcp.tmc.common.annotation.ExcelField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "项目成果统计返回参数")
public class ProjectAchievementStatisticsResp {

    @ApiModelProperty(value = "申请人姓名",name = "applierName")
    @ExcelField(name = "申请人", sort = 1)
    private String applierName;
    @ApiModelProperty(value = "申请人公司名称",name = "applierCompanyId")
    @ExcelField(name = "公司", sort = 2)
    private String applierCompanyId;

    @ApiModelProperty(value = "申请部门",name = "applierDeptId")
    @ExcelField(name = "部门", sort = 3)
    private String applierDeptId;

    @ApiModelProperty(value = "申请人",name = "applierId")
    private String applierId;


    @ApiModelProperty(value = "已申报的项目立项数量",name = "applyProjectTotal")
    @ExcelField(name = "已申报的项目立项数量", sort = 4)
    private String applyProjectTotal;

    @ApiModelProperty(value = "已申报的项目成果数量",name = "auditProjectTotal")
    @ExcelField(name = "已申报的项目成果数量", sort = 5)
    private String auditProjectTotal;

    @ApiModelProperty(value = "平均评分",name = "avgScore")
    @ExcelField(name = "平均评分", sort = 6)
    private String avgScore;

}
