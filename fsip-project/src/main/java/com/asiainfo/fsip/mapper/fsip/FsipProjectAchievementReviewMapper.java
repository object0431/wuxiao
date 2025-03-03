package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipProjectAchievementReviewEntity;
import com.asiainfo.fsip.model.AchievementScoreVo;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface FsipProjectAchievementReviewMapper extends BaseMapper<FsipProjectAchievementReviewEntity> {

    List<StaffInfo> selectPendingCityJudges(String companyId);

    List<StaffInfo> selectPendingProvJudges();

    List<AchievementScoreVo> findAchievementScore(List<String> achievements);
}
