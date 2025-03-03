package com.asiainfo.fsip.service;

import com.asiainfo.fsip.model.LoginEmpInfoModel;
import com.asiainfo.fsip.model.LoginStatisticsModel;
import com.asiainfo.fsip.model.ProjectStatisticsProTypeModel;
import com.asiainfo.fsip.model.TimeScopeReq;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @program: fsip-backend
 * @author: cnda
 * @create: 2024-01-02 15:31
 **/
public interface ProjectClassificationService {
    /**
     * 获取立项分类统计信息列表
     */
    PageInfo<ProjectStatisticsProTypeModel> getProjectStatisticsInfoByType(PageReq<TimeScopeReq> dateMap);

    List<ProjectStatisticsProTypeModel> getProjectStatisticsInfoByType(TimeScopeReq req);

    /**
     * 根据 type 动态的改变
     * {@link ProjectStatisticsProTypeModel#getStatType()}
     * 字段上 {@link com.asiainfo.mcp.tmc.common.annotation.ExcelField} 注解的 {@code name} 属性
     * @param type 类型为 {@code XMLX}、{@code CXLX}
     * @return 修改了注解之后的 Class
     */
    Class<?> builderExcelName(String type) throws Exception;

    /**
     * 查询登录数统计信息
     */
    PageInfo<LoginStatisticsModel> findLoginStatisticsInfo(PageReq<TimeScopeReq> pageReq);


    List<LoginStatisticsModel> findLoginStatisticsInfo(TimeScopeReq frameReq) throws Exception;

    /**
     * 查询登录人员信息列表
     */
    PageInfo<LoginEmpInfoModel> findLoginEmpInfo(PageReq<TimeScopeReq> req);

    /**
     * 查询登录人员信息列表
     * <p>针对 staffName 进行模糊匹配查询，如果参数 staffName 不为空，则查询结果不需要针对 staffName 做额外处理
     * <p>如果参数 staffName 为空，只有在结果中这个字段为 null 的情况下才进行处理，否则也不进行额外处理。
     */
    List<LoginEmpInfoModel> findLoginEmpInfo(TimeScopeReq timeScopeReq);
}
