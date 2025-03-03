package com.asiainfo.fsip.model;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "项目成果model")
public class ZgSearchModel {

    @ApiModelProperty(value = "附件列表")
    private List<String> appendixList;

    @ApiModelProperty(value = "项目列表")
    private PageInfo<ProjectAchievementModel> modelPageInfo;

}
