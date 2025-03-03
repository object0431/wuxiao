package com.asiainfo.fsip.service;

import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.model.*;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface FsipProjectAchievementService extends IService<FsipProjectAchievementEntity> {

    PageInfo<ProjectAchievementPushSelResp> selPendingRatingList(PageReq<ProjectAchievementPushSelReq> req, StaffInfo staffInfo)  throws Exception;

    ProjectAchievementPushDetailResp selPendingRatingDetail(String achievementId,String type);

    ProjectAchievementRatingResp rating(ProjectAchievementRatingReq req, StaffInfo staffInfo);

    PageInfo<ProjectAchievementPushSelResp> selRatingList(PageReq<ProjectAchievementPushSelReq> req, StaffInfo staffInfo);

    PageInfo<ProjectAchievementStatisticsResp> queryAchievementStatistics(PageReq<ProjectAchievementStatisticsReq> req, StaffInfo staffInfo);

    NationalProjectAchievementResp nationalProjectAchievementPush(NationalProjectAchievementReq req);

    PageInfo<NationalProjectAchievementSelResp> selNationalProjectAchievementList(PageReq<NationalProjectAchievementSelReq> req);

    NationalProjectAchievementSelResp selNationalProjectAchievementDetail(String achievementId);

    List<ProjectAchievementPushSelResp> selCity2ProvAuditAchievement(City2ProvAuditAchievementReq req);

    NationalProjectAchievementResp delNationalProjectAchievement(String achievementId);
}

