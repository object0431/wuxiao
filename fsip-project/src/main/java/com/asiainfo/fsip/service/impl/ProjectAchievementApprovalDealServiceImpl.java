package com.asiainfo.fsip.service.impl;

import cn.hutool.core.map.MapUtil;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FsipApprovalParamEntity;
import com.asiainfo.fsip.entity.FsipFlowLogEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.mapper.fsip.FsipApprovalParamMapper;
import com.asiainfo.fsip.model.ApprovalModel;
import com.asiainfo.fsip.model.UrlModel;
import com.asiainfo.fsip.service.ApprovalDealService;
import com.asiainfo.fsip.service.FlowLogService;
import com.asiainfo.fsip.service.FsipProjectAchievementService;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("projectAchievementApprovalDealService")
@Slf4j
public class ProjectAchievementApprovalDealServiceImpl implements ApprovalDealService {
    @Resource
    private FsipProjectAchievementService fsipProjectAchievementService;
    @Resource
    private FlowLogService flowLogService;
    @Resource
    private FsipApprovalParamMapper fsipApprovalParamMapper;

    @Override
    public Map<String, Object> getTargetInfo(String targetId) {
        FsipProjectAchievementEntity projectAchievement = fsipProjectAchievementService.getById(targetId);
        if (projectAchievement == null) {
            throw new BusinessException("未查询到该成果申请信息");
        }

        if (!IFsipConstants.Status.BMLDSP.equals(projectAchievement.getStatus())
                && !IFsipConstants.Status.FGBMLD.equals(projectAchievement.getStatus())
                && !IFsipConstants.Status.GHZX.equals(projectAchievement.getStatus())
                && !IFsipConstants.Status.PSWYH.equals(projectAchievement.getStatus())) {
            throw new BusinessException("当前状态不是待审批状态");
        }

        String pendingCode = StringUtils.isEmpty(projectAchievement.getPendingCode())
                ? projectAchievement.getAchievementId() : projectAchievement.getPendingCode();

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("TARGET_ID", projectAchievement.getAchievementId());
        retMap.put("PENDING_CODE", pendingCode);
        retMap.put("TARGET_NAME", projectAchievement.getProjectName());
        retMap.put("NODE_CODE", projectAchievement.getApprNodeCode());
        retMap.put("APPLIER_ID", projectAchievement.getApplierId());
        retMap.put("APPLIER_NAME", projectAchievement.getApplierName());
        retMap.put("APPLIER_COMPANY_ID", projectAchievement.getApplierCompanyId());

        return retMap;
    }

    @Override
    public int modifyApprovalState(String targetId, Map<String, Object> targetMap, String state, String remark, StaffInfo staffInfo, Map<String, String> ext) {
        FsipProjectAchievementEntity projectAchievement = fsipProjectAchievementService.getById(targetId);
        projectAchievement.setStatus(state);
        fsipProjectAchievementService.updateById(projectAchievement);

        String nodeCode = MapUtil.getStr(targetMap, "NODE_CODE");
        if(IFsipConstants.Status.PSWYH.equals(state)){
            nodeCode = IConstants.NodeCode.PSWYHPS;
        }
        FsipApprovalParamEntity paramEntity = fsipApprovalParamMapper.selectByTypeNode(IFsipConstants.TaskType.CGSQ, nodeCode);
        FsipFlowLogEntity flowLog = FsipFlowLogEntity.builder()
                .flowType(IFsipConstants.TaskType.CGSQ)
                .extId(targetId)
                .nodeCode(nodeCode)
                .nodeName(paramEntity == null ? "" : paramEntity.getNodeName())
                .nodeStateName(paramEntity == null ? "" : paramEntity.getNodeName())
                .nodeState(state)
                .dealStaffId(staffInfo.getMainUserId())
                .dealStaffName(staffInfo.getEmpName())
                .remark(remark).build();
        return flowLogService.addFlowLog(flowLog);
    }

    @Override
    public void modifyApprovalNode(String targetId, String nodeCode, ApprovalModel approvalModel) {
        FsipProjectAchievementEntity projectAchievement = fsipProjectAchievementService.getById(targetId);
        projectAchievement.setApprNodeCode(nodeCode);
        projectAchievement.setPendingCode(approvalModel.getPendingCode());
        projectAchievement.setDingTaskId(approvalModel.getDingTaskId());
        fsipProjectAchievementService.updateById(projectAchievement);
    }

    @Override
    public UrlModel getVerifyUrl() {
        return UrlModel.builder().pcUrl("").build();
    }

    @Override
    public void complete(String targetId) {
        FsipProjectAchievementEntity projectAchievement = fsipProjectAchievementService.getById(targetId);
        projectAchievement.setStatus(IFsipConstants.Status.FINISH);
        fsipProjectAchievementService.updateById(projectAchievement);
    }
}
