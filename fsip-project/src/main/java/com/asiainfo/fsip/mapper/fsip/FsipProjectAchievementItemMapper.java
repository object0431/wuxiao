package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.entity.FsipProjectAchievementItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FsipProjectAchievementItemMapper extends BaseMapper<FsipProjectAchievementItemEntity> {

    List<FsipProjectAchievementItemEntity> selectByAchievementAndJudgeId(@Param("achievementId") String achievementId
            , @Param("judgeId")  String judgeId, @Param("cityToProvFlag")  String cityToProvFlag);
}
