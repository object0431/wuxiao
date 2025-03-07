package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FsipApprovalNodeEntity;
import com.asiainfo.fsip.entity.FsipApprovalParamEntity;
import com.asiainfo.fsip.entity.FsipFlowLogEntity;
import com.asiainfo.fsip.mapper.fsip.FsipApprovalNodeMapper;
import com.asiainfo.fsip.mapper.fsip.FsipApprovalParamMapper;
import com.asiainfo.fsip.mapper.fsip.FsipFlowLogMapper;
import com.asiainfo.fsip.model.FlowLogModel;
import com.asiainfo.fsip.service.FlowLogService;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class FlowLogServiceImpl implements FlowLogService {

    @Resource
    private FsipFlowLogMapper fsipFlowLogMapper;

    @Resource
    private FsipApprovalParamMapper fsipApprovalParamMapper;

    @Resource
    private FsipApprovalNodeMapper fsipApprovalNodeMapper;

    @Override
    public int addFlowLog(FsipFlowLogEntity entity) {
        List<FsipFlowLogEntity> entityList = fsipFlowLogMapper.selectByTypeAndId(entity.getFlowType(), entity.getExtId());

        int sort = 1;

        if (!CollUtil.isEmpty(entityList)) {
            int index = entityList.size() - 1;
            sort = entityList.get(index).getSort() + 1;
        }

        Date currDate = new Date();
        entity.setSort(sort);
        entity.setStartTime(currDate);
        entity.setEndTime(currDate);

        fsipFlowLogMapper.insert(entity);

        return sort;
    }

    @Override
    public List<FlowLogModel> queryFlowLogById(String flowType, String extId) {
        List<FsipFlowLogEntity> flowLogEntityList = fsipFlowLogMapper.selectByTypeAndId(flowType, extId);

        if (CollUtil.isEmpty(flowLogEntityList)) {
            return Collections.emptyList();
        }

        List<String> completeNodeList = new ArrayList<>();
        List<FlowLogModel> flowLogModelList = new LinkedList<>();
        for (FsipFlowLogEntity flowLogEntity : flowLogEntityList) {
            FlowLogModel flowLogModel = FlowLogModel.builder().extId(flowLogEntity.getExtId())
                    .nodeCode(flowLogEntity.getNodeCode()).nodeName(flowLogEntity.getNodeName())
                    .operateId(flowLogEntity.getDealStaffId()).operateName(flowLogEntity.getDealStaffName())
                    .approvalRet(flowLogEntity.getNodeStateName()).remark(flowLogEntity.getRemark())
                    .isComplete("1")
                    .operateTime(DateUtils.getDateString(flowLogEntity.getStartTime(), "yyyy-MM-dd HH:mm:ss")).build();

            if("02".equals(flowLogEntity.getNodeState()) || "00".equals(flowLogEntity.getNodeState())){
                completeNodeList.add(flowLogEntity.getNodeCode());
            }

            flowLogModelList.add(flowLogModel);
        }


        List<FsipApprovalNodeEntity> approvalNodeList = fsipApprovalNodeMapper.selectApprovalNodeByType(flowType, extId);
        if (CollUtil.isEmpty(approvalNodeList)) {
            return flowLogModelList;
        }
        int size = approvalNodeList.size();
        List<FlowLogModel> allLogModelList = new LinkedList<>();
        for (int i = size - 1; i > -1; i--) {
            FsipApprovalNodeEntity nodeEntity = approvalNodeList.get(i);
            if(IFsipConstants.ZFTRSP.equals(nodeEntity.getNodeCode())){
                continue;
            }

            if(completeNodeList.contains(nodeEntity.getNodeCode())){
                break;
            }
            allLogModelList.add(FlowLogModel.builder().nodeCode(nodeEntity.getNodeCode())
                    .nodeName(nodeEntity.getNodeName()).operateId(nodeEntity.getDealStaffId())
                    .operateName(nodeEntity.getDealStaffName()).isComplete("0").build());
        }

        allLogModelList.addAll(flowLogModelList);

        return allLogModelList;
    }

    @Override
    public List<FlowLogModel> queryFlowLogByFlowTypeExtId(List flowTypeList, String extId) {
        List<FsipFlowLogEntity> flowLogEntityList = fsipFlowLogMapper.selectByFlowTypeListAndExtId(flowTypeList, extId);
        if (CollUtil.isEmpty(flowLogEntityList)) {
            return Collections.emptyList();
        }
        List<String> completeNodeList = new ArrayList<>();
        List<FlowLogModel> flowLogModelList = new LinkedList<>();
        for (FsipFlowLogEntity flowLogEntity : flowLogEntityList) {
            FlowLogModel flowLogModel = FlowLogModel.builder().extId(flowLogEntity.getExtId())
                    .nodeCode(flowLogEntity.getNodeCode()).nodeName(flowLogEntity.getNodeName())
                    .operateId(flowLogEntity.getDealStaffId()).operateName(flowLogEntity.getDealStaffName())
                    .approvalRet(flowLogEntity.getNodeStateName()).remark(flowLogEntity.getRemark())
                    .isComplete("1")
                    .operateTime(DateUtils.getDateString(flowLogEntity.getStartTime(), "yyyy-MM-dd HH:mm:ss")).build();

            if("02".equals(flowLogEntity.getNodeState()) || "00".equals(flowLogEntity.getNodeState())){
                completeNodeList.add(flowLogEntity.getNodeCode());
            }

            flowLogModelList.add(flowLogModel);
        }

        List<FsipApprovalNodeEntity> approvalNodeList = fsipApprovalNodeMapper.selectApprovalNodeByApprTypeList(flowTypeList, extId);
        if (CollUtil.isEmpty(approvalNodeList)) {
            return flowLogModelList;
        }
        int size = approvalNodeList.size();
        List<FlowLogModel> allLogModelList = new LinkedList<>();
        for (int i = size - 1; i > -1; i--) {
            FsipApprovalNodeEntity nodeEntity = approvalNodeList.get(i);
            if(IFsipConstants.ZFTRSP.equals(nodeEntity.getNodeCode())){
                continue;
            }

            if(completeNodeList.contains(nodeEntity.getNodeCode())){
                break;
            }
            allLogModelList.add(FlowLogModel.builder().nodeCode(nodeEntity.getNodeCode())
                    .nodeName(nodeEntity.getNodeName()).operateId(nodeEntity.getDealStaffId())
                    .operateName(nodeEntity.getDealStaffName()).isComplete("0").build());
        }
        allLogModelList.addAll(flowLogModelList);
        return allLogModelList;
    }

    @Override
    public FlowLogModel queryCurrentNode(String flowType2, String extId) {
        String flowType = flowType2;
        List<FsipFlowLogEntity> flowLogEntityList = fsipFlowLogMapper.selectByTypeAndId(flowType, extId);
        if (CollUtil.isEmpty(flowLogEntityList)){
            flowLogEntityList = fsipFlowLogMapper.selectByTypeAndId("PLXSQ", extId);
            if (flowLogEntityList.size() > 0){
                flowType = "PLXSQ";
            }
        }

        if (CollUtil.isEmpty(flowLogEntityList)) {
            return FlowLogModel.builder().nodeCode(IConstants.NodeCode.SQRFQ)
                    .nodeName(IConstants.NodeCodeName.SQRFQ).build();
        }
        FsipFlowLogEntity lastOne = flowLogEntityList.get(0);

        String lastNodeState = lastOne.getNodeState();
        if (IConstants.State.TH.equals(lastNodeState) || IConstants.State.TH2.equals(lastNodeState)) {
            return FlowLogModel.builder().nodeCode(IConstants.NodeCode.THFQRXG)
                    .nodeName(IConstants.NodeCodeName.THFQRXG).build();
        }

        Map<String, String> nodeMap = new HashMap<>();
        for (FsipFlowLogEntity flowLogEntity : flowLogEntityList) {
            if (nodeMap.containsKey(flowLogEntity.getNodeCode())) {
                continue;
            }

            nodeMap.put(flowLogEntity.getNodeCode(), flowLogEntity.getNodeState());
        }

        List<FsipApprovalParamEntity> approvalParamEntityList = fsipApprovalParamMapper.selectByType(flowType);

        FsipApprovalParamEntity paramEntity = null;
        for (FsipApprovalParamEntity tmcApprovalParam : approvalParamEntityList) {
            if ("02".equals(nodeMap.get(tmcApprovalParam.getNodeCode()))) {
                continue;
            }

            paramEntity = tmcApprovalParam;
            break;
        }

        if(paramEntity != null){
            return FlowLogModel.builder().nodeCode(paramEntity.getNodeCode()).nodeName(paramEntity.getNodeName()).build();
        }

        if(IConstants.TaskType.RCNS.equals(flowType) || IConstants.TaskType.RCLX.equals(flowType)){
            return FlowLogModel.builder().nodeCode("00").nodeName("审批完成").build();
        }

        return FlowLogModel.builder().nodeCode("JG").nodeName("已完成").build();
    }

}
