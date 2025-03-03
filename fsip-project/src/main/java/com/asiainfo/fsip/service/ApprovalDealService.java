package com.asiainfo.fsip.service;

import com.asiainfo.fsip.model.ApprovalModel;
import com.asiainfo.fsip.model.UrlModel;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

import java.util.Map;

public interface ApprovalDealService {

    /**
     * 根据培训计划编码、立项编码或结算编码查询对应的培训计划数据或培训立项数据
     */
    Map<String, Object> getTargetInfo(String targetId);

    /**
     * 修改培训计划、立项等数据状态并添加审核轨迹
     */
    int modifyApprovalState(String targetId, Map<String, Object> targetMap, String state, String remark, StaffInfo staffInfo
            , Map<String, String> ext);

    /**
     * 保存下一个审核结算数据以及更新待办编码
     */
    void modifyApprovalNode(String targetId, String nodeCode, ApprovalModel approvalModel);

    /**
     * 获取审核地址
     */
    UrlModel getVerifyUrl();

    /**
     * 完成节点
     */
    void complete(String targetId);
}
