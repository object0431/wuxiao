package com.asiainfo.fsip.model;

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
@ApiModel(description = "项目成果库推送查询")
public class ProjectAchievementPushSelReq {

    @ApiModelProperty(value = "成果编码",name = "achievementId")
    private String achievementId;

    @ApiModelProperty(value = "项目名称",name = "projectName")
    private String projectName;

    @ApiModelProperty(value = "项目名称",name = "innovationType")
    private String innovationType;

    @ApiModelProperty(value = "所属项目",name = "projectType")
    private String projectType;

    @ApiModelProperty(value = "成果类型",name = "achievementType")
    private String achievementType;

    @ApiModelProperty(value = "状态",name = "status")
    private String status;

    @ApiModelProperty(value = "级别",name = "awardLevel")
    private String awardLevel;

    @ApiModelProperty(value = "开始日期 yyyy-MM-dd",name = "startDate")
    private String startDate;
    @ApiModelProperty(value = "结束日期 yyyy-MM-dd",name = "endDate")
    private String endDate;

    @ApiModelProperty(value = "地市类型",name = "type")
    private String type;

    @ApiModelProperty(value = "地市转省成果 0:不转，1:转",name = "cityToProvFlag")
    private String cityToProvFlag;

    @ApiModelProperty(value = "地市转省成果申请标识 0:没有申请，1:有申请",name = "cityToProvApplyFlag")
    private String cityToProvApplyFlag;

}
