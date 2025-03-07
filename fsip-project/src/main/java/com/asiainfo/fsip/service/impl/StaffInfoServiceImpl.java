package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ServiceException;
import com.asiainfo.fsip.config.VerifyProperties;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FispStaff2RoleEntity;
import com.asiainfo.fsip.entity.FsipApprovalNodeEntity;
import com.asiainfo.fsip.entity.FsipApprovalParamEntity;
import com.asiainfo.fsip.mapper.fsip.FispStaff2RoleMapper;
import com.asiainfo.fsip.mapper.fsip.FsipApprovalNodeMapper;
import com.asiainfo.fsip.mapper.fsip.FsipApprovalParamMapper;
import com.asiainfo.fsip.mapper.tmc.TmcEmployeeMapper;
import com.asiainfo.fsip.model.ApprovalNodeModel;
import com.asiainfo.fsip.model.OrganizerStrucRsp;
import com.asiainfo.fsip.model.QryEmployeeReq;
import com.asiainfo.fsip.model.Staff2RoleModel;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.fsip.service.StaffInfoService;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.entity.DepartmentInfo;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.entity.MiniUserEntity;
import com.asiainfo.mcp.tmc.mapper.MiniOrgMapper;
import com.asiainfo.mcp.tmc.mapper.MiniUserMapper;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StaffInfoServiceImpl implements StaffInfoService {

    @Resource
    @Qualifier("fastRedisTemplate")
    private RedisTemplate fastRedisTemplate;

    @Resource
    @Qualifier("stringRedisTemplate")
    private RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    private MiniOrgMapper miniOrgMapper;

    @Resource
    private MiniUserMapper miniUserMapper;

    @Resource
    private TmcEmployeeMapper tmcEmployeeMapper;

    @Resource
    private FsipApprovalParamMapper fsipApprovalParamMapper;

    @Resource
    private FsipApprovalNodeMapper fsipApprovalNodeMapper;

    @Resource
    private FispStaff2RoleMapper fispStaff2RoleMapper;

    @Resource
    private CacheService cacheService;

    @Resource
    private VerifyProperties verifyProperties;

    @Override
    public PageInfo<OrganizerStrucRsp.EmployeeChildrenBean> queryStaffInfoList(PageReq<QryEmployeeReq> req) {
        QryEmployeeReq qryEmployeeReq = req.getReqParam();
        if (qryEmployeeReq == null) {
            qryEmployeeReq = QryEmployeeReq.builder().build();
        }

        log.info(JSONObject.toJSONString(qryEmployeeReq));

        List<MiniUserEntity> employeeInfoList;
        if (StringUtils.isNotBlank(qryEmployeeReq.getDeptCode())) {
            List<String> orgCodeList = getSubDeptList(qryEmployeeReq.getDeptCode());

            PageHelper.startPage(req.getPageNum(), req.getPageSize());
            employeeInfoList = tmcEmployeeMapper.selectByProp(qryEmployeeReq.getStaffName(), qryEmployeeReq.getSerialNumber(), orgCodeList);
        } else if (StringUtils.isNotBlank(qryEmployeeReq.getOrgCode())) {
            List<String> orgCodeList = getSubDeptList(qryEmployeeReq.getOrgCode());

            PageHelper.startPage(req.getPageNum(), req.getPageSize());
            employeeInfoList = tmcEmployeeMapper.selectByProp(qryEmployeeReq.getStaffName(), qryEmployeeReq.getSerialNumber(), orgCodeList);
        } else {
            PageHelper.startPage(req.getPageNum(), req.getPageSize());
            employeeInfoList = tmcEmployeeMapper.selectByProp(qryEmployeeReq.getStaffName(), qryEmployeeReq.getSerialNumber(), null);
        }

        log.info(JSONObject.toJSONString(employeeInfoList));

        PageInfo<MiniUserEntity> page = PageInfo.of(employeeInfoList);

        Map<String, DepartmentInfo> departmentMap = loadAllDepartment();

        List<OrganizerStrucRsp.EmployeeChildrenBean> employeeList = new ArrayList<>();
        for (MiniUserEntity empEntity : employeeInfoList) {
            OrganizerStrucRsp.EmployeeChildrenBean employeeChildrenBean = OrganizerStrucRsp.EmployeeChildrenBean.builder()
                    .staffName(empEntity.getStaffName()).emailAddress(empEntity.getEmail())
                    .hrEmpCode(empEntity.getStaffId()).identityNumber(empEntity.getPsptId())
                    .mainUserId(empEntity.getAccountCode()).mobileTel(empEntity.getSerialNumber())
                    .deptId(empEntity.getDeptId()).deptName(empEntity.getDeptName())
                    .sex(empEntity.getSex()).mainPosition(empEntity.getPostDescription())
                    .build();

            loadDepartmentInfo(employeeChildrenBean, departmentMap);

            employeeList.add(employeeChildrenBean);
        }

        PageInfo<OrganizerStrucRsp.EmployeeChildrenBean> pageInfo = new PageInfo<>(employeeList);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());

        return pageInfo;
    }

    @Override
    public List<String> getSubDeptList(String parentOrgCode) {
        List<String> orgCodeList = new ArrayList<>();
        orgCodeList.add(parentOrgCode);
        getSubDeptList(orgCodeList, orgCodeList);

        Set<String> orgCodes = new HashSet<>(orgCodeList);
        orgCodeList.clear();
        orgCodeList.addAll(orgCodes);
        return orgCodeList;
    }

    private void getSubDeptList(List<String> deptCodeList, List<String> parentCodeList) {
        List<String> orgEntityList = miniOrgMapper.selectSubOrgCode(parentCodeList);
        if (CollUtil.isEmpty(orgEntityList)) {
            return;
        }

        deptCodeList.addAll(orgEntityList);
        getSubDeptList(deptCodeList, orgEntityList);
    }

    @Override
    public List<StaffInfo> getStaffList(String staffId) {
        List<MiniUserEntity> userEntityList = miniUserMapper.selectByUserId(staffId);
        if (CollUtil.isEmpty(userEntityList)) {
            return Collections.emptyList();
        }

        StaffInfo staffInfo = StaffInfoUtil.getStaff();

        List<StaffInfo> staffInfoList = new ArrayList<>();
        for (MiniUserEntity userEntity : userEntityList) {
            StaffInfo staff = StaffInfoUtil.copyProperties(userEntity);
            if (userEntity.getId() == staffInfo.getId()) {
                staff.setCurrent("1");
            } else {
                staff.setCurrent("0");
            }
            staffInfoList.add(staff);
        }
        return staffInfoList;
    }

    @Override
    public StaffInfo switchIdentity(long id) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        MiniUserEntity miniUserEntity = miniUserMapper.selectByIdAndUserId(id, staffInfo.getMainUserId());
        if (miniUserEntity == null) {
            throw new ServiceException("未找到对应的工号信息");
        }

        staffInfo = StaffInfoUtil.copyProperties(miniUserEntity);
        DepartmentInfo departmentInfo = cacheService.getDepartment(miniUserEntity.getDeptId());

        staffInfo.setDeptName(departmentInfo.getDeptName());
        staffInfo.setCompanyId(departmentInfo.getCompanyId());
        staffInfo.setCompanyName(departmentInfo.getCompanyName());

        String redisKey = "USER:" + staffInfo.getMainUserId();
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(staffInfo), 60 * 60 * 2, TimeUnit.SECONDS);

        return staffInfo;
    }

    @Override
    public List<ApprovalNodeModel> getApprovalList(String approvalType, String extId, StaffInfo staffInfo) throws Exception {
        List<FsipApprovalParamEntity> approvalParamEntityList = fsipApprovalParamMapper.selectApprovalByType(approvalType);
        if (CollectionUtils.isEmpty(approvalParamEntityList)) {
            return Collections.emptyList();
        }

        Map<String, FsipApprovalNodeEntity> approvalNodeMap = getApprovalNodeInfo(approvalType, extId);

        String nodeState = IConstants.State.BMLDSP;

        List<ApprovalNodeModel> approvalModelList = new ArrayList<>();
        for (FsipApprovalParamEntity paramEntity : approvalParamEntityList) {
            String officer = paramEntity.getApprOfficer();

            ApprovalNodeModel approvalNodeModel = ApprovalNodeModel.builder().apprOfficer(paramEntity.getApprOfficer())
                    .nodeCode(paramEntity.getNodeCode()).nodeName(paramEntity.getNodeName())
                    .sort(paramEntity.getSort()).apprNumber(paramEntity.getApprNumber()).build();

            FsipApprovalNodeEntity approvalNode = approvalNodeMap.get(paramEntity.getNodeCode());

            if (approvalNode != null && "02".equals(approvalNode.getNodeState())) {
                continue;
            }

            String extValue = paramEntity.getExtValue();
            if (!StringUtils.isEmpty(extValue) && !extValue.contains(staffInfo.getCompanyId())) {
                continue;
            }

            List<MiniUserEntity> officerList;
            // 部门领导
            if (IFsipConstants.ApprovalLevel.APPROVAL_LEVEL_DEPT_LEADER.equals(officer)) {
                officerList = getDeptLeader(officer, staffInfo);
            } else {
                officerList = getManagerByRoleId(officer, staffInfo);
            }

            List<ApprovalNodeModel.Officer> officers = convertOfficer(officerList);

            if (CollectionUtils.isEmpty(officers)) {
                continue;
            }

            approvalNodeModel.setNodeState(nodeState);
            approvalNodeModel.setOfficerList(officers);

            nodeState = paramEntity.getNodeState();

            approvalModelList.add(approvalNodeModel);
        }
        return approvalModelList;
    }

    @Override
    public DepartmentInfo getDepartment(String deptCode, Map<String, DepartmentInfo> departMap) {
        if(departMap.containsKey(deptCode)){
            return departMap.get(deptCode);
        }

        String parentCode = miniOrgMapper.selectParentOrg(deptCode);
        if(StringUtils.isEmpty(parentCode)){
            return DepartmentInfo.builder().build();
        }

        return getDepartment(parentCode, departMap);
    }

    private void loadDepartmentInfo(OrganizerStrucRsp.EmployeeChildrenBean userInfo, Map<String, DepartmentInfo> departmentMap) {
        try {
            if (CollUtil.isEmpty(departmentMap)) {
                throw new BusinessException("未获取到本部和公司信息");
            }

            DepartmentInfo departmentInfo = departmentMap.get(userInfo.getDeptId());
            if (departmentInfo != null) {
                userInfo.setCompanyId(departmentInfo.getCompanyId());
                userInfo.setCompanyName(departmentInfo.getCompanyName());
                return;
            }

            String parentOrgCode = miniOrgMapper.selectParentOrg(userInfo.getDeptId());
            if (org.springframework.util.StringUtils.isEmpty(parentOrgCode)) {
                throw new BusinessException("未获取到本部和公司信息");
            }

            departmentInfo = departmentMap.get(parentOrgCode);
            userInfo.setDeptId(parentOrgCode);

            if (departmentInfo == null) {
                loadDepartmentInfo(userInfo, departmentMap);
            } else {
                userInfo.setDeptName(departmentInfo.getDeptName());
                userInfo.setCompanyId(departmentInfo.getCompanyId());
                userInfo.setCompanyName(departmentInfo.getCompanyName());
            }
        } catch (Exception e) {
            log.error("Could not load department info, userInfo = " + JSONObject.toJSONString(userInfo) + ", error = " + e);
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

    private Map<String, FsipApprovalNodeEntity> getApprovalNodeInfo(String apprType, String extId) {
        if (StringUtils.isEmpty(extId)) {
            return Collections.emptyMap();
        }

        List<FsipApprovalNodeEntity> approvalNodeEntities = fsipApprovalNodeMapper.selectApprovalNode(apprType, extId);
        if (CollUtil.isEmpty(approvalNodeEntities)) {
            return Collections.emptyMap();
        }

        Map<String, FsipApprovalNodeEntity> approvalNodeMap = new HashMap<>();

        for (FsipApprovalNodeEntity nodeEntity : approvalNodeEntities) {
            FsipApprovalNodeEntity tmcApprovalNode = approvalNodeMap.get(nodeEntity.getNodeCode());
            if (tmcApprovalNode == null) {
                approvalNodeMap.put(nodeEntity.getNodeCode(), nodeEntity);
            } else if ("02".equals(nodeEntity.getNodeState())) {
                tmcApprovalNode.setNodeState(nodeEntity.getNodeState());
            }
        }

        return approvalNodeMap;
    }

    /**
     * 查询部门领导
     */
    private List<MiniUserEntity> getDeptLeader(String officer, StaffInfo staffInfo) throws Exception {
        List<MiniUserEntity> officerList = getManagerByRoleId(officer, staffInfo);
        if (!CollUtil.isEmpty(officerList)) {
            return filterOneself(staffInfo, officerList);
        }
        officerList = tmcEmployeeMapper.selectDeptLeader(staffInfo.getMdmDeptCode());
        if (!CollUtil.isEmpty(officerList)) {
            return filterOneself(staffInfo, officerList);
        }

        officerList = tmcEmployeeMapper.selectDeptLeader(staffInfo.getDeptId());
        if (!CollUtil.isEmpty(officerList)) {
            return filterOneself(staffInfo, officerList);
        }
        return Collections.emptyList();
    }

    public List<MiniUserEntity> getManagerByRoleId(String roleId, StaffInfo staffInfo) throws Exception {
        FispStaff2RoleEntity entity = FispStaff2RoleEntity.builder().roleId(roleId).companyId(staffInfo.getCompanyId()).build();
        if(IFsipConstants.RoleId.BMJL.equals(roleId)){
            entity.setDeptId(staffInfo.getDeptId());
        }
        log.info("entity =" + JSONObject.toJSONString(entity));

        List<Staff2RoleModel> staff2RoleModelList = fispStaff2RoleMapper.selectByProp(entity);
        if (CollUtil.isEmpty(staff2RoleModelList)) {
            log.error("Could not find data by entity = " + JSONObject.toJSONString(entity));
            return Collections.emptyList();
        }

        List<String> staffIdList = staff2RoleModelList.parallelStream().map(item -> item.getStaffId()).collect(Collectors.toList());
        List<MiniUserEntity> officerList = tmcEmployeeMapper.selectByStaffId(staffIdList);

        if (CollUtil.isEmpty(officerList)) {
            log.error("未找到审批人员信息，请联系管理员配置，" + JSONObject.toJSONString(staffInfo));
            return Collections.emptyList();
        }

        List<MiniUserEntity> miniUserEntities = filterOneself(staffInfo, officerList);
        return miniUserEntities;
    }

    private List<MiniUserEntity> filterOneself(StaffInfo staffInfo, List<MiniUserEntity> officerList) {
        if (CollUtil.isEmpty(officerList)) {
            return officerList;
        }

        if("1".equals(verifyProperties.getFilterOneself())){
            for (MiniUserEntity userEntity : officerList) {
                if (staffInfo.getMainUserId().equals(userEntity.getAccountCode())) {
                    return Collections.emptyList();
                }
            }
        }

        return officerList;
    }

    private List<ApprovalNodeModel.Officer> convertOfficer(List<MiniUserEntity> userEntityList) {
        if (CollectionUtils.isEmpty(userEntityList)) {
            return Collections.emptyList();
        }

        Set<ApprovalNodeModel.Officer> officerList = new HashSet<>();
        for (MiniUserEntity userEntity : userEntityList) {
            ApprovalNodeModel.Officer officer = ApprovalNodeModel.Officer.builder().staffId(userEntity.getAccountCode())
                    .staffName(userEntity.getStaffName()).mobilePhone(userEntity.getSerialNumber()).build();

            officerList.add(officer);
        }

        return new ArrayList<>(officerList);
    }
}
