package com.asiainfo.fsip.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 项目成果评审请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AchievementPushQryReq {

    @ApiModelProperty(value = "查询类型：0=待推送、1=已推送")
    private String queryType;

    @ApiModelProperty(value = "评分状态:ZC=暂存、00=提交")
    private String status;

    @ApiModelProperty(value = "OA代办编码")
    private String pendingCode;

    @ApiModelProperty(value = "成果类型：PROV=省级、CITY=市级")
    private String achievementType;

    @ApiModelProperty(value = "公司编码")
    private String companyId;

    @ApiModelProperty(value = "部门编码")
    private String deptId;

    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    private String city2Prov;

    private String dealStaffId;

}
