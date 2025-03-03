package com.asiainfo.fsip.service.impl;

import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FsipApprovalNodeEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementReviewEntity;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementReviewMapper;
import com.asiainfo.fsip.model.ApprovalModel;
import com.asiainfo.fsip.service.FsipProjectAchievementReviewService;
import com.asiainfo.fsip.service.FsipProjectAchievementService;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class FsipProjectAchievementReviewServiceImpl extends ServiceImpl<FsipProjectAchievementReviewMapper, FsipProjectAchievementReviewEntity> implements FsipProjectAchievementReviewService {
    @Resource
    private FsipProjectAchievementService fsipProjectAchievementService;

    @Override
    public void initPs(String targetId, String nodeCode, ApprovalModel approvalModel, FsipApprovalNodeEntity node) {
        FsipProjectAchievementEntity project = fsipProjectAchievementService.getById(targetId);
        if (ObjectUtils.isEmpty(project)) {
            throw new BusinessException("500", "查无数据");
        }
        FsipProjectAchievementReviewEntity reviewEntity = FsipProjectAchievementReviewEntity.builder()
                .achievementId(project.getAchievementId())
                .judgesId(node.getDealStaffId())
                .judgesName(node.getDealStaffName())
                .judgesTime(new Date())
                .score(0f)
                .status(IFsipConstants.Status.ZC)
                .build();
        saveOrUpdate(reviewEntity);
        project.setApprNodeCode(nodeCode);
        fsipProjectAchievementService.updateById(project);
    }
}
