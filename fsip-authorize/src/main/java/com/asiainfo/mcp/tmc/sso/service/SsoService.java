package com.asiainfo.mcp.tmc.sso.service;

import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.chinaunicom.usercenter.sso.util.UserEntry;

public interface SsoService {

    Object ssoLogin();

    String userCenterCheck(String getToken) throws Exception;

    Object sdkObtainsService() throws Exception;

    Object ssoLoginStatusCheck() throws Exception;

    Object heartBeat() throws Exception;

    //UserEntry checkAuthentication(String soap) throws Exception;

    String assertionsQuery() throws Exception;

    StaffInfo getEmployInfo(String userId);
}
