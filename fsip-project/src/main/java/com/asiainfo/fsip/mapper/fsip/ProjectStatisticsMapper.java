package com.asiainfo.fsip.mapper.fsip;

import com.asiainfo.fsip.model.LoginEmpInfoModel;
import com.asiainfo.fsip.model.ProjectStatisticsProTypeModel;
import com.asiainfo.fsip.model.TimeScopeReq;
import com.asiainfo.mcp.tmc.common.entity.DepartmentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: fsip-backend
 * @author: cnda
 * @create: 2024-01-04 16:42
 **/
public interface ProjectStatisticsMapper {

    /**
     * sql 利用 union all 进行全连接
     */
    List<ProjectStatisticsProTypeModel> findProjectStatisticsByType(@Param("req") TimeScopeReq req);

    /**
     * 查询登录人员信息
     */
    List<LoginEmpInfoModel> findLoginEmpInfo(@Param("req") TimeScopeReq req);

    /**
     * 根据部门编号查询部门公司信息
     * 保证（deptIds 元素不重复）
     */
    List<DepartmentInfo> findDeptInfoByDeptId(@Param("ids") List<String> deptIds);
}
