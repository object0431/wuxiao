package com.asiainfo.fsip.service;

import com.asiainfo.fsip.model.ApprovalNodeModel;
import com.asiainfo.fsip.model.OrganizerStrucRsp;
import com.asiainfo.fsip.model.QryEmployeeReq;
import com.asiainfo.mcp.tmc.common.entity.DepartmentInfo;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StaffInfoService {

    PageInfo<OrganizerStrucRsp.EmployeeChildrenBean> queryStaffInfoList(PageReq<QryEmployeeReq> req);

    List<String> getSubDeptList(String parentOrgCode);

    List<StaffInfo> getStaffList(String staffId);

    StaffInfo switchIdentity(long id);

    /**
     * 获取待审批列表
     */
    List<ApprovalNodeModel> getApprovalList(String approvalType, String extId, StaffInfo staffInfo) throws Exception;

    /**
     * 获取员工的部门编码
     */
    DepartmentInfo getDepartment(String deptCode, Map<String, DepartmentInfo> departMap);
}
