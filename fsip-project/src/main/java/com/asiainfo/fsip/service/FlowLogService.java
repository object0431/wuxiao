package com.asiainfo.fsip.service;

import com.asiainfo.fsip.entity.FsipFlowLogEntity;
import com.asiainfo.fsip.model.FlowLogModel;

import java.util.Date;
import java.util.List;

public interface FlowLogService {

    int addFlowLog(FsipFlowLogEntity entity);

    List<FlowLogModel> queryFlowLogById(String flowType, String extId);

    FlowLogModel queryCurrentNode(String flowType, String extId);

    List<FlowLogModel> queryFlowLogByFlowTypeExtId(List flowTypeList, String extId);

}
