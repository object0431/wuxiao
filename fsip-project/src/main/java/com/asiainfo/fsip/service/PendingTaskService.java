package com.asiainfo.fsip.service;

import com.asiainfo.fsip.model.ApprovalApplyReq;
import com.asiainfo.fsip.model.PendingModel;
import com.asiainfo.fsip.model.UrlModel;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

import java.util.List;
import java.util.Map;

public interface PendingTaskService {

    void saveApprovalNode(String apprType, String apprId, List<Map<String, String>> nodeList);

    /**
     * 待办工号转换(测试用)
     */
    String convertPendingStaff(String staffId, StaffInfo staffInfo);

    /**
     * 待办处理
     */
    ApprovalApplyReq.ApprovalNode getApprovalNode(String operType, ApprovalApplyReq approvalReq);

    /**
     * 待办处理
     */
    Long applyApproval(PendingModel pendingModel, StaffInfo staffInfo);

    /**
     * 新增代办通知
     */
    void sendPendingTask(PendingModel pendingModel, StaffInfo staffInfo);

    /**
     * 更新待办
     */
    void updatePendingStatus(String pendingCode);

    /**
     * 发送钉钉OA消息
     */
    Long sendDingOaMessage(String targetId, UrlModel urlModel, String content, String nextStaffId, StaffInfo staffInfo);

    /**
     * 发送钉钉链接消息
     */
    Long sendDingLinkMessage(String url, String title, String content, String staffId);

    /**
     * 发送钉钉待办
     */
    Long sendDingTextMessage(String content, String staffId);

    /**
     * 更新钉钉待办状态
     */
    void updateDingNotifyStatus(Long taskId, String status);
}
