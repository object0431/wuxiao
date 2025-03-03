package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.asiainfo.fsip.entity.FsipStaticParamEntity;
import com.asiainfo.fsip.mapper.fsip.FsipStaticParamMapper;
import com.asiainfo.fsip.model.ParamModel;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.fsip.service.ParamService;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ParamServiceImpl implements ParamService {

    @Resource
    private CacheService cacheService;

    @Resource
    private FsipStaticParamMapper fsipStaticParamMapper;

    @Override
    public List<FsipStaticParamEntity> queryParam(FsipStaticParamEntity req, StaffInfo staff) {
        String attrType = req.getAttrType();
        String attrCode = req.getAttrCode();
        if("DEADLINE".equals(attrType) && "CITY".equals(attrCode)){
            attrCode = attrCode.concat(staff.getCompanyId());
            req.setAttrCode(attrCode);
        }
        List<FsipStaticParamEntity> paramList = fsipStaticParamMapper.selectByProp(req);
        if(CollUtil.isEmpty(paramList)){
            return Collections.emptyList();
        }

        return paramList;
    }

    @Override
    public int addParam(FsipStaticParamEntity req, StaffInfo staff) {
        if (StringUtils.isEmpty(req.getAttrType())) {
            throw new BusinessException("9001", "参数类型不能为空！");
        }
        if (StringUtils.isEmpty(req.getAttrValue())) {
            throw new BusinessException("9001", "参数值不能为空！");
        }
        FsipStaticParamEntity maxParamEntity = fsipStaticParamMapper.selectMaxSortByAttrType(req.getAttrType());

        int sort = maxParamEntity == null ? 1 : maxParamEntity.getSort() + 1;
        String attrCode = StringUtils.isBlank(req.getAttrCode()) ? req.getAttrType().concat(String.valueOf(sort)) : req.getAttrCode();

        String attrType = req.getAttrType();
        if("DEADLINE".equals(attrType) && "CITY".equals(attrCode)){
            attrCode = attrCode.concat(staff.getCompanyId());
            req.setAttrCode(attrCode);
        }

        req.setAttrCode(attrCode);
        req.setOperatorId(staff.getMainUserId());
        req.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        req.setState("1");
        req.setSort(sort);

        int ret = fsipStaticParamMapper.insert(req);

        cacheService.refreshParam(req.getAttrType(), req.getAttrCode(), req.getAttrValue());
        return ret;
    }

    @Override
    public int modifyParam(FsipStaticParamEntity req, StaffInfo staff) {
        req.setOperatorId(staff.getHrEmpCode());
        req.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        int ret = fsipStaticParamMapper.updateByAttrCode(req);
        cacheService.refreshParam(req.getAttrType(), req.getAttrCode(), req.getAttrValue());
        return ret;
    }

    @Override
    public int deleteParam(String attrType, List<String> attrCodeList) {
        if(StringUtils.isBlank(attrType)){
            throw new BusinessException("9001", "参数类型不能为空");
        }

        int ret =  fsipStaticParamMapper.deleteByAttrCode(attrType, attrCodeList);
        cacheService.refreshParam(attrType);
        return ret;
    }


    @Override
    public List<ParamModel> queryCompanyList() {
        return cacheService.getCompanyList();
    }

    @Override
    public List<ParamModel> queryDeptList(String companyId, String deptName) {
        Map<String, String> departmentMap = cacheService.getDepartmentMap(companyId);

        if(CollUtil.isEmpty(departmentMap)){
            return Collections.emptyList();
        }

        List<ParamModel> paramModelList = new ArrayList<>();
        for(String deptId : departmentMap.keySet()){
            String value = departmentMap.get(deptId);
            if(StringUtils.isBlank(deptName)){
                paramModelList.add(ParamModel.builder().code(deptId).name(value).build());
            }else if(value.contains(deptName)){
                paramModelList.add(ParamModel.builder().code(deptId).name(value).build());
            }
        }

        return paramModelList;
    }
}
