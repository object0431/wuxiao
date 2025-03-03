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
@ApiModel(description = "项目成果等级评定返回参数")
public class ProjectAchievementRatingResp {

    @ApiModelProperty(value = "返回code",name = "respCode")
    public String respCode;

    @ApiModelProperty(value = "返回消息",name = "respMsg")
    public String respMsg;

    @ApiModelProperty(value = "项目成果等级评定返回List",name = "reqData")
    public List<ProjectAchievementRatingRespBean> reqData;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "项目成果等级评定Bean实体返回")
    public static class ProjectAchievementRatingRespBean{
        @ApiModelProperty(value = "成果编码",name = "achievementId")
        private String achievementId;

        @ApiModelProperty(value = "错误情况",name = "errorMsg")
        private String errorMsg;
    }
}
