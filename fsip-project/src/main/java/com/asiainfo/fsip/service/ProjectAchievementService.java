package com.asiainfo.fsip.service;

import com.asiainfo.fsip.entity.AchievementArchiveReq;
import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.model.*;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface ProjectAchievementService {
    FsipProjectAchievementEntity saveProject(ProjectAchievementModel projectModel, String operateType, StaffInfo staffInfo);

    BaseRsp<Object> handleProject(ProjectAchievementAddReq req, StaffInfo staffInfo);

    NextApprovalNodeModel getNextApprovalInfo(ProjectAchievementModel projectModel);

    ProjectAchievementModel getProject(String projectId);

    PageInfo<ProjectAchievementModel> searchProject(PageReq<ProjectAchievementSearchModel> pageReq, StaffInfo staffInfo);

    Map<String, List<String>> delProjects(String[] projectIds);

    String confirmProject(ProjectAchievementModel projectModel);

    void expertAdvice(String projectId, String suggestion, StaffInfo staffInfo);

    void expertAdviceScore(String expertAdviceId, float score, StaffInfo staffInfo);

    void achievementReview(AchievementReviewReq req, StaffInfo staffInfo);

    void recallProject(String projectId);

    PageInfo<ProjectAchievementModel> zgSearchProject(PageReq<Map<String, String>> pageReq, StaffInfo staffInfo);

    PageInfo<ProjectAchievementModel> queryReviewList(PageReq<AchievementPushQryReq> pageReq, StaffInfo staffInfo);

    void zgPush(ZgPushReq req, StaffInfo staffInfo);

    /**
     * 市级成果转省级成果
     *
     * @param req --申请参数
     * @param staffInfo --操作员信息
     */
    void city2ProvApply(City2ProvAchievementApplyReq req, StaffInfo staffInfo);

    /**
     * 查询项目成果数据列表信息
     */
    List<ProjectAchievementModel> searchProject(ProjectAchievementSearchModel req);

    /**
     * 查询未完成评分的评审人员列表
     */
    List<StaffInfo> queryReviewJudgeList(String queryType, StaffInfo staffInfo);

    /**
     * 成果评分催办
     */
    void urgingReview(ReviewUrgingReq req);

    void achievementArchive(AchievementArchiveReq req, StaffInfo staffInfo);
}
