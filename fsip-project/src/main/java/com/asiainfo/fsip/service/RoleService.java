package com.asiainfo.fsip.service;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.entity.FispStaff2RoleEntity;
import com.asiainfo.fsip.model.QueryCommitteeListRsp;
import com.asiainfo.fsip.model.Staff2RoleModel;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface RoleService {

    PageInfo<Staff2RoleModel> queryStaffRoleList(PageReq<FispStaff2RoleEntity> req, StaffInfo staff);

    void deleteStaffRole(String roleId, List<String> staffIdList);

    void saveStaffRole(List<Staff2RoleModel> req, StaffInfo staff);

    QueryCommitteeListRsp queryCommitteeList(PageReq<JSONObject> req, StaffInfo staff);

}
