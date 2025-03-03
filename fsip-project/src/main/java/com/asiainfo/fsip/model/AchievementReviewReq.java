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
public class AchievementReviewReq {
    @ApiModelProperty(value = "评分状态:ZC=暂存、00=提交")
    private String status;

    @ApiModelProperty(value = "OA代办编码")
    private String pendingCode;

    @ApiModelProperty(value = "附件列表")
    private List<FileModel> appendixList;

    @ApiModelProperty(value = "审批环节")
    private List<ScoreNode> scoreNodeList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ScoreNode {
        @ApiModelProperty(value = "项目成果ID")
        private String projectId;
        @ApiModelProperty(value = "项目评分")
        private Float score;
        @ApiModelProperty(value = "审批环节")
        private List<ItemScore> itemScoreList;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemScore {
        @ApiModelProperty(value = "列表值")
        private String itemCode;
        @ApiModelProperty(value = "列表名称")
        private String itemName;
        @ApiModelProperty(value = "列表评分")
        private Float score;
    }
}
