package com.asiainfo.mcp.tmc.sso.service;

import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

public interface LoggerService {

    void logLogin(StaffInfo staffInfo, String deviceType);

    void recordMenuLog(String meneId, String deviceType, StaffInfo staffInfo);
}
