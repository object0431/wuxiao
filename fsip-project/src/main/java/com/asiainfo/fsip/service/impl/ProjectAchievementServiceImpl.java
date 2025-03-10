package com.asiainfo.fsip.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.client.TmcRestClient;
import com.asiainfo.fsip.config.VerifyProperties;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.*;
import com.asiainfo.fsip.mapper.fsip.*;
import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.*;
import com.asiainfo.fsip.utils.ValidateUtil;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.consts.ReturnCode;
import com.asiainfo.mcp.tmc.common.entity.DepartmentInfo;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingUpEntity;
import com.asiainfo.mcp.tmc.common.exception.BaseException;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.DateUtils;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.common.util.TranceNoTool;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectAchievementServiceImpl implements ProjectAchievementService {
    @Resource
    private FsipProjectAchievementService fsipProjectAchievementService;
    @Resource
    private FsipProjectAchievementItemService achievementItemService;
    @Resource
    private FsipAppendixService appendixService;
    @Resource
    private ApprovalService approvalService;
    @Resource
    private FlowLogService flowLogService;
    @Resource
    private FsipExpertAdviceMapper expertAdviceMapper;
    @Resource
    private FsipProjectAchievementReviewService reviewService;

    @Resource
    private FsipProjectAchievementBaseService fsipProjectAchievementBaseService;

    @Resource
    private FsipApprovalNodeMapper approvalNodeMapper;
    @Resource
    private PendingTaskService pendingTaskService;
    @Resource
    private TranceNoTool tranceNoTool;
    @Resource
    private CacheService cacheService;
    @Resource
    private TmcRestClient restClient;

    @Resource
    private VerifyProperties verifyProperties;

    @Resource
    private FsipProjectAchievementMapper fsipProjectAchievementMapper;

    @Resource
    private FsipProjectAchievementItemMapper fsipProjectAchievementItemMapper;

    @Resource
    private FsipProjectAchievementBaseMapper fsipProjectAchievementBaseMapper;

    @Resource
    private FsipProjectAchievementReviewMapper fsipProjectAchievementReviewMapper;

    @Resource
    private ParamService paramService;

    @Override
    public FsipProjectAchievementEntity saveProject(ProjectAchievementModel projectModel, String operateType, StaffInfo staffInfo) {
        // 校验数据
        validateModel(projectModel);
        FsipProjectAchievementEntity projectAchievement;
        if (ObjectUtils.isEmpty(projectModel.getProjectId())) {
            projectAchievement = new FsipProjectAchievementEntity();
            projectAchievement.setAchievementId(tranceNoTool.getTranceNoSecondUnique("HN", "yyMMddHHmmssSSS", 3));
            projectAchievement.setApplierId(staffInfo.getMainUserId());
            projectAchievement.setApplierName(staffInfo.getEmpName());
            projectAchievement.setApplierCompanyId(staffInfo.getCompanyId());
            projectAchievement.setApplierDeptId(staffInfo.getDeptId());
            projectAchievement.setApplyDate(new Date());
            projectAchievement.setStatus(operateType);
            convert(projectModel, projectAchievement);
        } else {
            projectAchievement = fsipProjectAchievementService.getById(projectModel.getProjectId());

            if (ObjectUtils.isEmpty(projectAchievement)) {
                throw new BusinessException("500", "修改的项目成果不存在");
            }
            if (!IFsipConstants.Status.ZC.equals(projectAchievement.getStatus()) && !IFsipConstants.Status.TH.equals(projectAchievement.getStatus()) && !IFsipConstants.Status.CH.equals(projectAchievement.getStatus()) && !"11".equals(projectAchievement.getStatus()) && !"12".equals(projectAchievement.getStatus())){
                throw new BusinessException("500", "当前项目状态无法进行修改");
            }

            convert(projectModel, projectAchievement);
        }

        String projectId = projectAchievement.getAchievementId();
        List<FsipProjectAchievementItemEntity> itemList = new ArrayList<>();
        projectModel.getItemList().forEach(iCourse -> {
            FsipProjectAchievementItemEntity item = new FsipProjectAchievementItemEntity();
            item.setAchievementId(projectId);
            convert(iCourse, item);
            itemList.add(item);
        });

        // 删除目前关联的属性表
        LambdaQueryWrapper<FsipProjectAchievementItemEntity> uw = Wrappers.lambdaQuery();
        uw.eq(FsipProjectAchievementItemEntity::getAchievementId, projectId);
        achievementItemService.remove(uw);

        // 插入新的属性表
        achievementItemService.saveBatch(itemList);

        // 更新附件表
        updateAppendix(projectId, projectModel.getAppendixList());

        // 更新项目
        fsipProjectAchievementService.saveOrUpdate(projectAchievement);
        return projectAchievement;
    }

    @Override
    public BaseRsp<Object> handleProject(ProjectAchievementAddReq req, StaffInfo staffInfo) {
        //检查是否超期申报
        checkDeadLine(staffInfo, "CITY");
        ProjectAchievementModel projectModel = req.getProjectModel();
        //校验参数
        if (projectModel == null) {
            return RspHelp.fail(ValidateUtil.VALIDATE_ERROR_CODE, "项目信息[projectModel]不能为空");
        }

        List<FileModel> appendixList = projectModel.getAppendixList();
        if (appendixList == null || appendixList.isEmpty()) {
            return RspHelp.fail(ValidateUtil.VALIDATE_ERROR_CODE, "项目信息-附件列表[appendixList]不能为空");
        }
        if (req.getApplyReq() == null) {
            return RspHelp.fail(ValidateUtil.VALIDATE_ERROR_CODE, "办理信息[applyReq]不能为空");
        }
        ValidateUtil.ValidateResult validateResult = ValidateUtil.requestValidate(projectModel);
        if (!validateResult.isPass()) {
            return RspHelp.fail(ValidateUtil.VALIDATE_ERROR_CODE, validateResult.getMessage());
        }

        validateResult = ValidateUtil.requestValidate(req.getApplyReq());
        if (!validateResult.isPass()) {
            return RspHelp.fail(ValidateUtil.VALIDATE_ERROR_CODE, validateResult.getMessage());
        }
        if (CollUtil.isEmpty(req.getApplyReq().getApprovalNodeList())) {
            return RspHelp.fail(ValidateUtil.VALIDATE_ERROR_CODE, "审批步骤列表不能为空[approvalInfoList]不能为空");
        }

        for (ApprovalApplyReq.ApprovalNode approvalInfo : req.getApplyReq().getApprovalNodeList()) {
            validateResult = ValidateUtil.requestValidate(approvalInfo);
            if (!validateResult.isPass()) {
                return RspHelp.fail(ValidateUtil.VALIDATE_ERROR_CODE, validateResult.getMessage());
            }
        }

        //保存信息到立项表
        FsipProjectAchievementEntity project = this.saveProject(projectModel, IFsipConstants.Status.BMLDSP, staffInfo);

        // 保存 申请信息到 flow 表
        String state = IFsipConstants.Status.BMLDSP;
        String apprNodeCode = IConstants.NodeCode.BMLDSP;
        state = StringUtils.isNotBlank(state) ? state : "01";
        //更新项目状态
        String pendingCode = tranceNoTool.getTimeId(IFsipConstants.TaskType.CGSQ);
        project.setPendingCode(pendingCode);
        project.setStatus(state);
        project.setApprNodeCode(apprNodeCode);
        if (StringUtils.isNotEmpty(project.getCityToProvFlag())) {
            req.getApplyReq().getApprovalNodeList().parallelStream().forEach(item -> item.setCity2Prov(project.getCityToProvFlag()));
        }
        //成果申请-项目名称-申请人-申请时间
        StringBuilder titleBuilder = new StringBuilder("成果申请-").append(project.getProjectName()).append("-").append(staffInfo.getEmpName()).append("-").append(DateUtils.getDateString());
        PendingModel pendingModel = PendingModel.builder().operType(IConstants.OperType.BL).approvalReq(req.getApplyReq()).pendingCode(pendingCode).pendingTitle(titleBuilder.toString()).taskType(IFsipConstants.TaskType.CGSQ).approvalId(project.getAchievementId()).pendingUrl(verifyProperties.getCgsqApprovalUrl()).taskStatus(IFsipConstants.Status.BMLDSP).build();
        log.info(project.getAchievementId() + "调用代办数据为：" + JSONObject.toJSONString(pendingCode));
        Long dingTaskId = pendingTaskService.applyApproval(pendingModel, staffInfo);
        if (dingTaskId != null) {
            project.setDingTaskId(dingTaskId);
        }
        LambdaQueryWrapper<FsipProjectAchievementEntity> traineeEntityLambdaQueryWrapper = Wrappers.lambdaQuery();
        traineeEntityLambdaQueryWrapper.eq(FsipProjectAchievementEntity::getAchievementId, project.getAchievementId());
        fsipProjectAchievementService.update(project, traineeEntityLambdaQueryWrapper);
        return RspHelp.success(project.getAchievementId());
    }

    @Override
    public NextApprovalNodeModel getNextApprovalInfo(ProjectAchievementModel projectModel) {
        // 查询此项目数据
        FsipProjectAchievementEntity project = fsipProjectAchievementService.getById(projectModel.getProjectId());

        if (ObjectUtils.isEmpty(project)) {
            throw new BusinessException("500", "无此项目ID");
        }

        if (!IConstants.State.BMLDSP.equals(project.getStatus()) && !IConstants.State.PXGLYSP.equals(project.getStatus()) && !IConstants.State.RLFZRSP.equals(project.getStatus())) {
            throw new BusinessException("500", "项目非审批状态");
        }
        // 查询流程配置所有节点
        FsipApprovalNodeEntity nodeEntity = approvalService.getNextApprovalNode(IConstants.TaskType.PXLX, project.getAchievementId(), project.getApprNodeCode());

        if (nodeEntity == null) {
            log.error("the next approval node is null, apprNodeCode = " + project.getApprNodeCode());
            return NextApprovalNodeModel.builder().hasNext(false).build();
        }

        return NextApprovalNodeModel.builder().hasNext(true).nodeCode(nodeEntity.getNodeCode()).nodeName(nodeEntity.getNodeName()).staffId(nodeEntity.getDealStaffId()).staffName(nodeEntity.getDealStaffName()).position(nodeEntity.getPosition()).build();
    }

    @Override
    public ProjectAchievementModel getProject(String projectId) {
        FsipProjectAchievementEntity project = fsipProjectAchievementService.getById(projectId);
        if (ObjectUtils.isEmpty(project)) {
            throw new BusinessException("500", "查无数据");
        }
        ProjectAchievementModel projectModel = new ProjectAchievementModel();

        List<FsipProjectAchievementItemEntity> itemList = achievementItemService.lambdaQuery().eq(FsipProjectAchievementItemEntity::getAchievementId, projectId).orderByAsc(FsipProjectAchievementItemEntity::getSort).list();
        List<FlowLogModel> logList = flowLogService.queryFlowLogById(IFsipConstants.TaskType.CGSQ, projectId);
        convert(project, itemList, projectModel, logList);

        return projectModel;
    }

    @Override
    public PageInfo<ProjectAchievementModel> searchProject(PageReq<ProjectAchievementSearchModel> pageReq, StaffInfo staffInfo) {
        ProjectAchievementSearchModel searchModel = pageReq.getReqParam();
        //查询成果等级参数
        FsipStaticParamEntity paramEntity = new FsipStaticParamEntity();
        paramEntity.setAttrType("CGLX");
        List<FsipStaticParamEntity> params = paramService.queryParam(paramEntity, null);
        Map<String, String> cglxMap = params.stream().collect(Collectors.toMap(FsipStaticParamEntity::getAttrCode, FsipStaticParamEntity::getAttrValue));
        if (pageReq.getPageSize() > 100) {
            throw new BusinessException("500", "单页查询数量太大");
        }
        if (searchModel != null && StringUtils.isNotBlank(searchModel.getAchievementType()) && !cglxMap.containsKey(searchModel.getAchievementType())) {
            throw new BusinessException("500", "成果等级参数错误");
        }

        LambdaQueryChainWrapper<FsipProjectAchievementEntity> lambdaQuery = fsipProjectAchievementService.lambdaQuery();
        if (!ObjectUtils.isEmpty(searchModel.getProjectName())) {
            lambdaQuery.like(FsipProjectAchievementEntity::getProjectName, searchModel.getProjectName());
        }
        if (!ObjectUtils.isEmpty(searchModel.getState())) {
            lambdaQuery.eq(FsipProjectAchievementEntity::getStatus, searchModel.getState());
        }
        if (!ObjectUtils.isEmpty(searchModel.getProjectType())) {
            lambdaQuery.eq(FsipProjectAchievementEntity::getProjectType, searchModel.getProjectType());
        }
        if (!ObjectUtils.isEmpty(searchModel.getInnovationType())) {
            lambdaQuery.eq(FsipProjectAchievementEntity::getInnovationType, searchModel.getInnovationType());
        }
        if (!ObjectUtils.isEmpty(searchModel.getApplierCompanyId())) {
            lambdaQuery.eq(FsipProjectAchievementEntity::getApplierCompanyId, searchModel.getApplierCompanyId());
        }
        if (!ObjectUtils.isEmpty(searchModel.getApplierDeptId())) {
            lambdaQuery.eq(FsipProjectAchievementEntity::getApplierDeptId, searchModel.getApplierDeptId());
        }
        if (!ObjectUtils.isEmpty(searchModel.getIsOwn()) && "1".equals(searchModel.getIsOwn())) {
            lambdaQuery.eq(FsipProjectAchievementEntity::getApplierId, staffInfo.getMainUserId());
        }
        if (!ObjectUtils.isEmpty(searchModel.getBenefit())) {
            String benefit[] = searchModel.getBenefit().split("-");
            if(benefit.length == 2) {
                BigDecimal benefitMin = genBigDecimal(benefit[0].trim());
                BigDecimal benefitMax = genBigDecimal(benefit[1].trim());
                if(null!=benefitMin) {
                    lambdaQuery.ge(FsipProjectAchievementEntity::getBenefit, benefitMin);
                }
                if(null!=benefitMax) {
                    lambdaQuery.le(FsipProjectAchievementEntity::getBenefit, benefitMax);
                }
            }
        }
        if(!ObjectUtils.isEmpty(searchModel.getSjcgsp())) {
            lambdaQuery.eq(FsipProjectAchievementEntity::getSjcgspStatus, searchModel.getSjcgsp());
        }
        if (StringUtils.isNotBlank(searchModel.getAchievementType()) || StringUtils.isNotBlank(searchModel.getAwardLevel())) {
            LambdaQueryChainWrapper<FsipProjectAchievementBaseEntity> achievementBaseLambdaQuery= fsipProjectAchievementBaseService.lambdaQuery();
            if (StringUtils.isNotBlank(searchModel.getAchievementType())) {
                achievementBaseLambdaQuery.eq(FsipProjectAchievementBaseEntity::getAchievementType, searchModel.getAchievementType());
            }
            if (StringUtils.isNotBlank(searchModel.getAwardLevel())) {
                achievementBaseLambdaQuery.eq(FsipProjectAchievementBaseEntity::getAwardLevel, searchModel.getAwardLevel());
            }
            List<FsipProjectAchievementBaseEntity> baseList = achievementBaseLambdaQuery.list();
            if (CollectionUtils.isEmpty(baseList)) {
                return PageInfo.of(new ArrayList<>());
            } else {
                lambdaQuery.in(FsipProjectAchievementEntity::getAchievementId, baseList.parallelStream().map(FsipProjectAchievementBaseEntity::getAchievementId).collect(Collectors.toList()));
            }
        }
        if (StringUtils.isBlank(searchModel.getAchievementType())) {
            List<FsipProjectAchievementBaseEntity> baseList = fsipProjectAchievementBaseService.lambdaQuery().eq(FsipProjectAchievementBaseEntity::getAchievementType, IFsipConstants.AchievementType.NATIONAL).list();
            if (!ObjectUtils.isEmpty(baseList)) {
                lambdaQuery.notIn(FsipProjectAchievementEntity::getAchievementId, baseList.parallelStream().map(FsipProjectAchievementBaseEntity::getAchievementId).collect(Collectors.toList()));
            }
        }
        lambdaQuery.orderByDesc(FsipProjectAchievementEntity::getApplyDate);

        PageHelper.startPage(pageReq.getPageNum(), pageReq.getPageSize());
        PageInfo<FsipProjectAchievementEntity> projectPage = PageInfo.of(lambdaQuery.list());
        Map<String, String> achievementTypeMap;
        Map<String, String> awardLevelMap;
        Map<String, String> achievementScoreMap;
        List<String> achievementIds = projectPage.getList().stream().map(FsipProjectAchievementEntity::getAchievementId).collect(Collectors.toList());
        if (!achievementIds.isEmpty()) {
            List<FsipProjectAchievementBaseEntity> list = fsipProjectAchievementBaseService.lambdaQuery().in(FsipProjectAchievementBaseEntity::getAchievementId, achievementIds).list();
            achievementTypeMap = list.stream().collect(Collectors.toMap(FsipProjectAchievementBaseEntity::getAchievementId, FsipProjectAchievementBaseEntity::getAchievementType));
            awardLevelMap = list.stream().collect(Collectors.toMap(FsipProjectAchievementBaseEntity::getAchievementId, FsipProjectAchievementBaseEntity::getAwardLevel));

            //省级评分/市级评分
            List<AchievementScoreVo> achievementScoreVos = fsipProjectAchievementReviewMapper.findAchievementScore(achievementIds);
            achievementScoreMap = achievementScoreVos.stream().collect(Collectors.toMap(AchievementScoreVo::getAchievementKey, AchievementScoreVo::getAvgScore));
        } else {
            achievementTypeMap = new HashMap<>();
            achievementScoreMap = new HashMap<>();
            awardLevelMap = new HashMap<>();
        }

        List<ProjectAchievementModel> projectModelList = new ArrayList<>();
        projectPage.getList().forEach(it -> {
            ProjectAchievementModel projectModel = new ProjectAchievementModel();
            List<FsipProjectAchievementItemEntity> itemList = achievementItemService.lambdaQuery().eq(FsipProjectAchievementItemEntity::getAchievementId, it.getAchievementId()).orderByAsc(FsipProjectAchievementItemEntity::getSort).list();
            convert(it, itemList, projectModel, null);
            projectModelList.add(projectModel);
            projectModel.setCityScore(achievementScoreMap.get(it.getAchievementId()+"-CITY"));
            projectModel.setProvScore(achievementScoreMap.get(it.getAchievementId()+"-PROV"));
            projectModel.setAchievementType(achievementTypeMap.get(it.getAchievementId()));
            projectModel.setAchievementTypeName(cglxMap.get(projectModel.getAchievementType()));
            projectModel.setAwardLevel(awardLevelMap.get(it.getAchievementId()));
            projectModel.setAwardLevelName(cacheService.getParamValue("AWARD_LEVEL", projectModel.getAwardLevel()));
        });
        PageInfo<ProjectAchievementModel> pageInfo = PageInfo.of(projectModelList);
        pageInfo.setTotal(projectPage.getTotal());
        pageInfo.setSize(projectPage.getSize());
        pageInfo.setPageNum(projectPage.getPageNum());
        pageInfo.setPages(projectPage.getPages());
        return pageInfo;
    }

    private BigDecimal genBigDecimal(String benefit) {
        try {
            return new BigDecimal(benefit);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, List<String>> delProjects(String[] projectIds) {
        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        List<FsipProjectAchievementEntity> projectList = fsipProjectAchievementService.lambdaQuery().in(FsipProjectAchievementEntity::getAchievementId, projectIds).list();
        projectList.stream().forEach(it -> {
            if (staffInfo.getMainUserId().equals(it.getApplierId()) && (IFsipConstants.Status.TH.equals(it.getStatus()) || IFsipConstants.Status.ZC.equals(it.getStatus()) || IFsipConstants.Status.CH.equals(it.getStatus()))) {//暂存 退回  撤回 可以被删除
                //TODO 删除逻辑校验，删除关联的信息
                fsipProjectAchievementService.removeById(it.getAchievementId());
                successList.add(it.getAchievementId());
            } else {
                failList.add(it.getAchievementId());
            }

        });

        Map<String, List<String>> map = new HashMap<>();
        map.put("successList", successList);
        map.put("failList", failList);
        return map;
    }

    @Override
    public String confirmProject(ProjectAchievementModel projectModel) {
        return null;
    }

    @Override
    public void expertAdvice(String projectId, String suggestion, StaffInfo staffInfo) {
        FsipProjectAchievementEntity project = fsipProjectAchievementService.getById(projectId);
        if (ObjectUtils.isEmpty(project)) {
            throw new BusinessException("500", "查无数据");
        }
        FsipExpertAdviceEntity expertAdviceEntity = FsipExpertAdviceEntity.builder().targetId(projectId).targetType(IFsipConstants.TaskType.CGSQ).status("0").applierId(staffInfo.getMainUserId()).applierName(staffInfo.getEmpName()).applierCompanyId(staffInfo.getCompanyId()).applierDeptId(staffInfo.getDeptId()).reqTime(new Date()).suggestion(suggestion).build();
        expertAdviceMapper.insert(expertAdviceEntity);
    }

    @Override
    public void expertAdviceScore(String expertAdviceId, float score, StaffInfo staffInfo) {
        FsipExpertAdviceEntity expertAdviceEntity = expertAdviceMapper.selectById(Integer.parseInt(expertAdviceId));
        if (ObjectUtils.isEmpty(expertAdviceEntity) || !"0".equals(expertAdviceEntity.getStatus())) {
            throw new BusinessException("500", "查无数据");
        }
        FsipProjectAchievementEntity project = fsipProjectAchievementService.getById(expertAdviceEntity.getTargetId());
        if (ObjectUtils.isEmpty(project)) {
            throw new BusinessException("500", "查无数据");
        }
        if (!staffInfo.getMainUserId().equals(expertAdviceEntity.getApplierId())) {
            throw new BusinessException("500", "非操作员无法评分");
        }
        expertAdviceEntity.setStatus("1");
        expertAdviceEntity.setRespTime(new Date());
        expertAdviceEntity.setScore(score);
        expertAdviceMapper.updateById(expertAdviceEntity);
    }

    @Override
    public void achievementReview(AchievementReviewReq req, StaffInfo staffInfo) {
        if (ObjectUtils.isEmpty(req.getPendingCode()) || ObjectUtils.isEmpty(req.getStatus())) {
            throw new BusinessException("500", "参数异常");
        }
        if (ObjectUtils.isEmpty(req.getScoreNodeList())) {
            throw new BusinessException("500", "参数异常，评分节点不能为空");
        }
        if ("00".equals(req.getStatus())) {
            List<FsipApprovalNodeEntity> nodeEntityList = approvalNodeMapper.selectApprovalNodeByPendingCode(req.getPendingCode());
            if (req.getScoreNodeList().size() != nodeEntityList.size()) {
                throw new BusinessException("8888", "还有项目未评分，无法提交");
            }
        }
        List<String> ids = req.getScoreNodeList().stream().map(AchievementReviewReq.ScoreNode::getProjectId).collect(Collectors.toList());
        List<FsipProjectAchievementEntity> projects = fsipProjectAchievementService.listByIds(ids);
        List<String> city2Prov = projects.parallelStream().filter(t -> "1".equals(t.getCityToProvFlag())).map(FsipProjectAchievementEntity::getAchievementId).collect(Collectors.toList());

        LambdaQueryChainWrapper<FsipProjectAchievementReviewEntity> lambdaQuery = reviewService.lambdaQuery();
        lambdaQuery.eq(FsipProjectAchievementReviewEntity::getStatus, IFsipConstants.Status.FINISH);
        lambdaQuery.eq(FsipProjectAchievementReviewEntity::getJudgesId, staffInfo.getMainUserId());
        lambdaQuery.in(FsipProjectAchievementReviewEntity::getAchievementId, ids);
        List<FsipProjectAchievementReviewEntity> list = lambdaQuery.list();
        if (!CollectionUtils.isEmpty(city2Prov) && !CollectionUtils.isEmpty(list)) {  //过滤掉市转省 在市级评审的数据
            list = list.parallelStream().filter(t -> city2Prov.contains(t.getAchievementId()) && "1".equals(t.getCityToProvFlag())).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(list)) {
            throw new BusinessException("8888", "您已评分过，无法再评分");
        }
        for (AchievementReviewReq.ScoreNode scoreNode : req.getScoreNodeList()) {
            FsipProjectAchievementEntity project = projects.parallelStream().filter(t -> t.getAchievementId().equals(scoreNode.getProjectId())).collect(Collectors.toList()).get(0);
            LambdaQueryWrapper<FsipProjectAchievementReviewEntity> lambdaQuery2 = Wrappers.lambdaQuery();
            lambdaQuery2.eq(FsipProjectAchievementReviewEntity::getJudgesId, staffInfo.getMainUserId());
            lambdaQuery2.eq(FsipProjectAchievementReviewEntity::getAchievementId, project.getAchievementId());
            if (!ObjectUtils.isEmpty(project.getCityToProvFlag()) && "1".equals(project.getCityToProvFlag())) {
                lambdaQuery2.eq(FsipProjectAchievementReviewEntity::getCityToProvFlag, "1");
            }
            reviewService.remove(lambdaQuery2);  //先删除
            if (!ObjectUtils.isEmpty(scoreNode.getItemScoreList())) {
                List<FsipProjectAchievementReviewEntity> list2 = new ArrayList<>();
                scoreNode.getItemScoreList().forEach(t -> {
                    FsipProjectAchievementReviewEntity reviewEntity = FsipProjectAchievementReviewEntity.builder().achievementId(scoreNode.getProjectId()).judgesId(staffInfo.getMainUserId()).judgesName(staffInfo.getEmpName()).judgesTime(new Date()).score(t.getScore()).status(req.getStatus()).cityToProvFlag(project.getCityToProvFlag()).itemCode(t.getItemCode()).itemName(t.getItemName()).build();
                    list2.add(reviewEntity);
                });
                reviewService.saveOrUpdateBatch(list2);
            }
            if ("00".equals(req.getStatus())) {
                //插入轨迹表
                flowLogService.addFlowLog(FsipFlowLogEntity.builder().flowType(IFsipConstants.TaskType.CGSQ).extId(project.getAchievementId()).nodeCode(IConstants.NodeCode.PSWYHPS).nodeName(IConstants.NodeCodeName.PSWYHPS).nodeState("04").dealStaffId(staffInfo.getMainUserId()).dealStaffName(staffInfo.getEmpName()).build());
            }
        }
        //更新附件
        updateAppendix(req.getPendingCode(), req.getAppendixList());

        if ("00".equals(req.getStatus())) {
            updatePendingStatus(req.getPendingCode());  //更新OA状态
            for (FsipProjectAchievementEntity project : projects) {
                //判断是否完成
                checkProjectApprove(project);
            }
        }
    }

    @Override
    public void recallProject(String projectId) {
        FsipProjectAchievementEntity project = fsipProjectAchievementService.getById(projectId);
        if (ObjectUtils.isEmpty(project)) {
            throw new BusinessException("500", "查无数据");
        }
        if (!IFsipConstants.Status.BMLDSP.equals(project.getStatus())) {
            throw new BusinessException("500", "该成果申报状态不支持撤回");
        }
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        if (!project.getApplierId().equals(staffInfo.getMainUserId())) {
            throw new BusinessException("500", "非本人操作，无法撤回");
        }
        project.setStatus(IFsipConstants.Status.CH);
        fsipProjectAchievementService.updateById(project);
        // 删除审批信息  及流程  评分信息
    }

    @Override
    public PageInfo<ProjectAchievementModel> zgSearchProject(PageReq<Map<String, String>> pageReq, StaffInfo staffInfo) {
        if (pageReq.getPageSize() > 100) {
            throw new BusinessException("500", "单页查询数量太大");
        }
        Map<String, String> params = pageReq.getReqParam();
        if (MapUtil.isEmpty(params)) {
            throw new BusinessException("500", "成果类型不能为空");
        }
        String pendingCode = null;
        if (!ObjectUtils.isEmpty(params) && params.containsKey("pendingCode")) {
            pendingCode = params.get("pendingCode");
        }
        String achievementType = params.get("achievementType");
        PageHelper.startPage(pageReq.getPageNum(), pageReq.getPageSize());
        LambdaQueryChainWrapper<FsipProjectAchievementEntity> lambdaQuery = fsipProjectAchievementService.lambdaQuery();
        if (StringUtils.isEmpty(pendingCode)) {
            lambdaQuery.eq(FsipProjectAchievementEntity::getStatus, IFsipConstants.Status.PSWYH);
            if ("PROV".equals(achievementType)) {
                lambdaQuery.and(wq -> wq.eq(FsipProjectAchievementEntity::getCityToProvFlag, "1"));  //市转省
            } else {
                lambdaQuery.eq(FsipProjectAchievementEntity::getApplierCompanyId, staffInfo.getCompanyId());
                lambdaQuery.and(wq -> wq.isNull(FsipProjectAchievementEntity::getCityToProvFlag).or().ne(FsipProjectAchievementEntity::getCityToProvFlag, "1"));  //市转省
            }
        } else {  //评完分也可看自己的
            List<FsipApprovalNodeEntity> nodeList = approvalNodeMapper.selectApprovalNodeByPendingCode(pendingCode);
            if (!ObjectUtils.isEmpty(nodeList)) {
                nodeList = nodeList.parallelStream().filter(t -> t.getDealStaffId().equals(staffInfo.getMainUserId())).collect(Collectors.toList());
            }
            if (!ObjectUtils.isEmpty(nodeList)) {
                lambdaQuery.in(FsipProjectAchievementEntity::getAchievementId, nodeList.stream().map(t -> t.getApprId()).collect(Collectors.toList()));
            } else {
                throw new BusinessException("500", "未查到该待办相关评分项目信息");
            }
        }
        lambdaQuery.orderByDesc(FsipProjectAchievementEntity::getApplyDate);

        PageInfo<FsipProjectAchievementEntity> projectPage = PageInfo.of(lambdaQuery.list());
        List<ProjectAchievementModel> projectModelList = new ArrayList<>();
        final String pendingCode1 = pendingCode;
        projectPage.getList().forEach(it -> {
            ProjectAchievementModel projectModel = new ProjectAchievementModel();
            List<FsipProjectAchievementItemEntity> itemList = achievementItemService.lambdaQuery().eq(FsipProjectAchievementItemEntity::getAchievementId, it.getAchievementId()).orderByAsc(FsipProjectAchievementItemEntity::getSort).list();
            convert(it, itemList, projectModel, null);
            if (StringUtils.isNotEmpty(pendingCode1)) {
                LambdaQueryChainWrapper<FsipProjectAchievementReviewEntity> lambdaQuery2 = reviewService.lambdaQuery();
                lambdaQuery2.eq(FsipProjectAchievementReviewEntity::getAchievementId, it.getAchievementId());
                lambdaQuery2.eq(FsipProjectAchievementReviewEntity::getJudgesId, staffInfo.getMainUserId());
                if (StringUtils.isNotEmpty(it.getCityToProvFlag()) && "1".equals(it.getCityToProvFlag())) {
                    lambdaQuery2.eq(FsipProjectAchievementReviewEntity::getCityToProvFlag, "1");
                }
                List<FsipProjectAchievementReviewEntity> reviewEntityList = lambdaQuery2.list();
                if (!ObjectUtils.isEmpty(reviewEntityList)) {
                    projectModel.getItemList().forEach(t -> {
                        List<FsipProjectAchievementReviewEntity> entity = reviewEntityList.parallelStream().filter(f -> StringUtils.isNotEmpty(f.getItemCode()) && f.getItemCode().equals(t.getItemCode())).collect(Collectors.toList());
                        if (!ObjectUtils.isEmpty(entity)) {
                            t.setScore(String.valueOf(entity.get(0).getScore()));
                        }
                    });
                }
            }
            projectModelList.add(projectModel);
        });
        PageInfo<ProjectAchievementModel> pageInfo = PageInfo.of(projectModelList);
        pageInfo.setTotal(projectPage.getTotal());
        pageInfo.setSize(projectPage.getSize());
        pageInfo.setPageNum(projectPage.getPageNum());
        pageInfo.setPages(projectPage.getPages());
        return pageInfo;

    }

    @Override
    public PageInfo<ProjectAchievementModel> queryReviewList(PageReq<AchievementPushQryReq> pageReq, StaffInfo staffInfo) {
        AchievementPushQryReq qryReq = pageReq.getReqParam();

        log.info("qryReq = " + JSONObject.toJSONString(qryReq));
        String pendingCode = qryReq.getPendingCode();
        String achievementType = qryReq.getAchievementType();

        Map<String, String> achievementScoreMap;

        PageHelper.startPage(pageReq.getPageNum(), pageReq.getPageSize());

        List<FsipProjectAchievementEntity> dataList;
        if (StringUtils.isEmpty(pendingCode)) {
            qryReq.setCity2Prov("PROV".equals(achievementType) ? "1" : "0");
            if ("CITY".equals(achievementType)) {
                qryReq.setCompanyId(staffInfo.getCompanyId());
            }

            dataList = "1".equals(qryReq.getQueryType()) ? fsipProjectAchievementMapper.selectPushedReviewByProp(qryReq) : fsipProjectAchievementMapper.selectPendingPushByProp(qryReq);

            if(null!=dataList && dataList.size()>0){
                List<String> achievementIds = dataList.stream().map(FsipProjectAchievementEntity::getAchievementId).collect(Collectors.toList());
                List<AchievementScoreVo> achievementScoreVos = fsipProjectAchievementReviewMapper.findAchievementScore(achievementIds);
                achievementScoreMap = achievementScoreVos.stream().collect(Collectors.toMap(AchievementScoreVo::getAchievementKey, AchievementScoreVo::getAvgScore));
            } else {
                achievementScoreMap = new HashMap<>();
            }
        } else {
            achievementScoreMap = new HashMap<>();
            qryReq.setDealStaffId(staffInfo.getMainUserId());
            dataList = fsipProjectAchievementMapper.selectPendingCommitByProp(qryReq);
        }

        if (CollUtil.isEmpty(dataList)) {
            return new PageInfo<>();
        }

        List<ProjectAchievementModel> projectModelList = dataList.parallelStream().map(it -> {
            ProjectAchievementModel projectModel = new ProjectAchievementModel();
            List<FsipProjectAchievementItemEntity> itemList = fsipProjectAchievementItemMapper.selectByAchievementAndJudgeId(it.getAchievementId(), staffInfo.getMainUserId(), it.getCityToProvFlag());

            convert(it, itemList, projectModel, null);
            if(StringUtils.isEmpty(pendingCode)) {
                projectModel.setCityScore(achievementScoreMap.get(it.getAchievementId()+"-CITY"));
                projectModel.setProvScore(achievementScoreMap.get(it.getAchievementId()+"-PROV"));
                projectModel.setScore(projectModel.getCityScore());
            }

            return projectModel;
        }).collect(Collectors.toList());

        PageInfo<FsipProjectAchievementEntity> orgPage = PageInfo.of(dataList);


        PageInfo<ProjectAchievementModel> pageInfo = PageInfo.of(projectModelList);
        pageInfo.setTotal(orgPage.getTotal());
        pageInfo.setSize(orgPage.getSize());
        pageInfo.setPageNum(orgPage.getPageNum());
        pageInfo.setPages(orgPage.getPages());
        return pageInfo;
    }

    @Override
    public void zgPush(ZgPushReq req, StaffInfo staffInfo) {
        if (ObjectUtils.isEmpty(req.getList())) {
            throw new BusinessException("500", "请选择评委会成员");
        }
        boolean checkTime = false;
        try {
            checkDeadLine(staffInfo, req.getAchievementType());
        } catch (Exception e) {
            checkTime = true;
        }
        if (!checkTime) {
            throw new BusinessException("500", "请在截止日期后再推送给评审委员会");
        }

        List<String> achievementIds = req.getAchievementIdList();
        if (CollUtil.isEmpty(achievementIds)) {
            throw new BusinessException("500", "请选择需要推送的成果列表");
        }

        String city2Prov = "PROV".equals(req.getAchievementType()) ? "1" : "0";
        List<FsipProjectAchievementEntity> push2CommentList = fsipProjectAchievementMapper.selectPush2CommentById(achievementIds, city2Prov);

        if (!ObjectUtils.isEmpty(push2CommentList)) {
            List<String> achievementNameList = push2CommentList.parallelStream().map(item -> item.getProjectName()).collect(Collectors.toList());
            throw new BusinessException("500", "[" + String.join("、", achievementNameList) + "]已推送过，不能再次推送");
        }

        String pendingUrl = verifyProperties.getCgsqReviewUrl().replaceAll("#achievementType", req.getAchievementType());
        Map<String, String> staffPendingMap = new HashMap<>();

        achievementIds.stream().forEach(achievementId -> {
            List<FsipApprovalNodeEntity> nodeList = new ArrayList<>();
            for (ZgPushReq.PSWYHBean beans : req.getList()) {
                FsipApprovalNodeEntity node = FsipApprovalNodeEntity.builder().apprType(IFsipConstants.TaskType.CGSQ).apprId(achievementId).nodeCode(IConstants.NodeCode.PSWYHPS).dealStaffName(beans.getStaffName()).dealStaffId(beans.getStaffId()).updateTime(new Date()).city2Prov(city2Prov).build();
                approvalNodeMapper.insert(node);
                nodeList.add(node);
            }
            if (!ObjectUtils.isEmpty(nodeList)) {
                for (FsipApprovalNodeEntity node : nodeList) {
                    if (staffPendingMap.containsKey(node.getDealStaffId())) {
                        node.setPendingCode(staffPendingMap.get(node.getDealStaffId()));
                        approvalNodeMapper.updateById(node);
                    } else {
                        String pendingCode = tranceNoTool.getTimeId(IFsipConstants.TaskType.CGSQ);
                        StringBuilder titleBuilder = new StringBuilder(DateUtils.getCurrYear()).append("年”五小创新“省级成果评审");
                        if ("CITY".equals(req.getAchievementType())) {
                            String month = LocalDateTimeUtil.format(LocalDateTime.now(), "MM");
                            titleBuilder = new StringBuilder(DateUtils.getCurrYear()).append("年").append(Integer.parseInt(month)).append("月”五小创新“市级成果评审");
                        }
                        PendingEntity pendingEntity = PendingEntity.builder().pendingCode(pendingCode).pendingTitle(titleBuilder.toString()).pendingDate(DateUtils.getDateString()).pendingUserID(node.getDealStaffId()).pendingURL(pendingUrl.concat(pendingCode)).pendingStatus(IConstants.PendingState.DB).pendingLevel(0).pendingSourceUserID(staffInfo.getMainUserId()).pendingSource(staffInfo.getEmpName()).applierId(staffInfo.getMainUserId()).applierName(staffInfo.getEmpName()).applierCompanyId(staffInfo.getCompanyId()).taskId(pendingCode).taskType(IFsipConstants.TaskType.CGSQ).taskStatus(IFsipConstants.Status.PSWYH).build();
                        BaseRsp<Void> response = restClient.addPending(new PendingEntity[]{pendingEntity});
                        if (!ReturnCode.SUCCESS.equals(response.getRspCode())) {
                            log.info("新增代办失败：" + response.getRspDesc());
                            throw new BaseException(response.getRspCode(), response.getRspDesc());
                        } else {
                            node.setPendingCode(pendingCode);
                            approvalNodeMapper.updateById(node);
                            staffPendingMap.put(node.getDealStaffId(), pendingCode);
                            //插入轨迹表
                            flowLogService.addFlowLog(FsipFlowLogEntity.builder().flowType(IFsipConstants.TaskType.CGSQ).extId(pendingCode).nodeCode("ZGTS").nodeName("专干推送").nodeState("04").dealStaffId(staffInfo.getMainUserId()).dealStaffName(staffInfo.getEmpName()).build());
                        }
                    }
                }
            }
        });
    }

    @Transactional
    @Override
    public void city2ProvApply(City2ProvAchievementApplyReq req, StaffInfo staffInfo) {
        List<String> achievementIdList = req.getAchievementIdList();
        if (CollUtil.isEmpty(achievementIdList)) {
            throw new BusinessException("请选择需申请的地市成果信息");
        }

        String month = LocalDateTimeUtil.format(LocalDateTime.now(), "MM");
        String deadLine = cacheService.getParamValue("DEADLINE", "PROVINCE");

        if (StringUtils.isNotBlank(deadLine) && Integer.parseInt(month) >= NumberUtils.toInt(deadLine)) {
            throw new BusinessException("500", "申请失败，失败原因：已过截止期限，请在每年" + Integer.parseInt(deadLine) + "月前申请");
        }

        for (String achievementId : achievementIdList) {
            FsipProjectAchievementBaseEntity baseEntity = fsipProjectAchievementBaseService.getById(achievementId);
            if (baseEntity == null) {
                log.error("Could not find base entity by id = " + achievementId);
                throw new BusinessException("成果信息不存在，请重新选择");
            }

            FsipProjectAchievementEntity achievementEntity = fsipProjectAchievementMapper.selectById(achievementId);
            log.info("apprNodeCode = " + baseEntity.getApprNodeCode());

            if ("GHZXSP".equals(baseEntity.getApprNodeCode())) {
                throw new BusinessException("[" + achievementEntity.getProjectName() + "]已提交申请，请不要重复提交");
            }
        }

        String remark = req.getApprovalReq().getOpinion();
        if(null!=req.getApprovalReq().getApprovalNodeList()
                && req.getApprovalReq().getApprovalNodeList().size()>0) {
            remark += "[审批人: "+req.getApprovalReq().getApprovalNodeList().get(0).getApproveName()+"]";
        }

        //设置成果的审批状态
        for (String achievementId : achievementIdList) {
            FsipProjectAchievementEntity achievementEntity = fsipProjectAchievementMapper.selectById(achievementId);
            achievementEntity.setStatus(IFsipConstants.Status.GHZX);
            achievementEntity.setSjcgspStatus("1");//1审批中 2审批同意 3审批不同意
            fsipProjectAchievementMapper.updateById(achievementEntity);

            FsipFlowLogEntity flowLog = FsipFlowLogEntity.builder()
                    .flowType(IFsipConstants.TaskType.SJCGZSJ)//地市转省分成果申请
                    .extId(achievementId)
                    .nodeCode("GHZXSP")//
                    .nodeName("管理层审批")
                    .nodeState(IFsipConstants.Status.BMLDSP)
                    .dealStaffId(staffInfo.getMainUserId())
                    .dealStaffName(staffInfo.getEmpName())
                    .remark(remark).build();
            flowLogService.addFlowLog(flowLog);
        }

        String pendingCode = tranceNoTool.getTimeId("SJCG");
        StringBuilder titleBuilder = new StringBuilder(DateUtils.getCurrYear()).append("年”五小创新“省级成果申请");

        ApprovalApplyReq.ApprovalNode approvalNode = pendingTaskService.getApprovalNode(IConstants.OperType.BL, req.getApprovalReq());

        List<FsipProjectAchievementBaseEntity> baseEntityList = achievementIdList.parallelStream().map(achievementId -> FsipProjectAchievementBaseEntity.builder().achievementId(achievementId).pendingCode(pendingCode).apprNodeCode(approvalNode.getNodeCode()).status(approvalNode.getNodeState()).applierId(staffInfo.getMainUserId()).applierDeptId(staffInfo.getDeptId()).applierCompanyId(staffInfo.getCompanyId()).build()).collect(Collectors.toList());
        fsipProjectAchievementBaseService.updateBatchById(baseEntityList);

        PendingModel pendingModel = PendingModel.builder().operType(IConstants.OperType.BL).approvalReq(req.getApprovalReq()).pendingCode(pendingCode).pendingTitle(titleBuilder.toString()).taskType(IFsipConstants.TaskType.SJCGZSJ).approvalId(pendingCode).pendingUrl(verifyProperties.getCity2ProvPcUrl()).taskStatus(IConstants.State.BMLDSP).operType(IConstants.OperType.BL).build();

        Long dingTaskId = pendingTaskService.applyApproval(pendingModel, staffInfo);
        if (dingTaskId != null) {
            baseEntityList.parallelStream().forEach(item -> item.setDingTaskId(dingTaskId));
            fsipProjectAchievementBaseService.updateBatchById(baseEntityList);
        }
    }

    @Override
    public List<ProjectAchievementModel> searchProject(ProjectAchievementSearchModel req) {
        StaffInfo staff = StaffInfoUtil.getStaff();

        // 查询逻辑
        LambdaQueryWrapper<FsipProjectAchievementEntity> wrapper = new LambdaQueryWrapper<>();

        // 查询参数添加
        wrapper.like(StringUtils.isNotBlank(req.getProjectName()), FsipProjectAchievementEntity::getProjectName, req.getProjectName());
        wrapper.eq(StringUtils.isNotBlank(req.getState()), FsipProjectAchievementEntity::getStatus, req.getState());
        wrapper.eq(StringUtils.isNotBlank(req.getProjectType()), FsipProjectAchievementEntity::getProjectType, req.getProjectType());
        wrapper.eq(StringUtils.isNotBlank(req.getInnovationType()), FsipProjectAchievementEntity::getInnovationType, req.getInnovationType());
        wrapper.eq(StringUtils.isNotBlank(req.getApplierCompanyId()), FsipProjectAchievementEntity::getApplierCompanyId, req.getApplierCompanyId());
        wrapper.eq(StringUtils.isNotBlank(req.getApplierDeptId()), FsipProjectAchievementEntity::getApplierDeptId, req.getApplierDeptId());
        wrapper.eq(StringUtils.isNotBlank(req.getIsOwn()) && "1".equals(req.getIsOwn()), FsipProjectAchievementEntity::getApplierId, staff.getMainUserId());
        wrapper.eq(StringUtils.isNotBlank(req.getSjcgsp()), FsipProjectAchievementEntity::getSjcgspStatus, req.getSjcgsp());

        if (StringUtils.isNotEmpty(req.getBenefit())) {
            String benefit[] = req.getBenefit().split("-");
            if(benefit.length == 2) {
                BigDecimal benefitMin = genBigDecimal(benefit[0].trim());
                BigDecimal benefitMax = genBigDecimal(benefit[1].trim());
                if(null!=benefitMin) {
                    wrapper.ge(FsipProjectAchievementEntity::getBenefit, benefitMin);
                }
                if(null!=benefitMax) {
                    wrapper.le(FsipProjectAchievementEntity::getBenefit, benefitMax);
                }
            }
        }

        if (StringUtils.isNotBlank(req.getAchievementType()) || StringUtils.isNotBlank(req.getAwardLevel())) {
            LambdaQueryChainWrapper<FsipProjectAchievementBaseEntity> achievementBaseLambdaQuery= fsipProjectAchievementBaseService.lambdaQuery();
            if (StringUtils.isNotBlank(req.getAchievementType())) {
                achievementBaseLambdaQuery.eq(FsipProjectAchievementBaseEntity::getAchievementType, req.getAchievementType());
            }
            if (StringUtils.isNotBlank(req.getAwardLevel())) {
                achievementBaseLambdaQuery.eq(FsipProjectAchievementBaseEntity::getAwardLevel, req.getAwardLevel());
            }
            List<FsipProjectAchievementBaseEntity> baseList = achievementBaseLambdaQuery.list();
            if (CollectionUtils.isEmpty(baseList)) {
                return new ArrayList<>();
            } else {
                wrapper.in(FsipProjectAchievementEntity::getAchievementId, baseList.parallelStream().map(FsipProjectAchievementBaseEntity::getAchievementId).collect(Collectors.toList()));
            }
        }

        List<FsipProjectAchievementBaseEntity> baseList = fsipProjectAchievementBaseService.lambdaQuery().eq(FsipProjectAchievementBaseEntity::getAchievementType, IFsipConstants.AchievementType.NATIONAL).list();

        if (!ObjectUtils.isEmpty(baseList)) {
            wrapper.notIn(FsipProjectAchievementEntity::getAchievementId, baseList.parallelStream().map(t -> t.getAchievementId()).collect(Collectors.toList()));
        }
        wrapper.orderByDesc(FsipProjectAchievementEntity::getApplyDate);
        log.info("baseList.size:{}", baseList.size());
        List<FsipProjectAchievementEntity> tmpList = fsipProjectAchievementService.list(wrapper);
        log.info("tmpList.size:{}", tmpList.size());

        //查询成果等级参数
        FsipStaticParamEntity paramEntity = new FsipStaticParamEntity();
        paramEntity.setAttrType("CGLX");
        List<FsipStaticParamEntity> params = paramService.queryParam(paramEntity, null);
        Map<String, String> cglxMap = params.stream().collect(Collectors.toMap(FsipStaticParamEntity::getAttrCode, FsipStaticParamEntity::getAttrValue));

        //省级评分/市级评分
        Map<String, String> achievementScoreMap;
        Map<String, String> awardLevelMap;
        Map<String, String> achievementTypeMap;
        if(null!=tmpList && tmpList.size()>0){
            List<String> achievementIds = tmpList.parallelStream().map(t -> t.getAchievementId()).collect(Collectors.toList());
            List<AchievementScoreVo> achievementScoreVos = fsipProjectAchievementReviewMapper.findAchievementScore(achievementIds);
            achievementScoreMap = achievementScoreVos.stream().collect(Collectors.toMap(AchievementScoreVo::getAchievementKey, AchievementScoreVo::getAvgScore));

            List<FsipProjectAchievementBaseEntity> list = fsipProjectAchievementBaseService.lambdaQuery().in(FsipProjectAchievementBaseEntity::getAchievementId, achievementIds).list();
            achievementTypeMap = list.stream().collect(Collectors.toMap(FsipProjectAchievementBaseEntity::getAchievementId, FsipProjectAchievementBaseEntity::getAchievementType));
            awardLevelMap = list.stream().collect(Collectors.toMap(FsipProjectAchievementBaseEntity::getAchievementId, FsipProjectAchievementBaseEntity::getAwardLevel));
        } else {
            achievementScoreMap = new HashMap<>();
            achievementTypeMap = new HashMap<>();
            awardLevelMap = new HashMap<>();
        }

        List<ProjectAchievementModel> projectModelList = new ArrayList<>();
        tmpList.forEach(it -> {
            ProjectAchievementModel projectModel = new ProjectAchievementModel();
            List<FsipProjectAchievementItemEntity> itemList = achievementItemService.lambdaQuery().eq(FsipProjectAchievementItemEntity::getAchievementId, it.getAchievementId()).orderByAsc(FsipProjectAchievementItemEntity::getSort).list();
            convert(it, itemList, projectModel, null);
            projectModel.setCityScore(achievementScoreMap.get(it.getAchievementId()+"-CITY"));
            projectModel.setProvScore(achievementScoreMap.get(it.getAchievementId()+"-PROV"));
            projectModel.setAchievementType(achievementTypeMap.get(it.getAchievementId()));
            projectModel.setAchievementTypeName(cglxMap.get(projectModel.getAchievementType()));
            projectModel.setAwardLevel(awardLevelMap.get(it.getAchievementId()));
            projectModel.setAwardLevelName(cacheService.getParamValue("AWARD_LEVEL", projectModel.getAwardLevel()));
            projectModel.setSjcgspName(cacheService.getParamValue("SJZSJCG_SP", projectModel.getSjcgsp()));
            projectModelList.add(projectModel);
        });
        return projectModelList;
    }

    @Override
    public List<StaffInfo> queryReviewJudgeList(String queryType, StaffInfo staffInfo) {
        if ("0".equals(queryType)) {
            return fsipProjectAchievementReviewMapper.selectPendingCityJudges(staffInfo.getCompanyId());
        }

        return fsipProjectAchievementReviewMapper.selectPendingProvJudges();
    }

    @Override
    public void urgingReview(ReviewUrgingReq req) {
        List<String> staffIdList = req.getStaffIdList();
        if (CollUtil.isEmpty(staffIdList)) {
            throw new BusinessException("请选择需要提醒的人员信息");
        }
        if (StringUtils.isBlank(req.getContent())) {
            throw new BusinessException("请输入提醒内容");
        }

        staffIdList.parallelStream().forEach(staffId -> {
            pendingTaskService.sendDingTextMessage(req.getContent(), staffId);
        });
    }

    private void updateAppendix(String extId, List<FileModel> appendixList) {
        // 删除目前关联的附件表
        LambdaQueryWrapper<FsipAppendixEntity> uw2 = Wrappers.lambdaQuery();
        uw2.eq(FsipAppendixEntity::getExtId, extId);
        appendixService.remove(uw2);

        Date applyDate = new Date();
        if (!ObjectUtils.isEmpty(appendixList)) {
            List<FsipAppendixEntity> appendixEntities = appendixList.parallelStream().map(item -> FsipAppendixEntity.builder().extId(extId).filePath(item.getOriginalFilename()).ossFileName(item.getFileName()).applyDate(applyDate).build()).collect(Collectors.toList());
            //插入新的附件表
            appendixService.saveBatch(appendixEntities);
        }
    }

    private void updatePendingStatus(String pendingCode) {
        PendingUpEntity pendingUpEntity = PendingUpEntity.builder().pendingCode(pendingCode).pendingStatus(IConstants.PendingState.YB).build();
        BaseRsp<Void> baseRsp = restClient.updatePendingStatus(new PendingUpEntity[]{pendingUpEntity});

        if (!RspHelp.SUCCESS_CODE.equals(baseRsp.getRspCode())) {
            throw new BaseException(baseRsp.getRspCode(), baseRsp.getRspDesc());
        }
    }

    /**
     * 检查是否在截止日期前
     *
     * @param staffInfo
     */
    private void checkDeadLine(StaffInfo staffInfo, String type) {
        String companyId = staffInfo.getCompanyId();
        if ("PROV".equals(type)) { //省公司
            String month = LocalDateTimeUtil.format(LocalDateTime.now(), "MM");
            String deadLine = cacheService.getParamValue("DEADLINE", "PROVINCE");
            if (ObjectUtils.isEmpty(deadLine)) {
                return;
            }
            if (Integer.parseInt(month) >= Integer.parseInt(deadLine)) {
                throw new BusinessException("500", "申请失败，失败原因：已过截止期限，请在每年" + Integer.parseInt(deadLine) + "月前申请");
            }
        } else {
            String day = LocalDateTimeUtil.format(LocalDateTime.now(), "dd");
            String deadLine = cacheService.getParamValue("DEADLINE", "CITY" + companyId);
            log.info("deadLine = " + deadLine + ", CITY" + companyId);
            if (ObjectUtils.isEmpty(deadLine)) {
                return;
            }
            if (Integer.parseInt(day) >= Integer.parseInt(deadLine)) {
                throw new BusinessException("500", "申请失败，失败原因：已过截止期限，请在每月" + Integer.parseInt(deadLine) + "日前申请");
            }
        }
    }

    private void validateModel(ProjectAchievementModel projectModel) {
        // 项目名称
        if (ObjectUtils.isEmpty(projectModel.getProjectName())) {
            throw new BusinessException("500", "校验失败:项目名称不能为空");
        }

        // 项目起始时间
        if (ObjectUtils.isEmpty(projectModel.getStartDate())) {
            throw new BusinessException("500", "校验失败:项目起始时间不能为空");
        }

        // 项目结束时间
        if (ObjectUtils.isEmpty(projectModel.getEndDate())) {
            throw new BusinessException("500", "校验失败:项目结束时间不能为空");
        }

        // 经济效益
        if (ObjectUtils.isEmpty(projectModel.getBenefit())) {
            throw new BusinessException("500", "校验失败:经济效益不能为空");
        }

        // 所属类别
        if (ObjectUtils.isEmpty(projectModel.getInnovationType())) {
            throw new BusinessException("500", "校验失败:所属类别不能为空");
        }
        // 所属项目
        if (ObjectUtils.isEmpty(projectModel.getProjectType())) {
            throw new BusinessException("500", "校验失败:所属项目不能为空");
        }

        // 时间转换
        try {
            projectModel.setStartDateDate(DateUtils.getDate(projectModel.getStartDate(), "yyyy-MM-dd"));
            projectModel.setEndDateDate(DateUtils.getDate(projectModel.getEndDate(), "yyyy-MM-dd"));
            projectModel.setBenefitFloat(Float.parseFloat(projectModel.getBenefit()));
        } catch (ParseException e) {
            throw new BusinessException("500", "校验失败:日期格式错误");
        }
        // 属性校验
        projectModel.getItemList().forEach(it -> {
            validateItemModel(it);
        });
    }

    private void validateItemModel(ProjectAchievementModel.AchievementItem itemModel) {
        // 属性编码
        if (ObjectUtils.isEmpty(itemModel.getItemCode())) {
            throw new BusinessException("500", "校验失败:属性编码不能为空");
        }

        // 属性名称
        if (ObjectUtils.isEmpty(itemModel.getItemName())) {
            throw new BusinessException("500", "校验失败:属性名称不能为空");
        }

        // 排序
        if (itemModel.getSort() == null) {
            throw new BusinessException("500", "校验失败:排序不能为空");
        }
    }

    private void convert(ProjectAchievementModel projectModel, FsipProjectAchievementEntity projectAchievementEntity) {
        projectAchievementEntity.setProjectName(projectModel.getProjectName());
        projectAchievementEntity.setStartDate(projectModel.getStartDateDate());
        projectAchievementEntity.setEndDate(projectModel.getEndDateDate());
        projectAchievementEntity.setBenefit(projectModel.getBenefitFloat());
        projectAchievementEntity.setInnovationType(projectModel.getInnovationType());
        projectAchievementEntity.setProjectType(projectModel.getProjectType());
        projectAchievementEntity.setBackImage(projectModel.getBackImage());
    }

    private void convert(ProjectAchievementModel.AchievementItem item, FsipProjectAchievementItemEntity achievementItemEntity) {
        achievementItemEntity.setItemType(IFsipConstants.ACHIEVEMENT);
        achievementItemEntity.setItemCode(item.getItemCode());
        achievementItemEntity.setItemName(item.getItemName());
        achievementItemEntity.setItemValue(item.getItemValue());
        achievementItemEntity.setSort(item.getSort());
    }

    private void convert(FsipProjectAchievementEntity project, List<FsipProjectAchievementItemEntity> itemList, ProjectAchievementModel projectModel, List<FlowLogModel> logList) {
        projectModel.setProjectId(project.getAchievementId());
        projectModel.setProjectName(project.getProjectName());
        projectModel.setStartDate(DateUtils.getDateString(project.getStartDate(), "yyyy-MM-dd"));
        projectModel.setEndDate(DateUtils.getDateString(project.getEndDate(), "yyyy-MM-dd"));
        projectModel.setBenefit(String.valueOf(project.getBenefit()));
        projectModel.setInnovationType(cacheService.getParamValue("CXLX", project.getInnovationType()));
        projectModel.setProjectType(cacheService.getParamValue("XMLX", project.getProjectType()));
        projectModel.setApplierId(project.getApplierId());
        projectModel.setApplierName(project.getApplierName());
        projectModel.setApplierCompanyId(project.getApplierCompanyId());
        projectModel.setApplierDeptId(project.getApplierDeptId());
        projectModel.setStatus(project.getStatus());
        projectModel.setSjcgsp(project.getSjcgspStatus());
        projectModel.setSjcgspName(cacheService.getParamValue("SJZSJCG_SP", project.getSjcgspStatus()));

        projectModel.setStatusName(cacheService.getParamValue("STATE", project.getStatus()));
        // 工会主席审批
        if ("02".equals(project.getStatus())){
            projectModel.setStatusName("工会主席审批");
        }
        if (!ObjectUtils.isEmpty(project.getApplierDeptId())) {
            DepartmentInfo departmentInfo = cacheService.getDepartment(project.getApplierDeptId());
            if (departmentInfo != null) {
                projectModel.setApplierDeptId(departmentInfo.getDeptName());
                projectModel.setApplierCompanyId(departmentInfo.getCompanyName());
            }
        }
        projectModel.setBackImage(project.getBackImage());
        projectModel.setApplyDate(DateUtils.getDateString(project.getApplyDate(), "yyyy-MM-dd HH:mm:ss"));
        // 属性信息
        List<ProjectAchievementModel.AchievementItem> newItemList = new ArrayList<>();
        List<ProjectAchievementModel.AchievementItem> awardAttachList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(itemList)) {
            itemList.stream().forEach(iItem -> {
                ProjectAchievementModel.AchievementItem item = new ProjectAchievementModel.AchievementItem();
                item.setItemType(iItem.getItemType());
                item.setItemCode(iItem.getItemCode());
                item.setItemName(iItem.getItemName());
                item.setItemValue(iItem.getItemValue());
                item.setSort(iItem.getSort());
                item.setScore(iItem.getScore());

                if ("PROV_ATTACHMENT".equals(iItem.getItemType()) || "CITY_ATTACHMENT".equals(iItem.getItemType())) {
                    awardAttachList.add(item);
                } else {
                    newItemList.add(item);
                }
            });
        }
        if (!ObjectUtils.isEmpty(logList)) {
            projectModel.setApprovalInfoList(logList.stream().map(t -> ProjectAchievementModel.ApprovalInfo.builder().nodeName(t.getNodeName()).dealStaffName(t.getOperateName()).dealTime(t.getOperateTime()).remark(t.getRemark()).build()).collect(Collectors.toList()));
        }
        List<FsipAppendixEntity> appendixList = appendixService.lambdaQuery().eq(FsipAppendixEntity::getExtId, project.getAchievementId()).list();
        if (!ObjectUtils.isEmpty(appendixList)) {
            projectModel.setAppendixList(appendixList.parallelStream().map(t -> FileModel.builder().fileName(t.getOssFileName()).originalFilename(t.getFilePath()).build()).collect(Collectors.toList()));
        }
        projectModel.setItemList(newItemList);
        projectModel.setAwardAttachList(awardAttachList);

        try {
            List<FsipProjectAchievementItemEntity> noQt = itemList.stream().filter(t -> !t.getItemCode().equals("QT")).collect(Collectors.toList());
            projectModel.setScoreTotal(noQt.size());
            projectModel.setScoreCount((int) noQt.stream().filter(t -> StringUtils.isNotBlank(t.getScore())).count());
            projectModel.setAvgScore(noQt.stream().filter(t -> StringUtils.isNotBlank(t.getScore())).map(t -> new BigDecimal(t.getScore())).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(projectModel.getScoreTotal()), 1, RoundingMode.HALF_UP).toString());
            Set<String> collect = noQt.stream().map(FsipProjectAchievementItemEntity::getScoreStatus).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            if(!collect.isEmpty()){
                projectModel.setScoreStatus(collect.contains("00") ? "00" : "ZC");
            }
        }catch (Exception e){}
    }

    //全都审批打分完成后 申请就变成完成
    private void checkProjectApprove(FsipProjectAchievementEntity project) {
        List<FsipApprovalNodeEntity> nodeEntities = approvalNodeMapper.selectApprovalNodeByNode(IFsipConstants.TaskType.CGSQ, project.getAchievementId(), IConstants.NodeCode.PSWYHPS);
        List<FsipProjectAchievementReviewEntity> list = reviewService.lambdaQuery().eq(FsipProjectAchievementReviewEntity::getAchievementId, project.getAchievementId()).eq(FsipProjectAchievementReviewEntity::getStatus, "00").list();
        if (!CollectionUtils.isEmpty(list)) {
            if (StringUtils.isNotEmpty(project.getCityToProvFlag()) && "1".equals(project.getCityToProvFlag())) {
                if (nodeEntities.stream().filter(t -> "1".equals(t.getCity2Prov())).map(t -> t.getDealStaffId()).distinct().count() == list.stream().filter(t -> "1".equals(t.getCityToProvFlag())).map(t -> t.getJudgesId()).distinct().count()) {
                    project.setStatus("00");
                    fsipProjectAchievementService.updateById(project);
                }
            } else {
                if (nodeEntities.stream().filter(t -> StringUtils.isEmpty(t.getCity2Prov()) || !"1".equals(t.getCity2Prov())).map(t -> t.getDealStaffId()).distinct().count() == list.stream().filter(t -> StringUtils.isEmpty(t.getCityToProvFlag()) || !"1".equals(t.getCityToProvFlag())).map(t -> t.getJudgesId()).distinct().count()) {
                    project.setStatus("00");
                    fsipProjectAchievementService.updateById(project);
                }
            }
        }
    }

    @Override
    public void achievementArchive(AchievementArchiveReq req, StaffInfo staffInfo) {
        if (ObjectUtils.isEmpty(req) || ObjectUtils.isEmpty(req.getProjectIds()) ) {
            throw new BusinessException("500", "参数异常");
        }

        String status ;
        String achievementType;

        if ("PROV2CITY".equals(req.getArchiveType())) {
            status = IFsipConstants.Status.PROV2CITY;
            achievementType = IFsipConstants.AchievementType.CITY;

            fsipProjectAchievementBaseMapper.update(null,Wrappers.<FsipProjectAchievementBaseEntity>lambdaUpdate().set(FsipProjectAchievementBaseEntity::getApprNodeCode, "").in(FsipProjectAchievementBaseEntity::getAchievementId, req.getProjectIds()));
        }else if ("CITY2DEPT".equals(req.getArchiveType())) {
            status = IFsipConstants.Status.CITY2DEPT;
            achievementType = IFsipConstants.AchievementType.DEPT;
        }else {
            throw new BusinessException("500", "参数异常");
        }
        FsipProjectAchievementEntity updateTemp = new FsipProjectAchievementEntity();
        updateTemp.setStatus(status);
        updateTemp.setApprNodeCode(achievementType);
        fsipProjectAchievementMapper.update(updateTemp, Wrappers.<FsipProjectAchievementEntity>lambdaQuery().in(FsipProjectAchievementEntity::getAchievementId, req.getProjectIds()));



        //TODO 删除审批信息  及流程  评分信息
    }
}
