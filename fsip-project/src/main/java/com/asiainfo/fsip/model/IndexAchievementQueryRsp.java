package com.asiainfo.fsip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexAchievementQueryRsp {

    private int nationalLevelCount;
    private int provincialLevelCount;
    private int cityLevelCount;

    private List<RspData> nationalLevel;
    private List<RspData> provincialLevel;
    private List<RspData> cityLevel;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RspData{
        private int length;
        private String achievementDate;
        private List<AchievementData> achievementList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AchievementData{
        private String achievementId;
        private String achievementName;
        private String achievementContent;
        private String projectName;
        private String applyName;
        private String applyCompanyName;
        private String backImage;
    }
}
