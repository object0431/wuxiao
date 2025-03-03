package com.asiainfo.mcp.tmc.sso.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.entity.DepartmentInfo;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.UserInfo;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.TokenUtils;
import com.asiainfo.mcp.tmc.dingding.entity.TmcDingUserInfo;
import com.asiainfo.mcp.tmc.dingding.service.DingUserService;
import com.asiainfo.mcp.tmc.entity.MiniUserEntity;
import com.asiainfo.mcp.tmc.mapper.MiniOrgMapper;
import com.asiainfo.mcp.tmc.mapper.MiniUserMapper;
import com.asiainfo.mcp.tmc.sso.entity.TmcLecturerInfo;
import com.asiainfo.mcp.tmc.sso.entity.TmcPermissionInfoEntity;
import com.asiainfo.mcp.tmc.sso.mapper.SsoTmcLecturerInfoMapper;
import com.asiainfo.mcp.tmc.sso.mapper.SsoTmcPermissionMapper;
import com.asiainfo.mcp.tmc.sso.service.LoginService;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    @Qualifier("fastRedisTemplate")
    private RedisTemplate fastRedisTemplate;

    @Resource
    private DingUserService dingUserService;

    @Resource
    private MiniUserMapper miniUserMapper;

    @Resource
    private MiniOrgMapper miniOrgMapper;

    @Resource
    private SsoTmcLecturerInfoMapper ssoTmcLecturerInfoMapper;

    @Resource
    private SsoTmcPermissionMapper ssoTmcPermissionMapper;

    @Override
    public StaffInfo getDingLoginUser(String token) {
        String userId = stringRedisTemplate.opsForValue().get(token);
        StaffInfo loginUser;
        if (StringUtils.isEmpty(userId)) {
            UserInfo userInfo = TokenUtils.decode(token);
            if (userInfo == null || StringUtils.isEmpty(userInfo.getMobile())) {
                log.error("the userinfo or mobile is null");
                throw new BusinessException("0909", "用户信息校验失败！");
            }
            TmcDingUserInfo tmcDingUserInfo;
            List<MiniUserEntity> employeeInfoList = miniUserMapper.selectBySerialNumber(userInfo.getMobile());
            if (CollUtil.isEmpty(employeeInfoList)) {
                log.error("Could not find any employee by mobile = {}", JSONObject.toJSONString(userInfo));

                employeeInfoList = miniUserMapper.selectByDingDeptId(userInfo.getDeptId());
                if (CollUtil.isEmpty(employeeInfoList)) {
                    throw new BusinessException("0909", "未找到对应的公司和部门信息！");
                }

                tmcDingUserInfo = TmcDingUserInfo.builder().dingUserId(userInfo.getUserId()).mobile(userInfo.getMobile()).deptId(userInfo.getDeptId()).build();
                loginUser = getLoginUser(employeeInfoList.get(0).getAccountCode());
                loginUser.setMobileTel(userInfo.getMobile());
                loginUser.setEmpName(userInfo.getUserName());
                loginUser.setEmailAddress("");
                loginUser.setIdentityNumber("");
            } else {
                tmcDingUserInfo = TmcDingUserInfo.builder().dingUserId(userInfo.getUserId()).mobile(userInfo.getMobile())
                        .deptId(userInfo.getDeptId()).userId(employeeInfoList.get(0).getAccountCode()).build();

                loginUser = getLoginUser(employeeInfoList.get(0).getAccountCode());
            }

            dingUserService.saveUserInfo(tmcDingUserInfo);

            return loginUser;
        }

        return getLoginUser(userId);
    }

    @Override
    public StaffInfo getLoginUser(String userId) {
        String redisKey = "USER:" + userId;
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);

        if (!StringUtils.isEmpty(redisValue)) {
            return JSONObject.parseObject(redisValue, StaffInfo.class);
        }
        List<MiniUserEntity> userEntityList = miniUserMapper.selectByUserId(userId);
        if (CollUtil.isEmpty(userEntityList)) {
            throw new BusinessException("0909", "用户信息校验失败，userId = " + userId);
        }

        StaffInfo loginUser = StaffInfoUtil.copyProperties(userEntityList.get(0));

        Map<String, DepartmentInfo> departmentMap = loadAllDepartment();
        loadDepartmentInfo(loginUser, departmentMap);

        setWorkBenchType(loginUser);

        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(loginUser), 60 * 60 * 2, TimeUnit.SECONDS);
        return loginUser;
    }

    private void loadDepartmentInfo(StaffInfo loginUser, Map<String, DepartmentInfo> departmentMap) {
        if (CollUtil.isEmpty(departmentMap)) {
            throw new BusinessException("未获取到部门和公司信息");
        }

        DepartmentInfo departmentInfo = departmentMap.get(loginUser.getDeptId());
        if (departmentInfo != null) {
            loginUser.setCompanyId(departmentInfo.getCompanyId());
            loginUser.setCompanyName(departmentInfo.getCompanyName());
            return;
        }

        String parentOrgCode = miniOrgMapper.selectParentOrg(loginUser.getDeptId());
        if (StringUtils.isEmpty(parentOrgCode)) {
            throw new BusinessException("未获取到部门和公司信息");
        }

        departmentInfo = departmentMap.get(parentOrgCode);
        loginUser.setDeptId(parentOrgCode);

        if (departmentInfo == null) {
            loadDepartmentInfo(loginUser, departmentMap);
        } else {
            loginUser.setDeptName(departmentInfo.getDeptName());
            loginUser.setCompanyId(departmentInfo.getCompanyId());
            loginUser.setCompanyName(departmentInfo.getCompanyName());
        }
    }

    private Map<String, DepartmentInfo> loadAllDepartment() {
        Object cacheDeptMap = fastRedisTemplate.opsForValue().get(IConstants.CACHE_ALL_DEPART_MAP);
        if (cacheDeptMap == null) {
            return Collections.emptyMap();
        }

        Map<String, DepartmentInfo> departmentMap = (Map<String, DepartmentInfo>) cacheDeptMap;
        if (CollUtil.isEmpty(departmentMap)) {
            return Collections.emptyMap();
        }

        return departmentMap;
    }

    @Override
    public void setWorkBenchType(StaffInfo loginUser) {
        String userId = loginUser.getMainUserId();
        List<TmcPermissionInfoEntity> tmcPermissionInfoEntityList = ssoTmcPermissionMapper.selectByStaffId(userId);

        if (!CollUtil.isEmpty(tmcPermissionInfoEntityList)) {
            for (TmcPermissionInfoEntity entity : tmcPermissionInfoEntityList) {
                if (IConstants.PermType.SGLY.equals(entity.getPermType()) || IConstants.PermType.SGLY.equals(loginUser.getPersonType())) {
                    loginUser.setPersonType(IConstants.PermType.SGLY);
                } else if (IConstants.PermType.DSGLY.equals(entity.getPermType()) || IConstants.PermType.DSGLY.equals(loginUser.getPersonType())) {
                    loginUser.setPersonType(IConstants.PermType.DSGLY);
                } else if (IConstants.PermType.BMGLY.equals(entity.getPermType()) || IConstants.PermType.BMGLY.equals(loginUser.getPersonType())) {
                    loginUser.setPersonType(IConstants.PermType.BMGLY);
                } else {
                    loginUser.setPersonType(entity.getPermType());
                }
            }

            for (TmcPermissionInfoEntity entity : tmcPermissionInfoEntityList) {
                if (IConstants.PermType.KSGLY.equals(entity.getPermType())) {
                    loginUser.setExamManger(entity.getPermType());
                }
            }
        }

        if (IConstants.PermType.SGLY.equals(loginUser.getPersonType()) || IConstants.PermType.DSGLY.equals(loginUser.getPersonType())
                || IConstants.PermType.BMGLY.equals(loginUser.getPersonType()) || IConstants.PermType.PXLXFQR.equals(loginUser.getPersonType())
                || IConstants.PermType.RLFZR.equals(loginUser.getPersonType())) {
            loginUser.setWorkBenchType(IConstants.WorkBenchType.PXGLY);
        }else{
            loginUser.setWorkBenchType(IConstants.WorkBenchType.CXRY);
        }

        if (org.apache.commons.lang.StringUtils.isBlank(loginUser.getPersonType())) {
            loginUser.setPersonType(IConstants.PermType.PTYG);
        }
    }
}
