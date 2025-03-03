package com.asiainfo.fsip.service;

import com.asiainfo.fsip.entity.FsipApprovalNodeEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementReviewEntity;
import com.asiainfo.fsip.model.ApprovalModel;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FsipProjectAchievementReviewService extends IService<FsipProjectAchievementReviewEntity> {
    void initPs(String targetId, String nodeCode, ApprovalModel approvalModel, FsipApprovalNodeEntity node);
}
