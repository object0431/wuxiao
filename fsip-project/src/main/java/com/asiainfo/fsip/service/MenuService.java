package com.asiainfo.fsip.service;


import com.asiainfo.fsip.model.MenuInfoResp;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

import java.util.TreeSet;

public interface MenuService {

    TreeSet<MenuInfoResp> queryUserMenuList(StaffInfo staffInfo);
}
