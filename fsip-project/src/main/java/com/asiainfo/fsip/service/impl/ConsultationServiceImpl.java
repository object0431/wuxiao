package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aliyun.oss.ServiceException;
import com.asiainfo.fsip.client.TmcRestClient;
import com.asiainfo.fsip.config.VerifyProperties;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FsipConsultOrderEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.entity.FsipProjectInitiationEntity;
import com.asiainfo.fsip.mapper.fsip.FsipConsultOrderMapper;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementMapper;
import com.asiainfo.fsip.mapper.fsip.FsipProjectInitiationMapper;
import com.asiainfo.fsip.model.ConsultationInitiateReq;
import com.asiainfo.fsip.model.ConsultationReplyReq;
import com.asiainfo.fsip.model.FsipConsultOrderRsp;
import com.asiainfo.fsip.model.UrlModel;
import com.asiainfo.fsip.service.ConsultationService;
import com.asiainfo.fsip.service.PendingTaskService;
import com.asiainfo.mcp.tmc.common.consts.ReturnCode;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.DateUtils;
import com.asiainfo.mcp.tmc.common.util.TranceNoTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConsultationServiceImpl implements ConsultationService {

    @Resource
    private FsipConsultOrderMapper fsipConsultOrderMapper;

    @Resource
    private FsipProjectAchievementMapper fsipProjectAchievementMapper;

    @Resource
    private FsipProjectInitiationMapper fsipProjectInitiationMapper;

    @Resource
    private TranceNoTool tranceNoTool;

    @Resource
    private TmcRestClient restClient;

    @Resource
    private VerifyProperties verifyProperties;

    @Resource
    private PendingTaskService pendingTaskService;

    @Override
    public void initiate(ConsultationInitiateReq req, StaffInfo staffInfo) {
        List<ConsultationInitiateReq.Expert> expertList = req.getExpertList();
        if (CollUtil.isEmpty(expertList)) {
            throw new ServiceException("咨询对象不能为空");
        }

        StringBuilder titleBuilder = new StringBuilder("项目咨询-");
        if (IFsipConstants.TaskType.LXSQ.equals(req.getTargetType())) {
            FsipProjectInitiationEntity entity = fsipProjectInitiationMapper.selectById(req.getTargetId());
            if (entity != null) {
                titleBuilder.append(entity.getProjectName());
            }
        } else {
            FsipProjectAchievementEntity entity = fsipProjectAchievementMapper.selectById(req.getTargetId());
            if (entity != null) {
                titleBuilder.append(entity.getProjectName());
            }
        }

        titleBuilder.append(staffInfo.getEmpName());

        List<PendingEntity> pendingEntityList = new ArrayList<>();


        List<FsipConsultOrderEntity> orderEntityList = expertList.parallelStream().map(item -> {
            String orderId = tranceNoTool.getTimeId("ZX");

            String pcUrl = verifyProperties.getConsultPcUrl().concat(orderId);
            pendingEntityList.add(PendingEntity.builder()
                    .pendingCode(orderId)
                    .pendingTitle(titleBuilder.toString())
                    .pendingDate(DateUtils.getDateString())
                    .pendingUserID(item.getStaffId())
                    .pendingURL(pcUrl)
                    .pendingLevel(0)
                    .pendingSourceUserID(staffInfo.getMainUserId())
                    .pendingSource(staffInfo.getEmpName())
                    .applierId(staffInfo.getMainUserId()).applierName(staffInfo.getEmpName())
                    .applierCompanyId(staffInfo.getCompanyId()).taskId(orderId)
                    .taskType(IFsipConstants.TaskType.ZXGD).taskStatus("01").build());

            return FsipConsultOrderEntity.builder().orderId(orderId).targetType(req.getTargetType()).targetId(req.getTargetId())
                    .status("01").request(req.getQuestions()).expertId(item.getStaffId())
                    .expertName(item.getStaffName()).build();
        }).collect(Collectors.toList());

        fsipConsultOrderMapper.batchInsert(orderEntityList);

        //调用待办接口
        BaseRsp<Void> response = restClient.addPending(pendingEntityList.toArray(new PendingEntity[pendingEntityList.size()]));
        if (!ReturnCode.SUCCESS.equals(response.getRspCode())) {
            throw new BusinessException("9001", "调用新增代办失败！".concat(response.getRspDesc()));
        }

        if (IFsipConstants.TaskType.LXSQ.equals(req.getTargetType())) {
            CompletableFuture.supplyAsync(() -> {
                orderEntityList.stream().forEach(item -> {
                    UrlModel urlModel = UrlModel.builder().mobileUrl(verifyProperties.getConsultMobileUrl())
                            .title(titleBuilder.toString()).build();

                    Long dingTaskId = pendingTaskService.sendDingOaMessage(item.getOrderId(), urlModel, req.getQuestions()
                            , item.getExpertId(), staffInfo);
                    item.setDingTaskId(dingTaskId);
                    fsipConsultOrderMapper.updateById(item);
                });
                return true;
            });
        }
    }

    @Transactional
    @Override
    public void reply(ConsultationReplyReq req, StaffInfo staffInfo) {
        FsipConsultOrderEntity orderEntity = fsipConsultOrderMapper.selectById(req.getOrderId());

        orderEntity.setResponse(req.getReplyContent());
        orderEntity.setStatus("00");
        orderEntity.setRespTime(new Date());

        fsipConsultOrderMapper.updateById(orderEntity);

        pendingTaskService.updatePendingStatus(req.getOrderId());
        pendingTaskService.updateDingNotifyStatus(orderEntity.getDingTaskId(), "已回复");
    }

    @Override
    public void grade(String orderId, String score) {
        FsipConsultOrderEntity orderEntity = FsipConsultOrderEntity.builder().orderId(orderId).score(score).build();

        fsipConsultOrderMapper.updateById(orderEntity);
    }

    @Override
    public List<FsipConsultOrderEntity> queryByTargetId(String targetId) {
        List<FsipConsultOrderEntity> consultOrderList = fsipConsultOrderMapper.selectByTargetId(targetId);
        if (CollUtil.isEmpty(consultOrderList)) {
            return Collections.emptyList();
        }

        return consultOrderList;
    }

    @Override
    public FsipConsultOrderEntity queryByOrderId(String orderId) {
        FsipConsultOrderEntity order = fsipConsultOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("未找到对应的咨询工单信息");
        }

        return order;
    }

    @Override
    public FsipConsultOrderRsp queryListByOrderId(String orderId) {
        FsipConsultOrderEntity order = fsipConsultOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("未找到对应的咨询工单信息");
        }

        FsipConsultOrderRsp orderRsp = FsipConsultOrderRsp.builder().currOrder(order).build();

        List<FsipConsultOrderEntity> hisOrderList = fsipConsultOrderMapper.selectHisByExpertId(order.getTargetId(), order.getExpertId());
        if(!CollUtil.isEmpty(hisOrderList)){
            orderRsp.setHisOrderList(hisOrderList.stream().filter(item -> !item.getOrderId().equals(orderId)).collect(Collectors.toList()));
        }

        return orderRsp;
    }
}
