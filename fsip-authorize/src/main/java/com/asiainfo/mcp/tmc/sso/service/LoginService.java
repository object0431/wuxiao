package com.asiainfo.mcp.tmc.sso.service;

import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

public interface LoginService {

    StaffInfo getDingLoginUser(String token);

    StaffInfo getLoginUser(String userId);

    void setWorkBenchType(StaffInfo loginUser);
}
