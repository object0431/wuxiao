package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FsipProjectAchievementBaseEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementItemEntity;
import com.asiainfo.fsip.entity.FsipProjectAchievementReviewEntity;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementBaseMapper;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementItemMapper;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementMapper;
import com.asiainfo.fsip.mapper.fsip.FsipProjectAchievementReviewMapper;
import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.fsip.service.FsipProjectAchievementService;
import com.asiainfo.fsip.service.OssService;
import com.asiainfo.fsip.utils.DateUtils;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.TranceNoTool;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FsipProjectAchievementServiceImpl extends ServiceImpl<FsipProjectAchievementMapper, FsipProjectAchievementEntity> implements FsipProjectAchievementService {

    @Resource
    private CacheService cacheService;

    @Resource
    private FsipProjectAchievementMapper fsipProjectAchievementMapper;

    @Resource
    private FsipProjectAchievementBaseMapper fsipProjectAchievementBaseMapper;

    @Resource
    private FsipProjectAchievementItemMapper fsipProjectAchievementItemMapper;

    @Resource
    private FsipProjectAchievementReviewMapper fsipProjectAchievementReviewMapper;

    @Resource
    private TranceNoTool tranceNoTool;

    @Resource
    OssService ossService;

    @Override
    public PageInfo<ProjectAchievementPushSelResp> selPendingRatingList(PageReq<ProjectAchievementPushSelReq> req, StaffInfo staffInfo) throws Exception {
        ProjectAchievementPushSelReq pushSelReq = req.getReqParam();
        String type = pushSelReq.getAchievementType();
        String startDate = pushSelReq.getStartDate();
        String endDate = pushSelReq.getEndDate();
        String isSelCityToProvFlag = "CITY".equals(type) ? "0" : "1";

        if (StringUtils.isNotBlank(startDate)){
            startDate = startDate.trim() +  " 00:00:00";
        }
        if (StringUtils.isNotBlank(endDate)) {
            endDate = endDate.trim() + " 23:59:59";
        }

        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        PageInfo<ProjectAchievementPushSelResp> pageInfo = new PageInfo<>();
        List<ProjectAchievementPushSelResp> retList = fsipProjectAchievementMapper.getPendingRatingSelList(pushSelReq, staffInfo.getCompanyId(), startDate, endDate, isSelCityToProvFlag);
        if (null != retList && !retList.isEmpty()) {
            retList.parallelStream().forEach(item -> {
                item.setInnovationType(cacheService.getParamValue("CXLX", item.getInnovationType()));
                item.setProjectType(cacheService.getParamValue("XMLX", item.getProjectType()));
                item.setApplierCompanyId(cacheService.getCompanyMap().get(item.getApplierCompanyId()));
                item.setApplierDeptId(cacheService.getDepartment(item.getApplierDeptId()).getDeptName());
                item.setAwardLevel(cacheService.getParamValue("AWARD_LEVEL", item.getAwardLevel()));
                item.setAchievementType(cacheService.getParamValue("ACHIEVEMENT_TYPE", item.getAchievementType()));
            });
            pageInfo = PageInfo.of(retList);
        }
        return pageInfo;
    }

    @Override
    public ProjectAchievementPushDetailResp selPendingRatingDetail(String achievementId, String type) {

        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        FsipProjectAchievementEntity fsipProjectAchievementEntity = fsipProjectAchievementMapper.selectById(achievementId);

        FsipProjectAchievementBaseEntity fsipProjectAchievementBaseEntity = fsipProjectAchievementBaseMapper.selectById(achievementId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ACHIEVEMENT_ID", achievementId);

        List<FsipProjectAchievementItemEntity> itemList = fsipProjectAchievementItemMapper.selectByMap(params);

        List<FsipProjectAchievementReviewEntity> reviewList = fsipProjectAchievementReviewMapper.selectByMap(params);


        Map<String, Object> columnMap = new HashMap<String, Object>();
        columnMap.put("ACHIEVEMENT_ID", achievementId);
        String itemName = "";
//        if ("004300".equals(staffInfo.getCompanyId())) {
//            itemName = "PROV_ATTACHMENT";
//        } else {
//            itemName = "CITY_ATTACHMENT";
//        }
        if ("PROV".equals(type)) {
            itemName = "PROV_ATTACHMENT";
        }
        if ("CITY".equals(type)) {
            itemName = "CITY_ATTACHMENT";
        }

        columnMap.put("ITEM_TYPE", itemName);
        List<FsipProjectAchievementItemEntity> itemEntities = fsipProjectAchievementItemMapper.selectByMap(columnMap);

        List<String> attachmentList = new ArrayList<String>();
        if (null != itemEntities && !itemEntities.isEmpty()) {
            for (FsipProjectAchievementItemEntity entity : itemEntities) {
                attachmentList.add(entity.getItemValue());
            }
        }

        return ProjectAchievementPushDetailResp.builder()
                .fsipProjectAchievementEntity(fsipProjectAchievementEntity)
                .fsipProjectAchievementBaseEntity(fsipProjectAchievementBaseEntity)
                .fsipProjectAchievementItemList(itemList)
                .fsipProjectAchievementReviewEntity(reviewList)
                .attachmentList(attachmentList)
                .build();
    }

    @Override
    public ProjectAchievementRatingResp rating(ProjectAchievementRatingReq req, StaffInfo staffInfo) {
        List<ProjectAchievementRatingReq.ProjectAchievementRatingBean> reqList = req.getReqParam();

        if (CollUtil.isEmpty(reqList)) {
            return ProjectAchievementRatingResp.builder().respCode("9999").respMsg("未正确传入请求参数").build();
        }

        List<ProjectAchievementRatingResp.ProjectAchievementRatingRespBean> errorRetList
                = new ArrayList<>();
        for (ProjectAchievementRatingReq.ProjectAchievementRatingBean achievementRatingBean : reqList) {
            FsipProjectAchievementEntity fpab
                    = fsipProjectAchievementMapper.selectById(achievementRatingBean.getAchievementId());

            FsipProjectAchievementBaseEntity fpabe
                    = fsipProjectAchievementBaseMapper.selectById(achievementRatingBean.getAchievementId());

            String companyId = staffInfo.getCompanyId();
            ProjectAchievementRatingResp.ProjectAchievementRatingRespBean ratingBean;

            String errorMsg = "";
            if (fpab == null) {
                errorMsg = String.format("根据成果编码：%s未查询到数据", achievementRatingBean.getAchievementId());
            }
            if (!"1".equals(fpab.getCityToProvFlag()) && !companyId.equals(fpab.getApplierCompanyId())) {
                errorMsg = String.format("您没有权限对成果编码：%s进行评级", achievementRatingBean.getAchievementId());
            }

            if (!"00".equals(fpab.getStatus())) {
                errorMsg = String.format("成果编码：%s目前处于%s中，不能进行评级", achievementRatingBean.getAchievementId()
                        , cacheService.getParamValue("STATE", fpabe.getStatus()));
            }

            if (!StringUtils.isEmpty(errorMsg)) {
                ratingBean = new ProjectAchievementRatingResp.ProjectAchievementRatingRespBean();
                ratingBean.setAchievementId(achievementRatingBean.getAchievementId());
                ratingBean.setErrorMsg(errorMsg);
                errorRetList.add(ratingBean);
            }
        }
        if (!errorRetList.isEmpty()) {
            return ProjectAchievementRatingResp.builder().respCode("9999").reqData(errorRetList).build();
        }

        for (ProjectAchievementRatingReq.ProjectAchievementRatingBean achievementRatingBean : reqList) {
            FsipProjectAchievementEntity fpab
                    = fsipProjectAchievementMapper.selectById(achievementRatingBean.getAchievementId());
            FsipProjectAchievementBaseEntity selFpabe = fsipProjectAchievementBaseMapper.selectById(achievementRatingBean.getAchievementId());
            //如果查询到有，则肯定代表是从地市到省分的成果
            if (null != selFpabe) {
                Calendar cal = Calendar.getInstance();
                if ("PROV".equals(req.getType())) {
                    int year = cal.get(Calendar.YEAR);
                    selFpabe.setAchievementType("PROV");
                    selFpabe.setApplyYearmon(year + "");
                    selFpabe.setAwardLevel(achievementRatingBean.getLevel().replace("CITY", "PROV"));
                } else {
                    selFpabe.setAchievementType("CITY");
                    selFpabe.setAwardLevel(achievementRatingBean.getLevel());
                    selFpabe.setStatus("00");
                }
                fsipProjectAchievementBaseMapper.updateById(selFpabe);
            } else {
                String itemName = "";
                FsipProjectAchievementBaseEntity fpabe = new FsipProjectAchievementBaseEntity();
                fpabe.setAchievementId(fpab.getAchievementId());

                if ("PROV".equals(req.getType())) {
                    fpabe.setAchievementType("PROV");
                    itemName = "PROV_ATTACHMENT";
                }
                if ("CITY".equals(req.getType())) {
                    fpabe.setAchievementType("CITY");
                    itemName = "CITY_ATTACHMENT";
                }

                fpabe.setApplierCompanyId(fpab.getApplierCompanyId());
                fpabe.setApplierId(fpab.getApplierId());
                fpabe.setApplierDeptId(fpab.getApplierDeptId());
                fpabe.setApplyDate(fpab.getApplyDate());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
                String formatDate = sdf.format(fpab.getApplyDate());
                fpabe.setApplyYearmon(formatDate);

                fpabe.setAwardLevel(achievementRatingBean.getLevel());
                fpabe.setStatus("00");
                fsipProjectAchievementBaseMapper.insert(fpabe);

                List<ProjectAchievementRatingReq.AttachmentBean> attachmentList = req.getAttachmentList();
                if (null != attachmentList && !attachmentList.isEmpty()) {
                    for (int i = 0; i < attachmentList.size(); i++) {
                        FsipProjectAchievementItemEntity itemEntity = new FsipProjectAchievementItemEntity();
                        itemEntity.setAchievementId(fpab.getAchievementId());
                        itemEntity.setItemType(itemName);
                        itemEntity.setItemCode("ATTACHMENT_" + (i + 1));
                        itemEntity.setItemName("附件");
                        itemEntity.setItemValue(attachmentList.get(i).getName());
                        itemEntity.setSort(i + 1);
                        fsipProjectAchievementItemMapper.insert(itemEntity);
                    }
                }
            }
        }
        return ProjectAchievementRatingResp.builder().respCode("0000").respMsg("已成功进行评级").build();
    }


    @Override
    public PageInfo<ProjectAchievementPushSelResp> selRatingList(PageReq<ProjectAchievementPushSelReq> req, StaffInfo staffInfo) {
        ProjectAchievementPushSelReq reqParam = req.getReqParam();
        if (reqParam == null) {
            reqParam = ProjectAchievementPushSelReq.builder().build();
        }

        reqParam.setAchievementType(IFsipConstants.AchievementType.CITY);

        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        PageInfo<ProjectAchievementPushSelResp> pageInfo = new PageInfo<>();
        List<ProjectAchievementPushSelResp> retList = fsipProjectAchievementBaseMapper.getRatingList(reqParam, staffInfo.getCompanyId());
        if (!CollUtil.isEmpty(retList)) {
            retList.parallelStream().forEach(item -> {
                item.setInnovationType(cacheService.getParamValue("CXLX", item.getInnovationType()));
                item.setProjectType(cacheService.getParamValue("XMLX", item.getProjectType()));
                item.setAchievementType(cacheService.getParamValue("ACHIEVEMENT_TYPE", item.getAchievementType()));
                item.setAwardLevel(cacheService.getParamValue("AWARD_LEVEL", item.getAwardLevel()));
                item.setApplierCompanyId(cacheService.getCompanyMap().get(item.getApplierCompanyId()));
                item.setApplierDeptId(cacheService.getDepartment(item.getApplierDeptId()).getDeptName());
            });
            pageInfo = PageInfo.of(retList);
        }
        return pageInfo;
    }

    @Override
    public PageInfo<ProjectAchievementStatisticsResp> queryAchievementStatistics(PageReq<ProjectAchievementStatisticsReq> req, StaffInfo staffInfo) {
        ProjectAchievementStatisticsReq reqParam = req.getReqParam();
        String applierCompanyId = staffInfo.getCompanyId();
        //如果是省公司的账号查看，则都可以查看，如果是地市分公司的，只能查询属于自己分公司数据
        if ("004300".equals(staffInfo.getCompanyId())) {
            if (StringUtils.isNotEmpty(reqParam.getApplierCompanyId())) {
                applierCompanyId = reqParam.getApplierCompanyId();
            } else {
                applierCompanyId = "";
            }
        }
        if (req.getPageSize() != Integer.MAX_VALUE){
            PageHelper.startPage(req.getPageNum(), req.getPageSize());
        }
        List<ProjectAchievementStatisticsResp> retList
                = fsipProjectAchievementMapper.getProjectAchievementStatistics(reqParam, applierCompanyId);

        if (CollUtil.isEmpty(retList)) {
            return new PageInfo<>();
        }

        retList.parallelStream().forEach(item -> {
                    item.setApplierDeptId(cacheService.getDepartment(item.getApplierDeptId()).getDeptName());
                    item.setApplierCompanyId(cacheService.getCompanyMap().get(item.getApplierCompanyId()));
                }
        );

        return new PageInfo<>(retList);
    }


    @Override
    public NationalProjectAchievementResp nationalProjectAchievementPush(NationalProjectAchievementReq req) {

        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        String projectId = tranceNoTool.getTranceNoSecondUnique("HN", "yyMMddHHmmssSSS", 3);

        FsipProjectAchievementEntity fpae = new FsipProjectAchievementEntity();
        fpae.setAchievementId(projectId);
        fpae.setProjectName(req.getProjectName());
        fpae.setApplierCompanyId(staffInfo.getCompanyId());
        fpae.setApplierId(staffInfo.getMainUserId());
        fpae.setApplierName(staffInfo.getEmpName());
        fpae.setApplierDeptId(staffInfo.getDeptId());
        fpae.setStatus("00");
        fpae.setApplyDate(new Date());
        fpae.setBackImage(req.getImageTag());

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
        String yyyy = "";
        try {
            yyyy = sdf.format(new Date());
        } catch (Exception e) {
        }

        FsipProjectAchievementBaseEntity fpabe = new FsipProjectAchievementBaseEntity();
        fpabe.setAchievementId(projectId);
        fpabe.setAchievementType(IFsipConstants.AchievementType.NATIONAL);
        fpabe.setApplierCompanyId(staffInfo.getCompanyId());
        fpabe.setApplierId(staffInfo.getMainUserId());
        fpabe.setApplierDeptId(staffInfo.getDeptId());
        fpabe.setApplierName(staffInfo.getEmpName());
        fpabe.setApplyDate(new Date());
        fpabe.setStatus("00");
        fpabe.setApplyYearmon(yyyy);
        fpabe.setMainCreateName(req.getMainCreateName());
        fpabe.setOtherCreateName(req.getOtherCreateName());
        fpabe.setAwardsProjectNameLevel(req.getAwardsProjectNameLevel());

        FsipProjectAchievementItemEntity fpaie = new FsipProjectAchievementItemEntity();
        fpaie.setAchievementId(projectId);
        fpaie.setItemType("ACHIEVEMENT");
        fpaie.setItemCode("CGJJ");
        fpaie.setItemName("成果简介");
        fpaie.setSort(1);
        fpaie.setItemValue(req.getProjectIntroduce());

        int i1 = fsipProjectAchievementMapper.insert(fpae);
        int i2 = fsipProjectAchievementBaseMapper.insert(fpabe);
        int i3 = fsipProjectAchievementItemMapper.insert(fpaie);


        List<NationalProjectAchievementReq.AttachmentBean> attachmentList = req.getAttachmentList();
        if (null != attachmentList && !attachmentList.isEmpty()) {
            for (int i = 0; i < attachmentList.size(); i++) {
                FsipProjectAchievementItemEntity itemEntity = new FsipProjectAchievementItemEntity();
                itemEntity.setAchievementId(projectId);
                itemEntity.setItemType("NATIONAL_ATTACHMENT");
                itemEntity.setItemCode("ATTACHMENT_" + (i + 1));
                itemEntity.setItemName("附件");
                itemEntity.setItemValue(attachmentList.get(i).getName());
                itemEntity.setSort(i + 1);
                fsipProjectAchievementItemMapper.insert(itemEntity);
            }
        }

        if (i1 > 0 && i2 > 0 && i3 > 0) {
            return NationalProjectAchievementResp.builder().respCode("0000").respMsg("新增成功").projectId(projectId).build();
        }
        return NationalProjectAchievementResp.builder().respCode("8888").respMsg("新增失败").build();
    }

    @Override
    public PageInfo<NationalProjectAchievementSelResp> selNationalProjectAchievementList(PageReq<NationalProjectAchievementSelReq> req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        PageInfo<NationalProjectAchievementSelResp> pageInfo = new PageInfo<NationalProjectAchievementSelResp>();
        List<NationalProjectAchievementSelResp> retList = fsipProjectAchievementBaseMapper.getNationalProjectAchievementList(req.getReqParam());
        if (null != retList && !retList.isEmpty()) {
            retList.parallelStream().forEach(item -> {
                item.setAchievementType(cacheService.getParamValue("ACHIEVEMENT_TYPE", item.getAchievementType()));
                item.setApplierDeptId(cacheService.getDepartment(item.getApplierDeptId()).getDeptName());
                item.setApplierCompanyId(cacheService.getCompanyMap().get(item.getApplierCompanyId()));
            });
            pageInfo = PageInfo.of(retList);
        }
        return pageInfo;
    }

    @Override
    public NationalProjectAchievementSelResp selNationalProjectAchievementDetail(String achievementId) {
        if (StringUtils.isEmpty(achievementId)) {
            return null;
        }
        NationalProjectAchievementSelReq req = new NationalProjectAchievementSelReq();
        req.setAchievementId(achievementId);
        List<NationalProjectAchievementSelResp> retList = fsipProjectAchievementBaseMapper.getNationalProjectAchievementList(req);

        if (null != retList && !retList.isEmpty()) {
            NationalProjectAchievementSelResp resp = retList.get(0);

            Map<String, Object> columnMap = new HashMap<String, Object>();
            columnMap.put("ACHIEVEMENT_ID", achievementId);
            columnMap.put("ITEM_TYPE", "NATIONAL_ATTACHMENT");
            List<FsipProjectAchievementItemEntity> itemEntities = fsipProjectAchievementItemMapper.selectByMap(columnMap);

            if (null != itemEntities && !itemEntities.isEmpty()) {
                List<String> attachmentList = new ArrayList<String>();
                for (FsipProjectAchievementItemEntity entity : itemEntities) {
                    attachmentList.add(entity.getItemValue());
                }
                resp.setAttachmentList(attachmentList);
            }
            return resp;
        }
        return null;
    }

    @Override
    public List<ProjectAchievementPushSelResp> selCity2ProvAuditAchievement(City2ProvAuditAchievementReq req) {
        String pendingCode = req.getPendingCode();
        if (StringUtils.isEmpty(pendingCode)) {
            return null;
        }
        List<ProjectAchievementPushSelResp> city2ProvAuditAchievementList = fsipProjectAchievementBaseMapper.getCity2ProvAuditAchievement(pendingCode);
        if (null != city2ProvAuditAchievementList && !city2ProvAuditAchievementList.isEmpty()) {
            city2ProvAuditAchievementList.parallelStream().forEach(item -> {
                item.setInnovationType(cacheService.getParamValue("CXLX", item.getInnovationType()));
                item.setProjectType(cacheService.getParamValue("XMLX", item.getProjectType()));
                item.setAchievementType(cacheService.getParamValue("ACHIEVEMENT_TYPE", item.getAchievementType()));
                item.setApplierDeptId(cacheService.getDepartment(item.getApplierDeptId()).getDeptName());
                item.setApplierCompanyId(cacheService.getCompanyMap().get(item.getApplierCompanyId()));
                item.setStatus(cacheService.getParamValue("STATE", item.getStatus()));
            });
            return city2ProvAuditAchievementList;
        }
        return null;
    }


    @Override
    public NationalProjectAchievementResp delNationalProjectAchievement(String achievementId) {
        if (StringUtils.isEmpty(achievementId)) {
            return NationalProjectAchievementResp.builder().respCode("8888").respMsg("删除错误，请传入正确参数").build();
        }
        NationalProjectAchievementSelReq req = new NationalProjectAchievementSelReq();
        req.setAchievementId(achievementId);
        List<NationalProjectAchievementSelResp> retList = fsipProjectAchievementBaseMapper.getNationalProjectAchievementList(req);

        if (null != retList && !retList.isEmpty()) {
            ossService.deleteFile(retList.get(0).getBackImage());
            fsipProjectAchievementBaseMapper.deleteById(achievementId);
            fsipProjectAchievementMapper.deleteById(achievementId);
            Map<String, Object> columnMap = new HashMap<String, Object>();
            columnMap.put("ACHIEVEMENT_ID", achievementId);
            List<FsipProjectAchievementItemEntity> itemEntities = fsipProjectAchievementItemMapper.selectByMap(columnMap);
            if (null != itemEntities && !itemEntities.isEmpty()) {
                //先删除OSS附件
                for (FsipProjectAchievementItemEntity item : itemEntities) {
                    if ("NATIONAL_ATTACHMENT".equals(item.getItemType())) {
                        ossService.deleteFile(item.getItemValue());
                    }
                }
                fsipProjectAchievementItemMapper.deleteByMap(columnMap);
            }
            return NationalProjectAchievementResp.builder().respCode("0000").respMsg("删除成功").build();
        }
        return NationalProjectAchievementResp.builder().respCode("8888").respMsg("删除错误，根据参数未查询到数据").build();
    }


}
