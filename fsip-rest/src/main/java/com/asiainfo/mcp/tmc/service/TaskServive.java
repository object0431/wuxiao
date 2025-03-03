package com.asiainfo.mcp.tmc.service;

import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingUpEntity;

public interface TaskServive {

    BaseRsp<Void> addPendingTask(PendingEntity[] data);

    BaseRsp<Void> updatePendingStatus(PendingUpEntity[] data);

}
