package com.asiainfo.fsip.service;

import com.asiainfo.fsip.entity.FsipApprovalNodeEntity;
import com.asiainfo.fsip.model.ApprovalRetModel;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

import java.util.List;
import java.util.Map;

public interface ApprovalService {

    /**
     * 审批任务
     */
    void approvalTask(ApprovalRetModel approvalRetModel, StaffInfo staffInfo);

    /**
     * 保存审批信息
     */
    void saveApprovalNode(String apprType, String apprId, List<Map<String, String>> nodeList);

    /**
     * 获取下一个审批环节
     */
    FsipApprovalNodeEntity getNextApprovalNode(String approvalType, String extId, String currNode);

    /**
     * 待办工号转换(测试用)
     */
    String convertPendingStaff(String staffId);

    List<FsipApprovalNodeEntity> getApprovalNodeList(String extId);
}
