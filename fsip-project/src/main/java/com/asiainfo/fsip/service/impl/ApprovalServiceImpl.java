package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.client.TmcRestClient;
import com.asiainfo.fsip.config.VerifyProperties;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FsipApprovalNodeEntity;
import com.asiainfo.fsip.mapper.fsip.FsipApprovalNodeMapper;
import com.asiainfo.fsip.model.ApprovalModel;
import com.asiainfo.fsip.model.ApprovalRetModel;
import com.asiainfo.fsip.model.UrlModel;
import com.asiainfo.fsip.service.ApprovalDealService;
import com.asiainfo.fsip.service.ApprovalService;
import com.asiainfo.mcp.tmc.common.config.TmcSpringContextUtil;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
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
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApprovalServiceImpl implements ApprovalService {

    @Resource
    private FsipApprovalNodeMapper fsipApprovalNodeMapper;

    @Resource
    private TmcRestClient restClient;

    @Value("${verify.checkApproval:1}")
    private String checkApproval;

    @Value("#{${approval.convert:{'noStaff':'noStaff'}}}")
    private Map<String, String> convertMap;

    @Value("${test.staffId:}")
    private String testStaffId;

    @Resource
    @Qualifier("fastRedisTemplate")
    private RedisTemplate fastRedisTemplate;

    @Resource
    private MessageService messageService;

    @Resource
    private VerifyProperties verifyProperties;

    private boolean checkApprovalStaff(String apprType, String apprId, String nodeCode, String mainUserId) {
        if ("0".equals(checkApproval)) {
            return true;
        }
        //一个环节可以有多个人审批
        List<FsipApprovalNodeEntity> list = fsipApprovalNodeMapper.selectApprovalNodeByNode(apprType, apprId, nodeCode);
        if (ObjectUtils.isEmpty(list)) {
            throw new BusinessException("找不到可审批的环节");
        }
        List<FsipApprovalNodeEntity> fsipApprovalNodeEntity = list.stream().filter(t -> mainUserId.equals(t.getDealStaffId())).collect(Collectors.toList());
        return ObjectUtils.isEmpty(fsipApprovalNodeEntity) ? false : true;
    }

    /**
     * 任务审批
     */
    @Transactional
    @Override
    public void approvalTask(ApprovalRetModel approvalRetModel, StaffInfo staffInfo) {
        ApprovalDealService dealService = getDealService(approvalRetModel.getTargetType());

        Map<String, Object> targetMap = dealService.getTargetInfo(approvalRetModel.getTargetId());
        String nodeCode = MapUtil.getStr(targetMap, "NODE_CODE");
        if (!checkApprovalStaff(approvalRetModel.getTargetType(), approvalRetModel.getTargetId(), nodeCode, staffInfo.getMainUserId())) {
            throw new BusinessException("找不到可审批的环节");
        }

        if (IFsipConstants.APPROVAL_RET_TG.equals(approvalRetModel.getRetType())) {
            dealApprovalTg(dealService, targetMap, approvalRetModel, staffInfo);
        } else {
            dealApprovalBh(dealService, targetMap, approvalRetModel, staffInfo);
        }
    }

    @Override
    public void saveApprovalNode(String apprType, String apprId, List<Map<String, String>> nodeList) {
        if (CollUtil.isEmpty(nodeList)) {
            return;
        }
        fsipApprovalNodeMapper.deleteByApprId(apprType, apprId);

        fsipApprovalNodeMapper.batchInsertNode(apprType, apprId, nodeList);
    }

    @Override
    public FsipApprovalNodeEntity getNextApprovalNode(String approvalType, String extId, String currNode) {
        List<FsipApprovalNodeEntity> approvalNodeList = fsipApprovalNodeMapper.selectApprovalNodeByType(approvalType, extId);

        FsipApprovalNodeEntity currApprovalNode = getApprovalParam(approvalNodeList, currNode);
        return getNextApprovalNode(approvalNodeList, currApprovalNode.getSort());
    }

    @Override
    public String convertPendingStaff(String staffId) {
        if (StringUtils.isEmpty(testStaffId)) {
            return staffId;
        }

        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        if (!testStaffId.contains(staffInfo.getMainUserId())) {
            return staffId;
        }

        String convertId = convertMap.get(staffId);
        return StringUtils.isEmpty(convertId) ? staffId : convertId;
    }

    @Override
    public List<FsipApprovalNodeEntity> getApprovalNodeList(String extId) {
        QueryWrapper<FsipApprovalNodeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("APPR_ID", extId);
        return fsipApprovalNodeMapper.selectList(queryWrapper);
    }

    /**
     * 通过处理
     */
    private void dealApprovalTg(ApprovalDealService dealService, Map<String, Object> targetMap
            , ApprovalRetModel retModel, StaffInfo staffInfo) {
        String nodeCode = MapUtil.getStr(targetMap, "NODE_CODE");

        List<FsipApprovalNodeEntity> approvalNodeList = fsipApprovalNodeMapper.selectApprovalNodeByType(retModel.getTargetType(), retModel.getTargetId());

        FsipApprovalNodeEntity currApprovalNode = getApprovalParam(approvalNodeList, nodeCode);

        targetMap.put("NODE_CODE", currApprovalNode.getNodeCode());

        //更新表状态
        int finish = dealService.modifyApprovalState(retModel.getTargetId(), targetMap, currApprovalNode.getNodeState()
                , retModel.getRemark(), staffInfo, retModel.getExt());

        if(finish > -1) {//正常情况一定会大于-1，只有市级转省级审批未完成时还是-1
            String pendingCode = MapUtil.getStr(targetMap, "PENDING_CODE");

            if ("1".equals(verifyProperties.getUseRest())){
                //调用总部待办更新接口
                updatePendingStatus(pendingCode);
            }

            if ("1".equals(verifyProperties.getUseDingding())){
                //调用钉钉通知状态修改接口
                updateDingNotifyStatus(MapUtil.getLong(targetMap, "DING_TASK_ID"));
            }

            boolean currNodeApprovaled = checkCurrentNodeApprovaled(retModel.getTargetId(), approvalNodeList, currApprovalNode, staffInfo);
            if (!currNodeApprovaled) {
                return;
            }

            FsipApprovalNodeEntity nextApprovalNode = getNextApprovalNode(approvalNodeList, currApprovalNode.getSort());

            //没有下个环节增加个审批完成处理
            if (nextApprovalNode == null) {
                //成果申请 评审是多人评审  由地市专干 统一发送给评委会
                if (IFsipConstants.TaskType.CGSQ.equals(retModel.getTargetType()) && IConstants.NodeCode.BMLDSP.equals(nodeCode)) {
                    //更新表状态
                    dealService.modifyApprovalState(retModel.getTargetId(), targetMap, IFsipConstants.Status.PSWYH
                            , retModel.getRemark(), staffInfo, retModel.getExt());
                } else {
                    dealService.complete(retModel.getTargetId());
                }
            } else {
                String dealStaffId = fsipApprovalNodeMapper.selectByNode(retModel.getTargetType(), retModel.getTargetId(), nextApprovalNode.getNodeCode());
                ApprovalModel approvalModel = addPendingTask(retModel.getTargetId(), dealService.getVerifyUrl()
                        , dealStaffId, staffInfo, nextApprovalNode, targetMap);
                //更新审核环节编码
                dealService.modifyApprovalNode(retModel.getTargetId(), nextApprovalNode.getNodeCode(), approvalModel);
            }
        } else {
            log.info(retModel.getTargetId() + " >> 检测到审批未全部完成，审核任务未完成。");
        }
    }

    private FsipApprovalNodeEntity getApprovalParam(List<FsipApprovalNodeEntity> approvalNodeList, String nodeCode) {
        if (CollUtil.isEmpty(approvalNodeList)) {
            throw new BaseException("未找到对应的审核节点");
        }

        for (FsipApprovalNodeEntity tmcApprovalNode : approvalNodeList) {
            if (tmcApprovalNode.getNodeCode().equals(nodeCode)) {
                return tmcApprovalNode;
            }
        }

        throw new BaseException("未找到对应的审核节点");
    }

    private boolean checkCurrentNodeApprovaled(String targetId, List<FsipApprovalNodeEntity> approvalNodeList
            , FsipApprovalNodeEntity currApprovalNode, StaffInfo staffInfo) {
        List<FsipApprovalNodeEntity> currNodeList = new ArrayList<>();

        String currNodeCode = currApprovalNode.getNodeCode();
        for (FsipApprovalNodeEntity nodeEntity : approvalNodeList) {
            if (currNodeCode.equals(nodeEntity.getNodeCode())) {
                currNodeList.add(nodeEntity);
            }
        }

        if (currNodeList.size() == 1) {
            return true;
        }

        String key = targetId.concat("-").concat(currNodeCode);
        Object approvaled = fastRedisTemplate.opsForValue().get(key);

        List<String> approvaledList;
        if (approvaled == null) {
            approvaledList = new ArrayList<>();
        } else {
            approvaledList = (List<String>) approvaled;
        }

        approvaledList.add(staffInfo.getMainUserId());
        boolean ret = currNodeList.size() == approvaledList.size();

        fastRedisTemplate.opsForValue().set(key, approvaledList, 30, TimeUnit.DAYS);

        return ret;
    }

    private FsipApprovalNodeEntity getNextApprovalNode(List<FsipApprovalNodeEntity> approvalNodeList, int currSort) {
        List<FsipApprovalNodeEntity> nextApprovalNodeList = new ArrayList<>();
        for (int i = 0; i < approvalNodeList.size(); i++) {
            FsipApprovalNodeEntity tmcApprovalNode = approvalNodeList.get(i);
            if (tmcApprovalNode.getSort() > currSort) {
                nextApprovalNodeList.add(tmcApprovalNode);
            }
        }

        if (CollUtil.isEmpty(nextApprovalNodeList)) {
            return null;
        }

        return nextApprovalNodeList.get(0);
    }

    /**
     * 驳回处理
     */
    private void dealApprovalBh(ApprovalDealService dealService
            , Map<String, Object> targetMap, ApprovalRetModel retModel, StaffInfo staffInfo) {
        //更新表状态
        dealService.modifyApprovalState(retModel.getTargetId(), targetMap, IConstants.State.TH, retModel.getRemark()
                , staffInfo, retModel.getExt());

        String pendingCode = MapUtil.getStr(targetMap, "PENDING_CODE");

        if ("1".equals(verifyProperties.getUseRest())){
            //调用总部待办更新接口
            updatePendingStatus(pendingCode);
        }

        if ("1".equals(verifyProperties.getUseDingding())){
            //调用钉钉通知状态修改接口
            updateDingNotifyStatus(MapUtil.getLong(targetMap, "DING_TASK_ID"));

            String targetName = MapUtil.getStr(targetMap, "TARGET_NAME");
            log.info("targetName = " + targetName + ", retModel = " + retModel.getRemark());

            //发送钉钉消息给申请人员
            String applierId = MapUtil.getStr(targetMap, "APPLY_DEAL_ID");
            StringBuilder content = new StringBuilder(targetName).append("被退回：").append(retModel.getRemark());
            sendDingTextMessage(dealService.getVerifyUrl(), content.toString(), applierId);
        }

    }

    private void updatePendingStatus(String pendingCode) {
        PendingUpEntity pendingUpEntity = PendingUpEntity.builder().pendingCode(pendingCode)
                .pendingStatus(IConstants.PendingState.YB).build();
        BaseRsp<Void> baseRsp = restClient.updatePendingStatus(new PendingUpEntity[]{pendingUpEntity});

        if (!RspHelp.SUCCESS_CODE.equals(baseRsp.getRspCode())) {
            log.error("更新待办[{}]失败:{}", pendingCode, baseRsp.getRspDesc());
            throw new BaseException(baseRsp.getRspCode(), baseRsp.getRspDesc());
        }
    }

    private void updateDingNotifyStatus(Long taskId) {
        log.info("begin to updateDingNotifyStatus, taskId = " + taskId);
        if (taskId == null) {
            return;
        }
        try {
            messageService.updateOaMsgState(taskId, "已审核");
        } catch (Exception e) {
            log.error("Could not execute updateDingNotifyStatus", e);
        }
    }

    private ApprovalModel addPendingTask(String targetId, UrlModel urlModel, String nextStaffId
            , StaffInfo staffInfo, FsipApprovalNodeEntity nextApprovalNode, Map<String, Object> targetMap) {
        if (IConstants.NO_PENDING.equalsIgnoreCase(nextApprovalNode.getExtValue())) {
            return ApprovalModel.builder().build();
        }

        String pendingCode = null;
        Long dingTaskId = null;
        if ("1".equals(verifyProperties.getUseRest())){
            String title = MapUtil.getStr(targetMap, "TARGET_NAME");
            String applierId = MapUtil.getStr(targetMap, "APPLIER_ID");
            String applierName = MapUtil.getStr(targetMap, "APPLIER_NAME");
            String applierCompanyId = MapUtil.getStr(targetMap, "APPLIER_COMPANY_ID");

            pendingCode = targetId + System.currentTimeMillis();
            PendingEntity pendingEntity = PendingEntity.builder()
                    .pendingCode(pendingCode).pendingTitle(title)
                    .pendingDate(DateUtils.getDateString()).pendingUserID(convertPendingStaff(nextStaffId))
                    .pendingURL(urlModel.getPcUrl().concat(targetId)).pendingStatus(IConstants.PendingState.DB).pendingLevel(0)
                    .pendingSourceUserID(staffInfo.getMainUserId())
                    .pendingSource(staffInfo.getEmpName())
                    .applierId(applierId).applierName(applierName)
                    .applierCompanyId(applierCompanyId).taskId(nextApprovalNode.getApprId())
                    .taskType(nextApprovalNode.getApprType()).taskStatus(nextApprovalNode.getNodeState()).build();

            BaseRsp<Void> baseRsp = restClient.addPending(new PendingEntity[]{pendingEntity});

            if (!RspHelp.SUCCESS_CODE.equals(baseRsp.getRspCode())) {
                log.error("发送待办[{}]失败:{}", pendingCode, baseRsp.getRspDesc());
                throw new BaseException(baseRsp.getRspCode(), baseRsp.getRspDesc());
            }

            if ("1".equals(verifyProperties.getUseDingding())){
                dingTaskId = sendDingMessage(targetId, urlModel, title, nextStaffId, staffInfo);
            }
        }

        return ApprovalModel.builder().pendingCode(pendingCode).dingTaskId(dingTaskId).build();
    }

    /**
     * 移动端发送钉钉审批消息
     */
    private Long sendDingMessage(String targetId, UrlModel urlModel, String title, String nextStaffId, StaffInfo staffInfo) {
        try {
            if (StringUtils.isEmpty(urlModel.getMobileUrl())) {
                return null;
            }
            log.info("begin to send oa message, urlModel = " + JSONObject.toJSONString(urlModel));
            String pcUrl = urlModel.getPcUrl().concat(targetId);
            String mobileUrl = urlModel.getMobileUrl().concat(targetId);

            Message message = Message.builder().userId(nextStaffId).oaMsg(Message.OaMsg.builder().author(staffInfo.getEmpName()).content(title)
                    .title(urlModel.getTitle()).pcUrl(pcUrl).mobileUrl(mobileUrl).build()).build();
            return messageService.sendOaMsg(message);
        } catch (Exception e) {
            log.error("Could not send oa message , userId = " + nextStaffId, e);
            return null;
        }
    }

    private ApprovalDealService getDealService(String apprType) {
        String beanName = null;
        if (IFsipConstants.TaskType.LXSQ.equals(apprType) || "PLXSQ".equals(apprType)) {
            beanName = "projectApprovalDealService";
        } else if (IFsipConstants.TaskType.CGSQ.equals(apprType)) {
            beanName = "projectAchievementApprovalDealService";
        } else if (IFsipConstants.TaskType.SJCGZSJ.equals(apprType)) {
            beanName = "city2ProvAchievementApprovalService";
        }
        if (StringUtils.isEmpty(beanName)) {
            throw new BaseException("不支持的审批处理类型，apprType = " + apprType);
        }
        return TmcSpringContextUtil.getBean(beanName);
    }


    /**
     * 移动端发送钉钉文本消息
     */
    private Long sendDingTextMessage(UrlModel urlModel, String content, String nextStaffId) {
        try {
            if (StringUtils.isEmpty(urlModel.getMobileUrl())) {
                return null;
            }
            log.info("begin to send text message, urlModel = " + JSONObject.toJSONString(urlModel));

            Message message = Message.builder().userId(nextStaffId).textMsg(Message.TextMsg.builder().content(content).build()).build();
            return messageService.sendTextMsg(message);
        } catch (Exception e) {
            log.error("Could not send text message , userId = " + nextStaffId, e);
            return null;
        }
    }
}
