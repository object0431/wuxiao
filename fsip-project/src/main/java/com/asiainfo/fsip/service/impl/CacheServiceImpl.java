package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.aliyuncs.utils.StringUtils;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FsipStaticParamEntity;
import com.asiainfo.fsip.mapper.fsip.FsipStaticParamMapper;
import com.asiainfo.fsip.model.ParamModel;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.entity.DepartmentInfo;
import com.asiainfo.mcp.tmc.entity.MiniUserEntity;
import com.asiainfo.mcp.tmc.mapper.MiniOrgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    @Resource
    private FsipStaticParamMapper fsipStaticParamMapper;

    @Resource
    private MiniOrgMapper miniOrgMapper;

    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    @Qualifier("fastRedisTemplate")
    private RedisTemplate fastRedisTemplate;

    @Value("${cache.refresh:1}")
    private String refresh;

    @PostConstruct
    private void init() {
        boolean loadCacheFlag = stringRedisTemplate.opsForValue().setIfAbsent("LOAD_CACHE_FLAG", "1", 20, TimeUnit.HOURS);
        if (loadCacheFlag || "1".equals(refresh)) {
            log.info("begin to load cache data");
            loadParam();
        }
    }

    private void loadParam() {
        List<FsipStaticParamEntity> paramEntities = fsipStaticParamMapper.selectByProp(FsipStaticParamEntity.builder().build());
        if (CollUtil.isEmpty(paramEntities)) {
            return;
        }

        Map<String, Map<String, String>> paramMap = new LinkedHashMap<>();

        for (FsipStaticParamEntity paramEntity : paramEntities) {
            Map<String, String> itemMap = paramMap.get(paramEntity.getAttrType());
            if (itemMap == null) {
                itemMap = new HashMap<>();
                paramMap.put(paramEntity.getAttrType(), itemMap);
            }
            itemMap.put(paramEntity.getAttrCode(), paramEntity.getAttrValue());
        }

        fastRedisTemplate.opsForValue().set(IFsipConstants.FSIP_CACHE_PARAM_MAP, paramMap);
    }

    @Override
    public void refreshParam(String attrType) {
        if (StringUtils.isEmpty(attrType)) {
            loadParam();
            return;
        }

        Object cacheParamMap = fastRedisTemplate.opsForValue().get(IFsipConstants.FSIP_CACHE_PARAM_MAP);
        if (cacheParamMap == null) {
            return;
        }

        List<FsipStaticParamEntity> paramEntities = fsipStaticParamMapper.selectByExtCode(attrType, null);
        if (CollUtil.isEmpty(paramEntities)) {
            return;
        }

        Map<String, Map<String, String>> paramMap = (Map<String, Map<String, String>>) cacheParamMap;
        Map<String, String> itemMap = new HashMap<>();

        for (FsipStaticParamEntity paramEntity : paramEntities) {
            itemMap.put(paramEntity.getAttrCode(), paramEntity.getAttrValue());
        }

        paramMap.put(attrType, itemMap);

        fastRedisTemplate.opsForValue().set(IFsipConstants.FSIP_CACHE_PARAM_MAP, paramMap);
    }

    @Override
    public String getParamValue(String attrType, String attrCode) {
        Map<String, String> itemMap = getParamListByType(attrType);
        if (MapUtil.isEmpty(itemMap)) {
            return null;
        }

        return itemMap.get(attrCode);
    }

    @Override
    public Map<String, String> getParamListByType(String attrType) {
        Map<String, Map<String, String>> paramMap = getParamMap();
        Map<String, String> itemMap = paramMap.get(attrType);
        if (MapUtil.isEmpty(itemMap)) {
            return Collections.emptyMap();
        }

        return itemMap;
    }

    @Override
    public Map<String, Map<String, String>> getParamMap() {
        Object cacheParamMap = fastRedisTemplate.opsForValue().get(IFsipConstants.FSIP_CACHE_PARAM_MAP);
        if (cacheParamMap == null) {
            return Collections.emptyMap();
        }

        Map<String, Map<String, String>> paramMap = (Map<String, Map<String, String>>) cacheParamMap;
        return paramMap;
    }

    @Override
    public String getParamValue(Map<String, Map<String, String>> paramMap, String attrType, String attrCode) {
        Map<String, String> itemMap = paramMap.get(attrType);
        if (MapUtil.isEmpty(itemMap)) {
            return null;
        }

        return itemMap.get(attrCode);
    }

    @Override
    public DepartmentInfo getDepartment(String departId) {
        Object cacheDepartmentMap = fastRedisTemplate.opsForValue().get(IConstants.CACHE_ALL_DEPART_MAP);
        if (cacheDepartmentMap == null) {
            return DepartmentInfo.builder().build();
        }

        Map<String, DepartmentInfo> deptMap = (Map<String, DepartmentInfo>) cacheDepartmentMap;
        return getDepartment(departId, deptMap);
    }

    @Override
    public List<ParamModel> getCompanyList() {
        Map<String, String> cacheOrgMap = getCompanyMap();
        if (CollUtil.isEmpty(cacheOrgMap)) {
            return Collections.emptyList();
        }
        List<ParamModel> paramModelList = new ArrayList<>();
        for (String ldapCode : cacheOrgMap.keySet()) {
            paramModelList.add(ParamModel.builder().code(ldapCode).name(cacheOrgMap.get(ldapCode)).build());
        }
        return paramModelList;
    }

    @Override
    public Map<String, String> getCompanyMap() {
        Object cacheOrgMap = fastRedisTemplate.opsForValue().get(IConstants.CACHE_COMPANY_MAP);
        if (cacheOrgMap == null) {
            return Collections.emptyMap();
        }

        return (Map<String, String>) cacheOrgMap;
    }

    @Override
    public Map<String, String> getDepartmentMap(String companyId) {
        Object cacheDepartmentMap = fastRedisTemplate.opsForValue().get(IConstants.CACHE_DEPARTMENT_MAP);
        if (cacheDepartmentMap == null) {
            return Collections.emptyMap();
        }

        Map<String, Map<String, String>> companyDeptMap = (Map<String, Map<String, String>>) cacheDepartmentMap;
        Map<String, String> deptMap = companyDeptMap.get(companyId);
        if (CollUtil.isEmpty(deptMap)) {
            return Collections.emptyMap();
        }

        return deptMap;
    }

    @Override
    public void refreshParam(String attrType, String attrCode, String attrValue) {
        Map<String, Map<String, String>> paramMap = getParamMap();
        Map<String, String> itemMap = paramMap.get(attrType);
        if(itemMap == null){
            itemMap = new HashMap<>();
        }
        itemMap.put(attrCode, attrValue);
        paramMap.put(attrType, itemMap);

        fastRedisTemplate.opsForValue().set(IFsipConstants.FSIP_CACHE_PARAM_MAP, paramMap);
    }

    @Override
    public Map<String, MiniUserEntity> getEmployeeHrMap() {
        Object cacheEmployeeMap = fastRedisTemplate.opsForValue().get(IConstants.CACHE_EMPLOYEE_HR_CODE_MAP);
        if (cacheEmployeeMap == null) {
            return Collections.emptyMap();
        }

        return (Map<String, MiniUserEntity>)cacheEmployeeMap;
    }

    @Override
    public Map<String, String> getUserId2HrCodeMap() {
        Object cacheEmployeeMap = fastRedisTemplate.opsForValue().get(IConstants.CACHE_EMPLOYEE_USERID_2_HRCODE);
        if (cacheEmployeeMap == null) {
            return Collections.emptyMap();
        }

        return (Map<String, String>) cacheEmployeeMap;
    }

    @Override
    public Map<String, DepartmentInfo> getDepartmentCache() {
        Object cacheDepartmentMap = fastRedisTemplate.opsForValue().get(IConstants.CACHE_ALL_DEPART_MAP);
        if (cacheDepartmentMap == null) {
            return Collections.emptyMap();
        }

        Map<String, DepartmentInfo> departmentMap = (Map<String, DepartmentInfo>) cacheDepartmentMap;
        return departmentMap;
    }

    private DepartmentInfo getDepartment(String deptCode, Map<String, DepartmentInfo> departMap){
        if(departMap.containsKey(deptCode)){
            return departMap.get(deptCode);
        }

        String parentCode = miniOrgMapper.selectParentOrg(deptCode);
        if(StringUtils.isEmpty(parentCode)){
            return DepartmentInfo.builder().build();
        }

        return getDepartment(parentCode, departMap);
    }
}
