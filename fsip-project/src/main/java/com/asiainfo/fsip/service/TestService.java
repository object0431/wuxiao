package com.asiainfo.fsip.service;

import com.asiainfo.fsip.model.PendingModel;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.TitleInfo;

public interface TestService {

    void sendPendingTask(PendingModel pendingModel, StaffInfo staffInfo);

    String getTitle(String type, TitleInfo titleInfo);
}
