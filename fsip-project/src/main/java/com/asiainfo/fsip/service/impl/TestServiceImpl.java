package com.asiainfo.fsip.service.impl;

import com.asiainfo.fsip.client.TmcRestClient;
import com.asiainfo.fsip.model.PendingModel;
import com.asiainfo.fsip.service.TestService;
import com.asiainfo.mcp.tmc.common.consts.ReturnCode;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.TitleInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class TestServiceImpl implements TestService {

    @Resource
    private TmcRestClient restClient;

    @Override
    public void sendPendingTask(PendingModel pendingModel, StaffInfo staffInfo) {
        //调用待办接口
        PendingEntity addPendingReq = PendingEntity.builder()
                .pendingCode(pendingModel.getPendingCode())
                .pendingTitle(pendingModel.getPendingTitle())
                .pendingDate(DateUtils.getDateString())
                .pendingUserID(pendingModel.getApprovalReq().getApprovalNodeList().get(0).getApproveId())
                .pendingURL(pendingModel.getPendingUrl())
                .pendingLevel(0).pendingSourceUserID(staffInfo.getMainUserId())
                .pendingSource(staffInfo.getEmpName())
                .applierId(staffInfo.getMainUserId()).applierName(staffInfo.getEmpName())
                .applierCompanyId(staffInfo.getCompanyId()).taskId(pendingModel.getApprovalId())
                .taskType(pendingModel.getTaskType()).taskStatus(pendingModel.getTaskStatus()).build();

        BaseRsp<Void> response = restClient.addPending(new PendingEntity[]{addPendingReq});
        if (!ReturnCode.SUCCESS.equals(response.getRspCode())) {
            throw new BusinessException("9001", "调用新增代办失败！".concat(response.getRspDesc()));
        }
    }

    @Override
    public String getTitle(String type, TitleInfo titleInfo) {
        return restClient.getTitle("TEST", TitleInfo.builder().build());
    }
}
