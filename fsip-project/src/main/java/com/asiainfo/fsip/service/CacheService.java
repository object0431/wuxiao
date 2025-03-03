package com.asiainfo.fsip.service;

import com.asiainfo.fsip.model.ParamModel;
import com.asiainfo.mcp.tmc.common.entity.DepartmentInfo;
import com.asiainfo.mcp.tmc.entity.MiniUserEntity;

import java.util.List;
import java.util.Map;

public interface CacheService {

    /**
     * 刷新参数：attrType为空时，将整个参数表刷新至redis
     */
    void refreshParam(String attrType);

    /**
     * 根据参数类型和参数编码获取参数值
     */
    String getParamValue(String attrType, String attrCode);

    /**
     * 根据参数类型获取参数列表
     */
    Map<String, String> getParamListByType(String attrType);

    /**
     * 获取整个参数列表
     */
    Map<String, Map<String, String>> getParamMap();

    /**
     * 获取参数值
     */
    String getParamValue(Map<String, Map<String, String>> paramMap, String attrType, String attrCode);

    /**
     * 根据部门编码获取部门信息
     */
    DepartmentInfo getDepartment(String departId);

    /**
     * 查询公司列表，前台使用
     */
    List<ParamModel> getCompanyList();

    /**
     * 获取公司列表
     */
    Map<String, String> getCompanyMap();

    /**
     * 根据公司编码获取部门列表
     */
    Map<String, String> getDepartmentMap(String companyId);

    /**
     * 更新缓存
     */
    void refreshParam(String attrType, String attrCode, String attrValue);

    /**
     * 根据HrEmpCode查询员工缓存信息
     */
    Map<String, MiniUserEntity> getEmployeeHrMap();

    /**
     * mainUserId和HrEmpCode映射关系
     */
    Map<String, String> getUserId2HrCodeMap();

    /**
     * 获取部门缓存信息
     */
    Map<String, DepartmentInfo> getDepartmentCache();

}
