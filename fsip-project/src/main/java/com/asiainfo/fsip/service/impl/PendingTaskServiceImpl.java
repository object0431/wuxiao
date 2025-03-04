package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.client.TmcRestClient;
import com.asiainfo.fsip.entity.FsipFlowLogEntity;
import com.asiainfo.fsip.mapper.fsip.FsipApprovalNodeMapper;
import com.asiainfo.fsip.model.ApprovalApplyReq;
import com.asiainfo.fsip.model.PendingModel;
import com.asiainfo.fsip.model.UrlModel;
import com.asiainfo.fsip.service.FlowLogService;
import com.asiainfo.fsip.service.PendingTaskService;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.consts.ReturnCode;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingUpEntity;
import com.asiainfo.mcp.tmc.common.exception.BaseException;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.DateUtils;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.dingding.model.Message;
import com.asiainfo.mcp.tmc.dingding.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PendingTaskServiceImpl implements PendingTaskService {

    @Resource
    private FsipApprovalNodeMapper fsipApprovalNodeMapper;

    @Value("${test.staffId:}")
    private String testStaffId;

    @Value("#{${approval.convert:{'noStaff':'noStaff'}}}")
    private Map<String, String> convertMap;

    @Value("${dingding.linkImageUrl:}")
    private String imageUrl;

    @Resource
    private TmcRestClient restClient;

    @Resource
    private FlowLogService flowLogService;

    @Resource
    private MessageService messageService;

    @Override
    public void saveApprovalNode(String apprType, String apprId, List<Map<String, String>> nodeList) {
        if (CollUtil.isEmpty(nodeList)) {
            return;
        }
        fsipApprovalNodeMapper.deleteByApprId(apprType, apprId);
        fsipApprovalNodeMapper.batchInsertNode(apprType, apprId, nodeList);
    }

    @Override
    public String convertPendingStaff(String staffId, StaffInfo staffInfo) {
        if (StringUtils.isEmpty(testStaffId)) {
            return staffId;
        }

        if (!testStaffId.contains(staffInfo.getMainUserId())) {
            return staffId;
        }

        String convertId = convertMap.get(staffId);
        return StringUtils.isEmpty(convertId) ? staffId : convertId;
    }

    @Override
    public ApprovalApplyReq.ApprovalNode getApprovalNode(String operType, ApprovalApplyReq approvalReq) {
        if (!IConstants.OperType.BL.equalsIgnoreCase(operType)) {
            return null;
        }

        if (approvalReq == null || CollUtil.isEmpty(approvalReq.getApprovalNodeList())) {
            throw new BusinessException("9001", "审批人员不能为空！");
        }

        List<ApprovalApplyReq.ApprovalNode> approvalNodeList = approvalReq.getApprovalNodeList();

        ApprovalApplyReq.ApprovalNode firstApprovalNode = approvalNodeList.get(0);
        if (IConstants.NO_PENDING.equalsIgnoreCase(firstApprovalNode.getExtValue())) {
            return null;
        }

        return firstApprovalNode;
    }

    @Override
    public Long applyApproval(PendingModel pendingModel, StaffInfo staffInfo) {
        ApprovalApplyReq approvalReq = pendingModel.getApprovalReq();

        List<ApprovalApplyReq.ApprovalNode> approvalNodeList = approvalReq.getApprovalNodeList();
        //保存办理信息
        List<Map<String, String>> nodeList = new ArrayList<>();
        for (ApprovalApplyReq.ApprovalNode approvalNode : approvalNodeList) {
            Map<String, String> nodeMap = new HashMap<>();
            nodeMap.put("nodeCode", approvalNode.getNodeCode());
            nodeMap.put("dealStaffId", approvalNode.getApproveId());
            nodeMap.put("dealStaffName", approvalNode.getApproveName());
            nodeMap.put("city2Prov", approvalNode.getCity2Prov());
            nodeList.add(nodeMap);
        }

        if (!CollUtil.isEmpty(nodeList)) {
            saveApprovalNode(pendingModel.getTaskType(), pendingModel.getApprovalId(), nodeList);
        }

        //插入轨迹表
        flowLogService.addFlowLog(FsipFlowLogEntity.builder().flowType(pendingModel.getTaskType()).extId(pendingModel.getApprovalId())
                .nodeCode(IConstants.NodeCode.SQRFQ).nodeName(IConstants.NodeCodeName.SQRFQ).nodeState("02")
                .dealStaffId(staffInfo.getMainUserId()).dealStaffName(staffInfo.getEmpName()).remark(approvalReq.getOpinion()).build());

        // 测试环境下面不走，下面全注释掉
        return -1L;

//        String dealStaffId = convertPendingStaff(approvalNodeList.get(0).getApproveId(), staffInfo);
//        String pcUrl = pendingModel.getPendingUrl().concat(pendingModel.getApprovalId());
//
//        //调用待办接口
//        PendingEntity addPendingReq = PendingEntity.builder()
//                .pendingCode(pendingModel.getPendingCode())
//                .pendingTitle(pendingModel.getPendingTitle())
//                .pendingDate(DateUtils.getDateString())
//                .pendingUserID(dealStaffId)
//                .pendingURL(pcUrl)
//                .pendingLevel(0).pendingSourceUserID(staffInfo.getMainUserId())
//                .pendingSource(staffInfo.getEmpName())
//                .applierId(staffInfo.getMainUserId()).applierName(staffInfo.getEmpName())
//                .applierCompanyId(staffInfo.getCompanyId()).taskId(pendingModel.getApprovalId())
//                .taskType(pendingModel.getTaskType()).taskStatus(pendingModel.getTaskStatus()).build();
//        //这里测试环境报错，先注释掉
//        BaseRsp<Void> response = restClient.addPending(new PendingEntity[]{addPendingReq});
//        if (!ReturnCode.SUCCESS.equals(response.getRspCode())) {
//            log.error("调用新增代办失败！".concat(response.getRspDesc()));
//            throw new BusinessException("9001", "调用新增代办失败！".concat(response.getRspDesc()));
//        }
//
//        if(!StringUtils.isEmpty(pendingModel.getMobileUrl())){
//            UrlModel urlModel = UrlModel.builder().pcUrl(pendingModel.getPendingUrl()).mobileUrl(pendingModel.getMobileUrl())
//                    .title(pendingModel.getPendingTitle()).build();
//            String content = StringUtils.isEmpty(pendingModel.getContent()) ? pendingModel.getPendingTitle() : pendingModel.getContent();
//            return this.sendDingOaMessage(pendingModel.getApprovalId(), urlModel, content, dealStaffId, staffInfo);
//        }
//
//        return null;
    }

    @Override
    public void sendPendingTask(PendingModel pendingModel, StaffInfo staffInfo) {
        ApprovalApplyReq approvalReq = pendingModel.getApprovalReq();
        if (approvalReq == null || CollUtil.isEmpty(approvalReq.getApprovalNodeList())) {
            throw new BusinessException("9001", "待办通知人员不能为空！");
        }

        List<ApprovalApplyReq.ApprovalNode> approvalNodeList = approvalReq.getApprovalNodeList();
        String dealStaffId = convertPendingStaff(approvalNodeList.get(0).getApproveId(), staffInfo);
        String pcUrl = pendingModel.getPendingUrl().concat(pendingModel.getApprovalId());

        //调用待办接口
        PendingEntity addPendingReq = PendingEntity.builder()
                .pendingCode(pendingModel.getPendingCode())
                .pendingTitle(pendingModel.getPendingTitle())
                .pendingDate(DateUtils.getDateString())
                .pendingUserID(dealStaffId)
                .pendingURL(pcUrl)
                .pendingLevel(0)
                .pendingSourceUserID(staffInfo.getMainUserId())
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
    public void updatePendingStatus(String pendingCode) {
        if (StringUtils.isEmpty(pendingCode)) {
            return;
        }

        PendingUpEntity pendingUpEntity = PendingUpEntity.builder().pendingCode(pendingCode)
                .pendingStatus(IConstants.PendingState.YB).build();
        BaseRsp<Void> baseRsp = restClient.updatePendingStatus(new PendingUpEntity[]{pendingUpEntity});

        if (!RspHelp.SUCCESS_CODE.equals(baseRsp.getRspCode())) {
            throw new BaseException(baseRsp.getRspCode(), baseRsp.getRspDesc());
        }
    }

    @Override
    public Long sendDingOaMessage(String targetId, UrlModel urlModel, String content, String nextStaffId, StaffInfo staffInfo){
        try {
            if (StringUtils.isEmpty(urlModel.getMobileUrl())) {
                return null;
            }
            log.info("begin to send oa message, urlModel = " + JSONObject.toJSONString(urlModel));
            String mobileUrl = urlModel.getMobileUrl().concat(targetId);

            Message message = Message.builder().userId(nextStaffId).oaMsg(Message.OaMsg.builder().author(staffInfo.getEmpName()).content(content)
                    .title(urlModel.getTitle()).mobileUrl(mobileUrl).build()).build();
            return messageService.sendOaMsg(message);
        } catch (Exception e) {
            log.error("Could not send oa message , userId = " + nextStaffId, e);
            return null;
        }
    }

    @Override
    public Long sendDingLinkMessage(String url, String title, String content, String staffId) {
        try {
            if (StringUtils.isEmpty(url)) {
                return null;
            }
            log.info("begin to send link message, url = " + url);

            Message message = Message.builder().userId(staffId).linkMsg(Message.LinkMsg.builder().title(title)
                    .picUrl("@" + imageUrl).text(content).url(url).build()).build();
            return messageService.sendLinkMsg(message);
        } catch (Exception e) {
            log.error("Could not send link message , userId = " + url, e);
            return null;
        }
    }

    @Override
    public Long sendDingTextMessage(String content, String staffId) {
        try {
            if (StringUtils.isEmpty(content) || StringUtils.isEmpty(staffId)) {
                return null;
            }
            log.info("begin to send text message, url = " + content);

            Message message = Message.builder().userId(staffId).textMsg(Message.TextMsg.builder().content(content).build()).build();
            return messageService.sendTextMsg(message);
        } catch (Exception e) {
            log.error("Could not send text message , userId = " + staffId, e);
            return null;
        }
    }

    @Override
    public void updateDingNotifyStatus(Long taskId, String status) {
        log.info("begin to updateDingNotifyStatus, taskId = " + taskId);
        if (taskId == null) {
            return;
        }
        try {
            messageService.updateOaMsgState(taskId, status);
        } catch (Exception e) {
            log.error("Could not execute updateDingNotifyStatus", e);
        }
    }
}
