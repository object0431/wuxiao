package com.asiainfo.fsip.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 版本管理
 * V1.35
 */
@Data
public class AchievementArchiveReq {

    @ApiModelProperty(value = "项目编号")
    private List<String> projectIds;

    @ApiModelProperty(value = "归档类型 PROV2CITY:省级到市级, CITY2DEPT:市级到部门级")
    private String archiveType;

}
