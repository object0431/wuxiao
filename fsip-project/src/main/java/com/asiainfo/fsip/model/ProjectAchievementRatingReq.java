package com.asiainfo.fsip.model;

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
@ApiModel(description = "项目成果等级评定请求参数")
public class ProjectAchievementRatingReq {


    @ApiModelProperty(value = "项目成果等级评定请求List",name = "reqParam")
    public List<ProjectAchievementRatingBean> reqParam;

    @ApiModelProperty(value = "附件列表",name = "attachmentList")
    private List<AttachmentBean> attachmentList;

    @ApiModelProperty(value = "地市级别",name = "type")
    public String type;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "项目成果等级评定Bean实体")
    public static class ProjectAchievementRatingBean{
        @ApiModelProperty(value = "成果编码",name = "achievementId")
        private String achievementId;

        @ApiModelProperty(value = "奖项等级",name = "level")
        private String level;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "附件上传实体Bean")
    public static class AttachmentBean{
        @ApiModelProperty(value = "附件名称",name = "name")
        private String name;
    }
}
