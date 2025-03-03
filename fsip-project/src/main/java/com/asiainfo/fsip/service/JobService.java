package com.asiainfo.fsip.service;

import com.asiainfo.fsip.model.LoginStatModel;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;

import java.util.List;

public interface JobService {

    /**
     * 统计登陆数据
     */
    void statisticsLoginData();

    /**
     * 生成登录统计数据
     */
    void generateLoginStatData(String month) throws Exception;

    /**
     * 生成登录统计数据
     */
    void generateLoginStatData(String startDate, String endDate) throws Exception;

    /**
     * 修复登陆人员部门编码
     */
    void modifyLoginDeptId(String startDate);

    /**
     * 导出统计数据
     */
    List<LoginStatModel> queryStatDataList(String statMonth, StaffInfo staffInfo);
}
