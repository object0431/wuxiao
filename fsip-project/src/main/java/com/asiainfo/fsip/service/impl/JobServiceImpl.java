package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.entity.FsipLoginInitiationStatEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.entity.FsipProjectInitiationEntity;
import com.asiainfo.fsip.entity.FsipTeamInfoEntity;
import com.asiainfo.fsip.mapper.fsip.*;
import com.asiainfo.fsip.mapper.tmc.TmcEmployeeMapper;
import com.asiainfo.fsip.model.LoginStatModel;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.fsip.service.JobService;
import com.asiainfo.fsip.service.StaffInfoService;
import com.asiainfo.fsip.utils.DateUtils;
import com.asiainfo.mcp.tmc.common.entity.DepartmentInfo;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.entity.MiniUserEntity;
import com.asiainfo.mcp.tmc.mapper.MiniOrgMapper;
import com.asiainfo.mcp.tmc.sso.entity.FsipLoginLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JobServiceImpl implements JobService {

    @Resource
    private FsipLoginLogMapper fsipLoginLogMapper;

    @Resource
    private FsipTeamInfoMapper fsipTeamInfoMapper;

    @Resource
    private MiniOrgMapper miniOrgMapper;

    @Resource
    private TmcEmployeeMapper tmcEmployeeMapper;

    @Resource
    private FsipProjectInitiationMapper fsipProjectInitiationMapper;

    @Resource
    private FsipProjectAchievementMapper fsipProjectAchievementMapper;

    @Resource
    private FsipLoginInitiationStatMapper fsipLoginInitiationStatMapper;

    @Resource
    private CacheService cacheService;

    @Resource
    private StaffInfoService staffInfoService;

    @Transactional
    @Override
    public void statisticsLoginData() {
        List<Map<String, Object>> loginDataList = fsipLoginLogMapper.countLastMonthLoginData();
        if (CollUtil.isEmpty(loginDataList)) {
            log.warn("there is no login data");
            return;
        }

        Map<String, Integer> loginDataMap = convertLoginMap(loginDataList);

        List<FsipTeamInfoEntity> teamInfoEntityList = fsipTeamInfoMapper.selectPendingData();
        if (CollUtil.isEmpty(teamInfoEntityList)) {
            return;
        }

        teamInfoEntityList.parallelStream().forEach(item -> {
            Integer loginStaffNum = loginDataMap.get(item.getCompanyId());
            if (loginStaffNum == null) {
                return;
            }

            int total = countStaffByCompanyId(item.getCompanyId());
            if (total <= 0) {
                return;
            }

            int shineValue = Math.floorDiv(loginStaffNum, total) * 1000;
            int loginShineValue = item.getLoginShineValue();
            loginShineValue = loginShineValue + shineValue;
            item.setLoginShineValue(loginShineValue);
            item.setUpdateShineTime(new Date());

            fsipTeamInfoMapper.updateById(item);
        });
    }

    @Override
    public void generateLoginStatData(String statMonth) throws Exception {
        String startDate = statMonth.concat("01");
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtils.parseDate(startDate, DateUtils.yyyyMMdd));
        int month = cal.get(Calendar.MONTH) + 1;
        cal.set(Calendar.MONTH, month);

        String endDate = DateUtils.formatDate(cal.getTime(), DateUtils.yyyyMMdd);
        generateLoginStatData(startDate, endDate);
    }

    @Override
    public void generateLoginStatData(String startDate, String endDate) throws Exception {
        log.info("begin to generate login stat data, startDate = {}, endDate = {}", startDate, endDate);
        Map<String, DepartmentInfo> departMap = cacheService.getDepartmentCache();

        Map<String, DepartmentInfo> staffDeptMap = cacheService.getDepartmentCache();

        List<FsipLoginLogEntity> loginLogEntities = fsipLoginLogMapper.countDeptLoginSData(startDate, endDate);

        Map<String, String> userId2HrCodeMap = cacheService.getUserId2HrCodeMap();
        Map<String, MiniUserEntity> employeeHrMap = cacheService.getEmployeeHrMap();

        String statMonth = startDate.substring(0, 6);
        Map<String, Integer> loginStatMap = new HashMap<>();
        if (!CollUtil.isEmpty(loginLogEntities)) {
            loginLogEntities.parallelStream().forEach(item -> {
                String hrEmpCode = userId2HrCodeMap.get(item.getLoginAccount());

                if (staffDeptMap.containsKey(item.getDeptId())) {
                    loginStatMap.put(item.getDeptId(), item.getTotal());
                } else if (employeeHrMap.containsKey(hrEmpCode)) {
                    loginStatMap.put(item.getDeptId(), item.getTotal());
                }
            });
        }

        List<FsipProjectInitiationEntity> initiationEntityList = fsipProjectInitiationMapper
                .countByDeptIdAndTime(startDate, endDate);
        Map<String, Integer> initiationStatMap = new HashMap<>();
        if (!CollUtil.isEmpty(initiationEntityList)) {
            initiationEntityList.parallelStream().forEach(item -> {
                initiationStatMap.put(item.getApplierDeptId(), item.getTotal());
            });
        }

        List<FsipProjectAchievementEntity> achievementEntityList = fsipProjectAchievementMapper
                .countByDeptId(startDate, endDate);
        Map<String, Integer> achievemenStatMap = new HashMap<>();
        if (!CollUtil.isEmpty(achievementEntityList)) {
            achievementEntityList.parallelStream().forEach(item -> {
                achievemenStatMap.put(item.getApplierDeptId(), item.getTotal());
            });
        }

        List<FsipLoginInitiationStatEntity> statEntityList = staffDeptMap.keySet().stream().map(deptId -> {
            DepartmentInfo department = departMap.get(deptId);

            int loginNum = MapUtil.getInt(loginStatMap, deptId, 0);
            int initiateNum = MapUtil.getInt(initiationStatMap, deptId, 0);
            int achievementNum = MapUtil.getInt(achievemenStatMap, deptId, 0);
            return FsipLoginInitiationStatEntity.builder().statMonth(statMonth)
                    .companyName(department.getCompanyName()).companyId(department.getCompanyId())
                    .deptId(department.getDeptId()).deptName(department.getDeptName())
                    .loginNum(loginNum).initiateNum(initiateNum).achievementNum(achievementNum).build();
        }).collect(Collectors.toList());

        fsipLoginInitiationStatMapper.deleteByStatMonth(statMonth);
        fsipLoginInitiationStatMapper.batchInsert(statEntityList);
    }

    @Override
    public void modifyLoginDeptId(String startDate) {
        List<FsipLoginLogEntity> pendingList = fsipLoginLogMapper.selectPendingDate(startDate);
        if (CollUtil.isEmpty(pendingList)) {
            return;
        }

        Map<String, String> user2HrMap = cacheService.getUserId2HrCodeMap();
        Map<String, MiniUserEntity> hrMap = cacheService.getEmployeeHrMap();

        String sql = "UPDATE FSIP_LOGIN_LOG SET DEPT_ID = '%s' WHERE LOGIN_ACCOUNT = '%s'";
        List<String> sqlList = pendingList.parallelStream().filter(item -> user2HrMap.containsKey(item.getLoginAccount()))
                .map(item -> {
                    String hrEmpCode = user2HrMap.get(item.getLoginAccount());
                    MiniUserEntity userEntity = hrMap.get(hrEmpCode);

                    return String.format(sql, userEntity.getDeptId(), item.getLoginAccount());
                }).collect(Collectors.toList());
        log.info(JSONObject.toJSONString(sqlList));
    }

    @Override
    public List<LoginStatModel> queryStatDataList(String statMonth, StaffInfo staffInfo) {
        return null;
    }

    private Map<String, Integer> convertLoginMap(List<Map<String, Object>> loginDataList) {
        Map<String, Integer> loginDataMap = new HashMap<>();
        for (Map<String, Object> item : loginDataList) {
            String companyId = MapUtil.getStr(item, "COMPANY_ID");
            if (StringUtils.isBlank(companyId)) {
                continue;
            }

            int loginStaffNum = MapUtil.getInt(item, "TOTAL");
            if (loginStaffNum == 0) {
                continue;
            }

            loginDataMap.put(companyId, loginStaffNum);
        }

        return loginDataMap;
    }

    private int countStaffByCompanyId(String companyId) {
        List<String> orgCodeList = new ArrayList<>();
        orgCodeList.add(companyId);
        getSubDeptList(orgCodeList);

        return tmcEmployeeMapper.countByDeptId(orgCodeList);
    }

    public void getSubDeptList(List<String> parentCodeList) {
        List<String> orgEntityList = miniOrgMapper.selectSubOrgCode(parentCodeList);
        if (CollUtil.isEmpty(orgEntityList)) {
            return;
        }

        parentCodeList.addAll(orgEntityList);
        getSubDeptList(orgEntityList);
    }
}
