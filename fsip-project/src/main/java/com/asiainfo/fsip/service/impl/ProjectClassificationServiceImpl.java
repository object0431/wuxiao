package com.asiainfo.fsip.service.impl;

import com.alibaba.fastjson.JSON;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.mapper.fsip.FsipLoginInitiationStatMapper;
import com.asiainfo.fsip.mapper.fsip.ProjectStatisticsMapper;
import com.asiainfo.fsip.mapper.tmc.TmcEmployeeMapper;
import com.asiainfo.fsip.model.LoginEmpInfoModel;
import com.asiainfo.fsip.model.LoginStatisticsModel;
import com.asiainfo.fsip.model.ProjectStatisticsProTypeModel;
import com.asiainfo.fsip.model.TimeScopeReq;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.fsip.service.ProjectClassificationService;
import com.asiainfo.fsip.utils.DateUtils;
import com.asiainfo.mcp.tmc.common.annotation.ExcelField;
import com.asiainfo.mcp.tmc.common.entity.DepartmentInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.entity.MiniUserEntity;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 立项分类统计相关 => 服务接口实现类
 **/
@Service
@Slf4j
public class ProjectClassificationServiceImpl implements ProjectClassificationService {

    @Resource
    private ProjectStatisticsMapper projectStatisticsMapper;

    @Resource
    private FsipLoginInitiationStatMapper loginInitiationStatMapper;

    @Resource
    private TmcEmployeeMapper employeeMapper;

    @Resource
    private CacheService cacheService;

    @Override
    public PageInfo<ProjectStatisticsProTypeModel> getProjectStatisticsInfoByType(PageReq<TimeScopeReq> dateMap) {
        PageHelper.startPage(dateMap.getPageNum(), dateMap.getPageSize());
        TimeScopeReq reqParam = dateMap.getReqParam();
        List<ProjectStatisticsProTypeModel> projectClassificationInfo = this.getProjectStatisticsInfoByType(reqParam);
        return new PageInfo<>(projectClassificationInfo);
    }

    @Override
    public List<ProjectStatisticsProTypeModel> getProjectStatisticsInfoByType(TimeScopeReq req) {
        this.checkTimeRange(req.getStartDate(), req.getEndDate());
        req.setType(checkStatisticalType(req.getType()));
        req.setEndDate(req.getEndDate().concat(" 23:59:59"));
        List<ProjectStatisticsProTypeModel> projectStatisticsProType = projectStatisticsMapper.findProjectStatisticsByType(req);
        Map<String, DepartmentInfo> departmentCache = cacheService.getDepartmentCache();
        return builderInfo(projectStatisticsProType,departmentCache);
    }

    @Override
    public Class<?> builderExcelName(String type) throws Exception {
        String typeTmp = this.checkStatisticalType(type);

        Class<ProjectStatisticsProTypeModel> obj = ProjectStatisticsProTypeModel.class;
        Field statType = obj.getDeclaredField("statType");
        ExcelField annotation = statType.getAnnotation(ExcelField.class);
        // 获取该注解，使用动态代理的方式实现修改注解的 name 值
        InvocationHandler handler = Proxy.getInvocationHandler(annotation);
        Field field = handler.getClass().getDeclaredField("memberValues");
        field.setAccessible(true);

        // 根据type修改 ExcelFiled(name) 值
        Map<String,Object> memberValues = (Map<String, Object>) field.get(handler);
        if (IFsipConstants.StaticParamType.XMLX.equalsIgnoreCase(typeTmp)) {
            memberValues.put("name", "项目类型");
        } else if (IFsipConstants.StaticParamType.CXLX.equalsIgnoreCase(typeTmp)) {
            memberValues.put("name", "创新类型");
        }
        log.info("annotation => {}", JSON.toJSONString(annotation));
        return obj;
    }

    @Override
    public PageInfo<LoginStatisticsModel> findLoginStatisticsInfo(PageReq<TimeScopeReq> pageReq) {
        try {
            PageHelper.startPage(pageReq.getPageNum(),pageReq.getPageSize());
            List<LoginStatisticsModel> loginStatisticsInfo = this.findLoginStatisticsInfo(pageReq.getReqParam());
            return new PageInfo<>(loginStatisticsInfo);
        }catch (Exception e){
            log.error("查询登录统计数据失败！查询参数信息 => {}",JSON.toJSONString(pageReq.getReqParam()),e);
            throw new BusinessException("查询登录统计数据失败。"+e.getMessage());
        }
    }

    @Override
    public List<LoginStatisticsModel> findLoginStatisticsInfo(TimeScopeReq timeScopeReq) {
        String startDate = timeScopeReq.getStartDate();
        if (startDate.length()!=DateUtils.yyyyMM.length()
                && !DateUtils.isValidFormat(startDate,DateUtils.yyyyMM)){
            throw new BusinessException("请输入正确月份格式 eg:【yyyyMM】");
        }
        return loginInitiationStatMapper.findByCidDid(timeScopeReq);
    }

    @Override
    public List<LoginEmpInfoModel> findLoginEmpInfo(TimeScopeReq timeScopeReq) {
        this.checkTimeRange(timeScopeReq.getStartDate(), timeScopeReq.getEndDate());

        timeScopeReq.setEndDate(timeScopeReq.getEndDate().concat(" 23:59:59"));

        Map<String, DepartmentInfo> departmentCache = cacheService.getDepartmentCache();

        List<LoginEmpInfoModel> loginEmpInfo = projectStatisticsMapper.findLoginEmpInfo(timeScopeReq);

        // 部门公司名称包装
        loginEmpInfo.forEach(item->{
            DepartmentInfo departmentInfo = departmentCache.get(item.getDeptId());
            if (departmentInfo==null){
                log.info("部门编号：{} 在缓存中不存在 DepartmentInfo 数据",item.getDeptId());
                return;
            }
            item.setDeptName(departmentInfo.getDeptName());
            item.setCompanyName(departmentInfo.getCompanyName());
        });

        // staffName 模糊查询条件不为空，则列表中的 staffName 数据必不为空，不需要做处理
        if (!StringUtils.isBlank(timeScopeReq.getStaffName())) {
            return loginEmpInfo;
        }

        // 筛选 staffName 为空的数据，如果有则进行筛选，没有则直接返回
        List<LoginEmpInfoModel> blankNameList = loginEmpInfo.parallelStream().filter(item -> StringUtils.isBlank(item.getStaffName())).collect(Collectors.toList());

        // 针对 staffName 为空的数据进行处理
        if (!blankNameList.isEmpty()) {
            Map<String, String> userId2HrCodeMap = cacheService.getUserId2HrCodeMap();
            Map<String, MiniUserEntity> employeeHrMap = cacheService.getEmployeeHrMap();
            List<String> retryEmpNameList = new ArrayList<>();

            // 包装数据，1. 先从缓存中查询
            blankNameList.forEach(item -> {
                String hrEmpCode = userId2HrCodeMap.get(item.getLoginAccount());
                if (StringUtils.isBlank(hrEmpCode) || employeeHrMap.get(hrEmpCode) == null) {
                    retryEmpNameList.add(item.getLoginAccount());
                    return;
                }
                item.setStaffName(employeeHrMap.get(hrEmpCode).getStaffName());
            });

            // 2.缓存中没有的再从数据库中查询
            if (!retryEmpNameList.isEmpty()) {
                log.info("登录用户 {} 未在缓存列表中，hrEmpCode is NULL", JSON.toJSONString(retryEmpNameList));
                // 根据 retryEmpNameList 查找数据库列表，防止 in 太多数据导致查询过慢
                if (retryEmpNameList.size() <= 1000) {
                    // 得到集合并转成map
                    Map<String, String> uidEmpNameMap = employeeMapper.selectUidEmpNameByStaffId(retryEmpNameList).stream().collect(Collectors.toMap(MiniUserEntity::getAccountCode, MiniUserEntity::getStaffName, (v1, v2) -> v1));
                    log.info("数据库查询的用户 Map => {}", JSON.toJSONString(uidEmpNameMap));
                    blankNameList.forEach(item -> {
                        if (StringUtils.isBlank(item.getStaffName())) {
                            item.setStaffName(uidEmpNameMap.get(item.getLoginAccount()));
                        }
                    });
                } else {
                    // 如果 retryEmpNameList 太大，可能是数据出现了问题
                    log.info("大量登录用户在缓存中不存在！重试用户名 => {}", JSON.toJSONString(retryEmpNameList));
                }
            }
            // 合并数据，将 blankNameList 与 loginEmpInfo 中 LoginAccount 进行匹配
            // 补充 loginEmpInfo staffName == null 的数据。
            blankNameList.forEach(itemA -> loginEmpInfo.forEach(itemB -> {
                if (itemA.getLoginAccount().equals(itemB.getLoginAccount())) {
                    itemB.setStaffName(itemA.getStaffName());
                }
            }));
        }

        return loginEmpInfo;

    }

    @Override
    public PageInfo<LoginEmpInfoModel> findLoginEmpInfo(PageReq<TimeScopeReq> req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<LoginEmpInfoModel> loginEmpInfos = this.findLoginEmpInfo(req.getReqParam());
        return new PageInfo<>(loginEmpInfos);
    }

    /**
     * 构建部门和公司信息
     */
    private List<ProjectStatisticsProTypeModel> builderInfo(List<ProjectStatisticsProTypeModel> list, Map<String, DepartmentInfo> departmentCache) {
        List<String> retryDeptIdList = new ArrayList<>();
        list.forEach(model -> {
            DepartmentInfo departmentInfo = departmentCache.get(model.getDeptId());
            if (departmentInfo == null) {

                retryDeptIdList.add(model.getDeptId());
                return;
            }
            model.setCompanyName(departmentInfo.getCompanyName());
            model.setDeptName(departmentInfo.getDeptName());
        });
        if (!retryDeptIdList.isEmpty()) {
            log.info("未从缓存中找到公司部门信息：{}", JSON.toJSONString(retryDeptIdList));
            // 尝试取数据中数据
            List<DepartmentInfo> deptInfoList = projectStatisticsMapper.findDeptInfoByDeptId(retryDeptIdList);
            log.info("部门信息重试，数据库查询结果：{}",JSON.toJSONString(deptInfoList));
            list.forEach(item -> {
                if (StringUtils.isBlank(item.getCompanyName()) && StringUtils.isBlank(item.getDeptName())) {
                    deptInfoList.forEach(departmentInfo -> {
                        if (departmentInfo.getDeptId().equals(item.getDeptId())) {
                            item.setCompanyName(departmentInfo.getCompanyName());
                            item.setDeptName(departmentInfo.getDeptName());
                        }
                    });
                }
            });
        }
        return list;
    }

    /**
     * 检查立项分类统计类型是否正确，如果正确返回常量接口中的对应的类型常量
     */
    private String checkStatisticalType(String type) {
        if (type == null) {
            throw new BusinessException("请输入查询立项分类统计类型！");
        }
        if (IFsipConstants.StaticParamType.XMLX.equalsIgnoreCase(type)) {
            return IFsipConstants.StaticParamType.XMLX;
        } else if (IFsipConstants.StaticParamType.CXLX.equalsIgnoreCase(type)) {
            return IFsipConstants.StaticParamType.CXLX;
        } else {
            throw new BusinessException("立项分类统计类型有误！");
        }
    }

    /**
     * 检查时间范围是否合法，如果合法就返回源数据，不合法直接抛错
     */
    private void checkTimeRange(String startDate, String endDate) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            throw new BusinessException("请输入查询时间范围！");
        }
        if (!DateUtils.isValidFormat(startDate,"yyyyMMdd")||!DateUtils.isValidFormat(endDate,"yyyyMMdd")){
            throw new BusinessException("请输入正确的时间范围！格式为：【yyyyMMdd】");
        }
    }
}
