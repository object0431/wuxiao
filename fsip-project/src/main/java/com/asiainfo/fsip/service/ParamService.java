package com.asiainfo.fsip.service;

import com.asiainfo.fsip.entity.FsipStaticParamEntity;
import com.asiainfo.fsip.model.ParamModel;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

import java.util.List;

public interface ParamService {

    /**
     * 静态参数查询
     */
    List<FsipStaticParamEntity> queryParam(FsipStaticParamEntity req, StaffInfo staff);

    /**
     * 新增静态参数
     */
    int addParam(FsipStaticParamEntity req, StaffInfo staff);

    /**
     * 更新静态参数
     */
    int modifyParam(FsipStaticParamEntity req, StaffInfo staff);

    /**
     * 删除静态参数
     */
    int deleteParam(String attrType, List<String> attrCodeList);

    List<ParamModel> queryCompanyList();

    List<ParamModel> queryDeptList(String companyId, String deptName);
}
