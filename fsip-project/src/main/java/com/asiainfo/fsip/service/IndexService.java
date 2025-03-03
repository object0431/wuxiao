package com.asiainfo.fsip.service;

import com.asiainfo.fsip.model.IndexAchievementQueryReq;
import com.asiainfo.fsip.model.IndexAchievementQueryRsp;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

public interface IndexService {

    IndexAchievementQueryRsp achievementQuery(IndexAchievementQueryReq req, StaffInfo staffInfo);
}
