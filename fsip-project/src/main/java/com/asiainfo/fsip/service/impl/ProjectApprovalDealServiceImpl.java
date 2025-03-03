package com.asiainfo.fsip.service.impl;

import cn.hutool.core.map.MapUtil;
import com.asiainfo.fsip.config.VerifyProperties;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FsipApprovalParamEntity;
import com.asiainfo.fsip.entity.FsipFlowLogEntity;
import com.asiainfo.fsip.entity.FsipProjectInitiationEntity;
import com.asiainfo.fsip.mapper.fsip.FsipApprovalParamMapper;
import com.asiainfo.fsip.mapper.fsip.FsipProjectInitiationMapper;
import com.asiainfo.fsip.model.ApprovalModel;
import com.asiainfo.fsip.model.UrlModel;
import com.asiainfo.fsip.service.ApprovalDealService;
import com.asiainfo.fsip.service.FlowLogService;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("projectApprovalDealService")
@Slf4j
public class ProjectApprovalDealServiceImpl implements ApprovalDealService {
    @Resource
    private FsipProjectInitiationMapper fsipProjectInitiationMapper;
    @Resource
    private FlowLogService flowLogService;
    @Resource
    private FsipApprovalParamMapper fsipApprovalParamMapper;

    @Resource
    private VerifyProperties verifyProperties;
    @Override
    public Map<String, Object> getTargetInfo(String targetId) {
        FsipProjectInitiationEntity initiationEntity = fsipProjectInitiationMapper.selectById(targetId);
        if(initiationEntity == null){
            throw new BusinessException("未查询到项目立项信息");
        }

        if(!IFsipConstants.Status.BMLDSP.equals(initiationEntity.getStatus())
                && !IFsipConstants.Status.FGBMLD.equals(initiationEntity.getStatus())
                && !IFsipConstants.Status.GHZX.equals(initiationEntity.getStatus())
                && !IFsipConstants.Status.PSWYH.equals(initiationEntity.getStatus())){
            throw new BusinessException("当前状态不是待审批状态");
        }

        String pendingCode = StringUtils.isEmpty(initiationEntity.getPendingCode())
                ? initiationEntity.getProjectId() : initiationEntity.getPendingCode();

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("TARGET_ID", initiationEntity.getProjectId());
        retMap.put("TARGET_NAME","立项申请-".concat(initiationEntity.getProjectName()).concat("-").concat(initiationEntity.getApplierName()));
        retMap.put("PENDING_CODE", pendingCode);
        retMap.put("DING_TASK_ID", initiationEntity.getDingTaskId());
        retMap.put("NODE_CODE", initiationEntity.getApprNodeCode());
        retMap.put("APPLIER_ID", initiationEntity.getApplierId());
        retMap.put("APPLIER_NAME", initiationEntity.getApplierName());
        retMap.put("APPLIER_COMPANY_ID", initiationEntity.getApplierCompanyId());
        return retMap;
    }

    @Override
    public int modifyApprovalState(String targetId, Map<String, Object> targetMap, String state, String remark, StaffInfo staffInfo, Map<String, String> ext) {
        FsipProjectInitiationEntity initiationEntity = new FsipProjectInitiationEntity();
        initiationEntity.setProjectId(targetId);
        initiationEntity.setStatus(state);
        fsipProjectInitiationMapper.updateById(initiationEntity);

        String nodeCode = MapUtil.getStr(targetMap, "NODE_CODE");
        FsipApprovalParamEntity paramEntity = fsipApprovalParamMapper.selectByTypeNode(IFsipConstants.TaskType.LXSQ,nodeCode);
        FsipFlowLogEntity flowLog = FsipFlowLogEntity.builder()
                .flowType(IFsipConstants.TaskType.LXSQ)
                .extId(targetId)
                .nodeCode(nodeCode)
                .nodeName(paramEntity==null?"":paramEntity.getNodeName())
                .nodeStateName(paramEntity==null?"":paramEntity.getNodeName())
                .nodeState(state)
                .dealStaffId(staffInfo.getMainUserId())
                .dealStaffName(staffInfo.getEmpName())
                .remark(remark).build();
        return flowLogService.addFlowLog(flowLog);
    }

    @Override
    public void modifyApprovalNode(String targetId, String nodeCode, ApprovalModel approvalModel) {
        FsipProjectInitiationEntity entity = FsipProjectInitiationEntity.builder().projectId(targetId).apprNodeCode(nodeCode)
                .pendingCode(approvalModel.getPendingCode()).dingTaskId(approvalModel.getDingTaskId()).build();
        fsipProjectInitiationMapper.updateById(entity);
    }

    @Override
    public UrlModel getVerifyUrl() {
        return UrlModel.builder().pcUrl(verifyProperties.getProjectPcUrl()).mobileUrl(verifyProperties.getProjectMobileUrl()).build();
    }

    @Override
    public void complete(String targetId) {
        FsipProjectInitiationEntity entity = FsipProjectInitiationEntity.builder().status(IFsipConstants.Status.FINISH).projectId(targetId).build();
        fsipProjectInitiationMapper.updateById(entity);
    }
}
