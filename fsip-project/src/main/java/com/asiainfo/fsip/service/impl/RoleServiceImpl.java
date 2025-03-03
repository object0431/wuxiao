package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FispStaff2RoleEntity;
import com.asiainfo.fsip.entity.FsipStaff2RoleExtend;
import com.asiainfo.fsip.mapper.fsip.FispStaff2RoleMapper;
import com.asiainfo.fsip.model.QueryCommitteeListRsp;
import com.asiainfo.fsip.model.Staff2RoleModel;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.fsip.service.RoleService;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    private FispStaff2RoleMapper fispStaff2RoleMapper;

    @Resource
    private CacheService cacheService;

    @Autowired
    private FsipStaff2RoleExtendService roleExtendService;

    @Override
    public PageInfo<Staff2RoleModel> queryStaffRoleList(PageReq<FispStaff2RoleEntity> req, StaffInfo staff) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());

        FispStaff2RoleEntity reqParam = req.getReqParam();
        if(reqParam == null){
            reqParam = FispStaff2RoleEntity.builder().build();
        }
        if (!"004300".equals(staff.getCompanyId()) && !IFsipConstants.RoleId.ZJRC.equals(reqParam.getRoleId())) {
            reqParam.setCompanyId(staff.getCompanyId());
        }

        return new PageInfo(queryStaffByRoleId(reqParam));
    }

    @Transactional
    @Override
    public void deleteStaffRole(String roleId, List<String> staffIdList) {
        fispStaff2RoleMapper.deleteByRoleIdAndStaffIds(roleId, staffIdList);
        roleExtendService.remove(new LambdaQueryWrapper<FsipStaff2RoleExtend>().eq(FsipStaff2RoleExtend::getRoleId, roleId).in(FsipStaff2RoleExtend::getStaffId, staffIdList));
    }

    @Transactional
    @Override
    public void saveStaffRole(List<Staff2RoleModel> req, StaffInfo staff) {
        String userId = staff.getMainUserId();
        String currTime = DateUtils.getHorizontalDate();

        List<String> staffIdList = new ArrayList<>();


        List<FsipStaff2RoleExtend> extendList = new ArrayList<>();
        List<FispStaff2RoleEntity> staffRoleList = req.parallelStream().map(role -> {
            FispStaff2RoleEntity entity = FispStaff2RoleEntity.builder().operatorId(userId).updateTime(currTime).build();
            BeanUtils.copyProperties(role, entity);

            staffIdList.add(role.getStaffId());
            if (!CollectionUtils.isEmpty(role.getExtendInfos())) {
                role.getExtendInfos().stream().forEach(extend -> {
                    FsipStaff2RoleExtend staff2RoleExtend = new FsipStaff2RoleExtend();
                    BeanUtils.copyProperties(extend, staff2RoleExtend);
                    staff2RoleExtend.setRoleId(role.getRoleId());
                    staff2RoleExtend.setStaffId(role.getStaffId());
                    staff2RoleExtend.setStaffName(role.getStaffName());
                    extendList.add(staff2RoleExtend);
                });
            }

            return entity;
        }).collect(Collectors.toList());


        roleExtendService.remove(new LambdaQueryWrapper<FsipStaff2RoleExtend>().eq(FsipStaff2RoleExtend::getRoleId, req.get(0).getRoleId()).in(FsipStaff2RoleExtend::getStaffId, staffIdList));
        fispStaff2RoleMapper.deleteByRoleIdAndStaffIds(req.get(0).getRoleId(), staffIdList);
        fispStaff2RoleMapper.batchInsert(staffRoleList);
        if (!extendList.isEmpty()) {
            roleExtendService.saveBatch(extendList);
        }
    }

    @Override
    public QueryCommitteeListRsp queryCommitteeList(PageReq<JSONObject> req, StaffInfo staff) {
        JSONObject paramMap = req.getReqParam();
        String achievementType = MapUtil.getStr(paramMap, "achievementType");
        if (StringUtils.isEmpty(achievementType)) {
            throw new BusinessException("请选择需要查询的评委会类型");
        }

        FispStaff2RoleEntity roleEntity = FispStaff2RoleEntity.builder().build();
        if ("CITY".equals(achievementType)) {
            roleEntity.setRoleId("DSPWH");
            roleEntity.setCompanyId(staff.getCompanyId());
        } else {
            roleEntity.setRoleId("SJPWH");
        }

        List<FispStaff2RoleEntity> select = fispStaff2RoleMapper.select(roleEntity);

        QueryCommitteeListRsp rsp = new QueryCommitteeListRsp();
        List<QueryCommitteeListRsp.ZYX_BEAN> zyxList = rsp.getZyxList();
        Map<String, List<FispStaff2RoleEntity>> collect1 = select.stream().collect(Collectors.groupingBy(a -> {
            String attrCode = a.getAttrCode();
            if (StringUtils.isEmpty(attrCode)) {
                a.setAttrValue("未分配专业线");
                return "未分配专业线";
            }
            return attrCode;
        }));
        for (Map.Entry<String, List<FispStaff2RoleEntity>> stringListEntry : collect1.entrySet()) {
            QueryCommitteeListRsp.ZYX_BEAN zyxBean = new QueryCommitteeListRsp.ZYX_BEAN();
            zyxBean.setZyxCode(stringListEntry.getKey());
            zyxBean.setZyxName(stringListEntry.getValue().get(0).getAttrValue());
            zyxList.add(zyxBean);

            List<Staff2RoleModel> staffList = zyxBean.getStaffList();
            for (FispStaff2RoleEntity staff2RoleEntity : stringListEntry.getValue()) {
                Staff2RoleModel staff2RoleModel = new Staff2RoleModel();
                BeanUtils.copyProperties(staff2RoleEntity, staff2RoleModel);
                staffList.add(staff2RoleModel);
            }

        }
        return rsp;
    }

    private List<Staff2RoleModel> queryStaffByRoleId(FispStaff2RoleEntity roleEntity){
        List<Staff2RoleModel> dataList = fispStaff2RoleMapper.selectByProp(roleEntity);
        if (!CollUtil.isEmpty(dataList)) {
            Map<String, String> companyMap = cacheService.getCompanyMap();
            dataList.parallelStream().forEach(item -> {
                item.setCompanyName(companyMap.get(item.getCompanyId()));
            });
        }

        return dataList;
    }
}
