package com.asiainfo.fsip.model;


import com.asiainfo.fsip.entity.FsipProjectAchievementBaseEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementItemEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementReviewEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "项目成果库推送详情查询")
public class ProjectAchievementPushDetailResp {

    @ApiModelProperty(value = "项目成果信息",name = "fsipProjectAchievementEntity")
    private FsipProjectAchievementEntity fsipProjectAchievementEntity;

    @ApiModelProperty(value = "项目成果信息",name = "fsipProjectAchievementItemList")
    private List<FsipProjectAchievementItemEntity> fsipProjectAchievementItemList;

    @ApiModelProperty(value = "项目成果信息评定信息",name = "fsipProjectAchievementBaseEntity")
    private FsipProjectAchievementBaseEntity fsipProjectAchievementBaseEntity;

    @ApiModelProperty(value = "项目成果评分信息",name = "fsipProjectAchievementReviewEntity")
    private List<FsipProjectAchievementReviewEntity> fsipProjectAchievementReviewEntity;

    @ApiModelProperty(value = "附件列表",name = "attachmentList")
    private List<String> attachmentList;
}
