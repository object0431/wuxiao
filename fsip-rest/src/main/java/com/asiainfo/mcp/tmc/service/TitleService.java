package com.asiainfo.mcp.tmc.service;

import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

public interface TitleService {

    String generateTitle(String type, StaffInfo staffInfo, String... keyArr);
}
