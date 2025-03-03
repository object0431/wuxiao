package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FsipApprovalParamEntity;
import com.asiainfo.fsip.entity.FsipFlowLogEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementBaseEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.mapper.fsip.FsipApprovalParamMapper;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementBaseMapper;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementMapper;
import com.asiainfo.fsip.model.ApprovalModel;
import com.asiainfo.fsip.model.UrlModel;
import com.asiainfo.fsip.service.ApprovalDealService;
import com.asiainfo.fsip.service.FlowLogService;
import com.asiainfo.fsip.service.FsipProjectAchievementService;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("city2ProvAchievementApprovalService")
public class City2ProvAchievementApprovalServiceImpl implements ApprovalDealService {

    @Resource
    private FlowLogService flowLogService;

    @Resource
    private FsipApprovalParamMapper fsipApprovalParamMapper;

    @Resource
    private FsipProjectAchievementBaseMapper fsipProjectAchievementBaseMapper;

    @Resource
    private FsipProjectAchievementMapper fsipProjectAchievementMapper;

    @Override
    public Map<String, Object> getTargetInfo(String targetId) {
        log.info("======city2ProvAchievementApprovalService===getTargetInfo=====targetId==========", targetId);
        Map<String, Object> param = new HashMap<>();
        param.put("PENDING_CODE", targetId);
        List<FsipProjectAchievementBaseEntity> fpabeList = fsipProjectAchievementBaseMapper.selectByMap(param);

        if (fpabeList == null && !fpabeList.isEmpty()) {
            throw new BusinessException("未查询到该成果信息");
        }
        String pendingCode = "";
        String nodeCode = "";
        String applierId = "";
        String applierCompanyId = "";
        for (FsipProjectAchievementBaseEntity fpabe : fpabeList) {
            pendingCode = fpabe.getPendingCode();
            nodeCode = fpabe.getApprNodeCode();
            applierId = fpabe.getApplierId();
            applierCompanyId = fpabe.getApplierCompanyId();
        }

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("PENDING_CODE", pendingCode);
        retMap.put("NODE_CODE", nodeCode);
        retMap.put("APPLIER_ID", applierId);
        retMap.put("APPLIER_COMPANY_ID", applierCompanyId);
        return retMap;
    }

    @Override
    public int modifyApprovalState(String targetId, Map<String, Object> targetMap, String state, String remark, StaffInfo staffInfo, Map<String, String> ext) {
        log.info("======city2ProvAchievementApprovalService===getTargetInfo=====targetId==========", targetId);
        Map<String, Object> param = new HashMap<>();
        param.put("PENDING_CODE", targetId);
        List<FsipProjectAchievementBaseEntity> fpabeList = fsipProjectAchievementBaseMapper.selectByMap(param);

        int finish = 1;//-1表示审核任务还没有完成，则不完成审核

        String nodeCode = MapUtil.getStr(targetMap, "NODE_CODE");
        FsipApprovalParamEntity paramEntity = fsipApprovalParamMapper.selectByTypeNode(IFsipConstants.TaskType.SJCGZSJ, nodeCode);
        int ret = 0;
        if (!CollUtil.isEmpty(fpabeList)) {
            String projectIds = ext.get("projectId");//通过的id
            String projectRefuseIds = ext.get("projectRefuseId");//不通过的id
            for (FsipProjectAchievementBaseEntity fsipProjectAchievementBaseEntity : fpabeList) {
                if(null==fsipProjectAchievementBaseEntity.getCityToProvFlag()
                        && "00".equals(fsipProjectAchievementBaseEntity.getStatus())) {//只针对还未审核的项目进行处理
                    FsipProjectAchievementEntity fpae = fsipProjectAchievementMapper.selectById(fsipProjectAchievementBaseEntity.getAchievementId());

                    if (null!=projectIds && projectIds.contains(fsipProjectAchievementBaseEntity.getAchievementId())) {
                        fsipProjectAchievementBaseEntity.setCityToProvFlag("1");

                        fpae.setStatus(IFsipConstants.Status.PSWYH);
                        fpae.setCityToProvFlag("1");
                        fpae.setSjcgspStatus("2");//1审批中 2审批同意 3审批不同意
                    } else if (null!=projectRefuseIds && projectRefuseIds.contains(fsipProjectAchievementBaseEntity.getAchievementId())){
                        //回退直接改成00状态。需要的时候再次发起
                        fsipProjectAchievementBaseEntity.setStatus(IFsipConstants.Status.TH);
                        state = IFsipConstants.Status.TH;
                        fpae.setStatus(IFsipConstants.Status.FINISH);
                        fsipProjectAchievementBaseEntity.setApprNodeCode(null);
                        fpae.setCityToProvFlag(null);
                        fpae.setSjcgspStatus("3");//1审批中 2审批同意 3审批不同意
                    } else {
                        finish = -1;
                        continue;
                    }

                    fsipProjectAchievementMapper.updateById(fpae);
                    fsipProjectAchievementBaseMapper.updateById(fsipProjectAchievementBaseEntity);

                    FsipFlowLogEntity flowLog = FsipFlowLogEntity.builder()
                            .flowType(IFsipConstants.TaskType.SJCGZSJ)
                            .extId(fsipProjectAchievementBaseEntity.getAchievementId())
                            .nodeCode(nodeCode)
                            .nodeName(paramEntity == null ? "" : paramEntity.getNodeName())
                            .nodeState(state)
                            .dealStaffId(staffInfo.getMainUserId())
                            .dealStaffName(staffInfo.getEmpName())
                            .remark(remark).build();
                    ret += flowLogService.addFlowLog(flowLog);
                }
            }
        }
        //return ret;
        return finish;
    }

    @Override
    public void modifyApprovalNode(String targetId, String nodeCode, ApprovalModel approvalModel) {
    }

    @Override
    public UrlModel getVerifyUrl() {
        return UrlModel.builder().pcUrl("").build();
    }

    @Override
    public void complete(String targetId) {
        log.info("======city2ProvAchievementApprovalService===complete=====targetId==========", targetId);
        Map<String, Object> param = new HashMap<>();
        param.put("PENDING_CODE", targetId);
        List<FsipProjectAchievementBaseEntity> fpabeList = fsipProjectAchievementBaseMapper.selectByMap(param);

        if (fpabeList == null && !fpabeList.isEmpty()) {
            for (FsipProjectAchievementBaseEntity fsipProjectAchievementBaseEntity : fpabeList) {
                log.info("==============fsipProjectAchievementBaseEntity.getStatus()===================" + fsipProjectAchievementBaseEntity.getStatus());
                if (!IFsipConstants.Status.TH.equals(fsipProjectAchievementBaseEntity.getStatus())) {
                    fsipProjectAchievementBaseEntity.setStatus(IFsipConstants.Status.FINISH);
                    fsipProjectAchievementBaseEntity.setCityToProvFlag("1");

                    log.info("==============setCityToProvFlag=========1==========");
                } else {
                    fsipProjectAchievementBaseEntity.setStatus(IFsipConstants.Status.FINISH);
                }
                fsipProjectAchievementBaseMapper.updateById(fsipProjectAchievementBaseEntity);
            }
        }
    }
}
