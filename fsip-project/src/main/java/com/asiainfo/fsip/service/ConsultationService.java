package com.asiainfo.fsip.service;

import com.asiainfo.fsip.entity.FsipConsultOrderEntity;
import com.asiainfo.fsip.model.ConsultationInitiateReq;
import com.asiainfo.fsip.model.ConsultationReplyReq;
import com.asiainfo.fsip.model.FsipConsultOrderRsp;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

import java.util.List;

public interface ConsultationService {

    /**
     * 发起咨询
     */
    void initiate(ConsultationInitiateReq req, StaffInfo staffInfo);

    /**
     * 回复咨询
     */
    void reply(ConsultationReplyReq req, StaffInfo staffInfo);

    /**
     * 咨询结果评分
     */
    void grade(String orderId, String score);

    /**
     * 查询咨询工单列表
     */
    List<FsipConsultOrderEntity> queryByTargetId(String targetId);

    /**
     * 查询咨询工单明細
     */
    FsipConsultOrderEntity queryByOrderId(String orderId);

    /**
     * 查询咨询工单明細
     */
    FsipConsultOrderRsp queryListByOrderId(String orderId);

}
