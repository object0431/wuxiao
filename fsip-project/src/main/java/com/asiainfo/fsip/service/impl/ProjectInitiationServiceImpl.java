package com.asiainfo.fsip.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.client.TmcRestClient;
import com.asiainfo.fsip.config.VerifyProperties;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.*;
import com.asiainfo.fsip.mapper.fsip.*;
import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.*;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.entity.DepartmentInfo;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.TranceNoTool;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectInitiationServiceImpl implements ProjectInitiationService {

    @Resource
    private FsipProjectInitiationMapper fsipProjectInitiationMapper;
    @Resource
    private CacheService cacheService;
    @Resource
    private FsipProjectInitiationItemMapper fsipProjectInitiationItemMapper;
    @Resource
    private PendingTaskService pendingTaskService;
    @Resource
    private FsipStaticParamMapper fsipStaticParamMapper;
    @Resource
    private TranceNoTool tranceNoTool;
    @Resource
    private FlowLogService flowLogService;
    @Resource
    private FsipExpertAdviceMapper fsipExpertAdviceMapper;
    @Resource
    private VerifyProperties verifyProperties;
    @Resource
    private FsipApprovalNodeMapper fsipApprovalNodeMapper;
    @Resource
    private FsipExpertAdviceMapper expertAdviceMapper;

    @Override
    public SaveProjectRsp saveProject(SaveProjectReq saveProjectReq) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        String projectId = tranceNoTool.getTranceNoSecondUnique("HN", "yyMMddHHmmssSSS", 3);
        FsipProjectInitiationEntity entity = new FsipProjectInitiationEntity();
        entity.setProjectId(projectId);
        entity.setProjectName(saveProjectReq.getProjectName());
        entity.setStartDate(this.stringToDate(saveProjectReq.getStartTime()));
        entity.setEndDate(this.stringToDate(saveProjectReq.getEndTime()));
        entity.setExpectedBenefits(StringUtils.isEmpty(saveProjectReq.getEconomicBenefit()) ? new BigDecimal(0) : new BigDecimal(saveProjectReq.getEconomicBenefit()));
        entity.setInnovationType(saveProjectReq.getTypeAttrCode());
        entity.setProjectType(saveProjectReq.getProjectAttrCode());
        entity.setStatus("1".equals(saveProjectReq.getOperateType()) ? IConstants.State.BMLDSP : IConstants.State.ZC);
        entity.setMemberNames(saveProjectReq.getMenberNames());
        entity.setApplierId(staffInfo.getMainUserId());
        entity.setApplierName(staffInfo.getEmpName());
        entity.setApplierCompanyId(staffInfo.getCompanyId());
        entity.setApplierDeptId(staffInfo.getDeptId());
        entity.setApplyDate(Calendar.getInstance().getTime());
        if (StringUtils.isEmpty(saveProjectReq.getProjectId())) {
            fsipProjectInitiationMapper.insert(entity);
        } else {
            projectId = saveProjectReq.getProjectId();
            entity.setProjectId(saveProjectReq.getProjectId());
            FsipProjectInitiationEntity dbEntity = fsipProjectInitiationMapper.selectById(projectId);
            if (dbEntity == null) {
                return SaveProjectRsp.builder().rspCode("8888").rspMsg("项目[".concat(projectId).concat("]不存在")).projectId(projectId).build();
            }
            entity.setExpectedBenefits(entity.getExpectedBenefits().compareTo(new BigDecimal(0)) == 0 ? dbEntity.getExpectedBenefits() : entity.getExpectedBenefits());
            fsipProjectInitiationMapper.updateById(entity);
        }
        String content = null;
        if (!CollectionUtils.isEmpty(saveProjectReq.getProjectAttrList())) {
            FsipProjectInitiationItemEntity item;
            int sort = 1;
            for (SaveProjectReq.ProjectAttr attr : saveProjectReq.getProjectAttrList()) {
                if("XMJS".equals(attr.getAttrType()) && "CXX".equals(attr.getAttrCode())){
                    content = attr.getAttrValue();
                }
                item = new FsipProjectInitiationItemEntity();
                item.setProjectId(projectId);
                item.setItemType(attr.getAttrType());
                item.setItemCode(attr.getAttrCode());
                item.setItemName(attr.getAttrName());
                item.setItemValue(attr.getAttrValue());
                item.setSort(sort++);
                Map<String, Object> conditionMap = new HashMap<>();
                conditionMap.put("PROJECT_ID", projectId);
                conditionMap.put("ITEM_TYPE", attr.getAttrType());
                conditionMap.put("ITEM_CODE", attr.getAttrCode());
                List<FsipProjectInitiationItemEntity> itemEntityList = fsipProjectInitiationItemMapper.selectByMap(conditionMap);
                if (!CollectionUtils.isEmpty(itemEntityList)) {
                    fsipProjectInitiationItemMapper.deleteByMap(conditionMap);
                }
                fsipProjectInitiationItemMapper.insert(item);
            }
        }
        if ("1".equals(saveProjectReq.getOperateType())) {
            String pendingCode = tranceNoTool.getCommonId(IFsipConstants.TaskType.LXSQ);
            String pendindTitle = "立项申请-".concat(entity.getProjectName()).concat("-").concat(entity.getApplierName());
            String taskType = IFsipConstants.TaskType.LXSQ;
            // 如果是省分部门
            if (saveProjectReq.getApplyReq().getApprovalNodeList().size() > 0){
                ApprovalApplyReq.ApprovalNode approvalNode = saveProjectReq.getApplyReq().getApprovalNodeList().get(0);
                if ("PBMLDSP".equals(approvalNode.getNodeCode())){
                    taskType = "PLXSQ";
                }
            }
            PendingModel pendingModel = PendingModel.builder().operType(IConstants.OperType.BL)
                    .approvalReq(saveProjectReq.getApplyReq()).pendingCode(pendingCode).pendingTitle(pendindTitle)
                    .taskType(taskType).approvalId(projectId).content(content)
                    .pendingUrl(verifyProperties.getProjectPcUrl()).mobileUrl(verifyProperties.getProjectMobileUrl())
                    .taskStatus(IConstants.State.BMLDSP).build();
            Long dingTaskId = pendingTaskService.applyApproval(pendingModel, staffInfo);
            FsipProjectInitiationEntity updateEntity = new FsipProjectInitiationEntity();
            updateEntity.setProjectId(projectId);
            updateEntity.setApprNodeCode(IConstants.NodeCode.BMLDSP);
            // 如果是省分部门领导，则进行处理
            if (saveProjectReq.getApplyReq().getApprovalNodeList().size() > 0){
                ApprovalApplyReq.ApprovalNode approvalNode = saveProjectReq.getApplyReq().getApprovalNodeList().get(0);
                String nodeCode = approvalNode.getNodeCode();
                if ("PBMLDSP".equals(nodeCode)){
                    updateEntity.setApprNodeCode(nodeCode);
                }
            }
            updateEntity.setPendingCode(pendingCode);
            updateEntity.setDingTaskId(dingTaskId);
            fsipProjectInitiationMapper.updateById(updateEntity);
        }
        return SaveProjectRsp.builder().rspCode("0000").rspMsg("ok").projectId(projectId).build();
    }

    @Override
    public ProjectQueryRsp queryProject(ProjectQueryReq queryReq) {
        log.info("queryReq = " + JSONObject.toJSONString(queryReq));
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        int pageNum = queryReq.getPageNum() == 0 ? 1 : queryReq.getPageNum();
        int pageSize = queryReq.getPageSize() == 0 ? 1 : queryReq.getPageSize();
        int start = (pageNum - 1) * pageSize;
        int end = start + pageSize;
        final List<ProjectQueryRsp.RspData> rspDataList = new ArrayList<>();
        QueryWrapper<FsipProjectInitiationEntity> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(queryReq.getTypeAttrCode())) {
            queryWrapper.eq("INNOVATION_TYPE", queryReq.getTypeAttrCode());
        }
        if (!StringUtils.isEmpty(queryReq.getProjectAttrCode())) {
            queryWrapper.eq("PROJECT_TYPE", queryReq.getProjectAttrCode());
        }
        if (!StringUtils.isEmpty(queryReq.getProjectName())) {
            queryWrapper.like("PROJECT_NAME", queryReq.getProjectName());
        }
        if (!StringUtils.isEmpty(queryReq.getProjectState())) {
            queryWrapper.eq("STATUS", queryReq.getProjectState());
        }
        if (!StringUtils.isEmpty(queryReq.getApplierCompanyId())) {
            queryWrapper.eq("APPLIER_COMPANY_ID", queryReq.getApplierCompanyId());
        }
        if (!StringUtils.isEmpty(queryReq.getApplierDeptId())) {
            queryWrapper.eq("APPLIER_DEPT_ID", queryReq.getApplierDeptId());
        }
        if (!StringUtils.isEmpty(queryReq.getIsSelf()) && "1".equals(queryReq.getIsSelf())) {
            queryWrapper.eq("APPLIER_ID", staffInfo.getMainUserId());
        }
        queryWrapper.orderByDesc("APPLY_DATE");
        List<FsipProjectInitiationEntity> entityList = fsipProjectInitiationMapper.selectList(queryWrapper);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!CollectionUtils.isEmpty(entityList)) {
            final Map<String, String> staticParamMap = this.getStaticParamMap("CXLX", "XMLX");

            Set<String> projectIds = entityList.stream().map(FsipProjectInitiationEntity::getProjectId).collect(Collectors.toSet());
            LambdaQueryWrapper<FsipProjectInitiationItemEntity> in = new QueryWrapper<FsipProjectInitiationItemEntity>().lambda().in(FsipProjectInitiationItemEntity::getProjectId, projectIds);
            List<FsipProjectInitiationItemEntity> itemEntityList = fsipProjectInitiationItemMapper.selectList(in);
            Map<String, List<FsipProjectInitiationItemEntity>> projectIdMap = itemEntityList.stream().collect(Collectors.groupingBy(FsipProjectInitiationItemEntity::getProjectId));

            entityList.forEach(v -> {
                List<ProjectQueryRsp.ProjectAttr> projectAttrList = new ArrayList<>();
                List<FsipProjectInitiationItemEntity> items = projectIdMap.get(v.getProjectId());
                if (items != null) {
                    items.forEach(iv -> projectAttrList.add(
                            ProjectQueryRsp.ProjectAttr.builder()
                                    .attrType(iv.getItemType())
                                    .attrCode(iv.getItemCode())
                                    .attrName(iv.getItemName())
                                    .attrValue(iv.getItemValue()).build()
                    ));
                }
                DepartmentInfo departmentInfo = cacheService.getDepartment(v.getApplierDeptId());
                rspDataList.add(
                        ProjectQueryRsp.RspData.builder()
                                .projectId(v.getProjectId())
                                .projectName(v.getProjectName())
                                .status(v.getStatus())
                                .startTime(v.getStartDate() == null ? "" : format.format(v.getStartDate()))
                                .endTime(v.getEndDate() == null ? "" : format.format(v.getEndDate()))
                                .economicBenefit(String.valueOf(v.getExpectedBenefits()))
                                .typeAttrCode(v.getInnovationType())
                                .typeAttrName(this.getMapValue(staticParamMap, "CXLX_".concat(v.getInnovationType())))
                                .projectAttrCode(v.getProjectType())
                                .projectAttrName(this.getMapValue(staticParamMap, "XMLX_".concat(v.getProjectType())))
                                .applier(v.getApplierId())
                                .applierName(v.getApplierName())
                                .applyCompany(v.getApplierCompanyId())
                                .applyCompanyName(cacheService.getCompanyMap().get(v.getApplierCompanyId()))
                                .applyDept(v.getApplierDeptId())
                                .apprNodeCode(v.getApprNodeCode())
                                .applyDeptName(departmentInfo == null ? "" : departmentInfo.getDeptName())
                                .projectAttrList(projectAttrList).build()
                );
            });
        }
        int total = rspDataList.size();
        List<ProjectQueryRsp.RspData> pageList = CollectionUtils.isEmpty(rspDataList) ? null : rspDataList.subList(start, Math.min(end, rspDataList.size()));
        return ProjectQueryRsp.builder().pageNum(pageNum).pageSize(pageSize).total(total).dataList(pageList).build();
    }

    @Override
    public ProjectDetailRsp detailProject(ProjectDetailReq detailReq) {
        ProjectDetailRsp rsp = null;
        String projectId = detailReq.getProjectId();
        FsipProjectInitiationEntity entity = fsipProjectInitiationMapper.selectById(projectId);
        if (entity != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final Map<String, String> staticParamMap = this.getStaticParamMap("CXLX", "XMLX");
            QueryWrapper<FsipProjectInitiationItemEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("PROJECT_ID", entity.getProjectId());
            List<ProjectDetailRsp.ProjectAttr> projectAttrList = new ArrayList<>();
            List<FsipProjectInitiationItemEntity> itemEntityList = fsipProjectInitiationItemMapper.selectList(wrapper);
            if (!CollectionUtils.isEmpty(itemEntityList)) {
                itemEntityList.forEach(iv -> projectAttrList.add(
                        ProjectDetailRsp.ProjectAttr.builder()
                                .attrType(iv.getItemType())
                                .attrCode(iv.getItemCode())
                                .attrName(iv.getItemName())
                                .attrValue(iv.getItemValue()).build()
                ));
            }
            List<ProjectDetailRsp.ApprovalFlow> approvalFlowList = new ArrayList<>();
            String taskType = IFsipConstants.TaskType.LXSQ;
            // 省分管领导
            if ("PBMLDSP".equals(entity.getApprNodeCode()) ||
                    "PFGFLD".equals(entity.getApprNodeCode()) ||
                    "PFGLD".equals(entity.getApprNodeCode())){
                taskType = "PLXSQ";
            }
            List<FlowLogModel> flowLogModelList = flowLogService.queryFlowLogById(taskType, projectId);
            if (!CollectionUtils.isEmpty(flowLogModelList)) {
                flowLogModelList.forEach(v -> approvalFlowList.add(
                        ProjectDetailRsp.ApprovalFlow.builder()
                                .nodeCode(v.getNodeCode())
                                .nodeName(v.getNodeName())
                                .operateTime(v.getOperateTime())
                                .operateId(v.getOperateId())
                                .operateName(v.getOperateName())
                                .approvalRet(v.getApprovalRet())
                                .isComplete(v.getIsComplete())
                                .remark(v.getRemark()).build()
                ));
            }
            List<ProjectDetailRsp.ExpertAdvice> expertAdviceList = new ArrayList<>();
            QueryWrapper<FsipExpertAdviceEntity> expertAdviceEntityQueryWrapper = new QueryWrapper<>();
            expertAdviceEntityQueryWrapper.eq("TARGET_ID", entity.getProjectId());
            List<FsipExpertAdviceEntity> expertAdviceEntityList = fsipExpertAdviceMapper.selectList(expertAdviceEntityQueryWrapper);
            if (!CollectionUtils.isEmpty(expertAdviceEntityList)) {
                expertAdviceEntityList.forEach(v -> expertAdviceList.add(
                        ProjectDetailRsp.ExpertAdvice.builder()
                                .suggestion(v.getSuggestion())
                                .score(String.valueOf(v.getScore()))
                                .reqTime(v.getReqTime() == null ? "" : format.format(v.getReqTime()))
                                .respTime(v.getRespTime() == null ? "" : format.format(v.getRespTime()))
                                .state(v.getStatus()).build()
                ));
            }
            DepartmentInfo departmentInfo = cacheService.getDepartment(entity.getApplierDeptId());
            rsp = ProjectDetailRsp.builder().projectId(entity.getProjectId())
                    .projectName(entity.getProjectName())
                    .startTime(entity.getStartDate() == null ? "" : format.format(entity.getStartDate()))
                    .endTime(entity.getEndDate() == null ? "" : format.format(entity.getEndDate()))
                    .economicBenefit(String.valueOf(entity.getExpectedBenefits()))
                    .typeAttrCode(entity.getInnovationType())
                    .typeAttrName(this.getMapValue(staticParamMap, "CXLX_".concat(entity.getInnovationType())))
                    .projectAttrCode(entity.getProjectType())
                    .projectAttrName(this.getMapValue(staticParamMap, "XMLX_".concat(entity.getProjectType())))
                    .menberNames(entity.getMemberNames())
                    .status(entity.getStatus())
                    .applierId(entity.getApplierId())
                    .applierName(entity.getApplierName())
                    .applyDeptId(entity.getApplierDeptId())
                    .applyDeptName(departmentInfo == null ? "" : departmentInfo.getDeptName())
                    .applyCompanyId(entity.getApplierCompanyId())
                    .applyCompanyName(cacheService.getCompanyMap().get(entity.getApplierCompanyId()))
                    .projectAttrList(projectAttrList)
                    .approvalFlowList(approvalFlowList)
                    .expertAdviceList(expertAdviceList).build();
        }
        return rsp;
    }

    @Override
    public void transferProject(ProjectTransferReq transferReq, StaffInfo staffInfo ) {
        String projectId = transferReq.getProjectId();

        FsipProjectInitiationEntity project = fsipProjectInitiationMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("8888", "没有找到项目[".concat(projectId).concat("]信息"));
        }

        if(!IConstants.State.BMLDSP.equals(project.getStatus())){
            throw new BusinessException("8888", "当前状态不允许转发");
        }

        final List<FsipApprovalNodeEntity> nodeEntityList = fsipApprovalNodeMapper.selectApprovalNodeByNode(IFsipConstants.TaskType.LXSQ, projectId, project.getApprNodeCode());
        FsipApprovalNodeEntity nodeEntity = null;
        if (CollectionUtils.isEmpty(nodeEntityList)) throw new BusinessException("8888", "没有找到项目审批环节信息");
        for (FsipApprovalNodeEntity v : nodeEntityList) {
            if (v.getDealStaffId().equals(staffInfo.getMainUserId())) {
                nodeEntity = v;
                break;
            }
        }

        if (nodeEntity == null) throw new BusinessException("8888", "没有找到项目审批环节信息");
        FsipApprovalNodeEntity node = new FsipApprovalNodeEntity();
        BeanUtils.copyProperties(nodeEntity, node);
        node.setId(null);
        node.setNodeCode(IFsipConstants.NodeCode.ZFTRSP);
        node.setDealStaffId(transferReq.getTransferStaffId());
        node.setDealStaffName(transferReq.getTransferStaffName());
        nodeEntity.setUpdateTime(Calendar.getInstance().getTime());
        nodeEntity.setTransferDealStaffId(transferReq.getTransferStaffId());
        nodeEntity.setTransferDealStaffName(transferReq.getTransferStaffName());
        fsipApprovalNodeMapper.updateById(nodeEntity);
        fsipApprovalNodeMapper.insert(node);

        FsipFlowLogEntity flowLog = FsipFlowLogEntity.builder()
                .flowType(IFsipConstants.TaskType.LXSQ)
                .extId(projectId)
                .nodeCode(IFsipConstants.NodeCode.ZFTRSP)
                .nodeName("转发他人审批")
                .nodeStateName("转发审批")
                .nodeState(IFsipConstants.Status.ZFTRSP)
                .dealStaffId(staffInfo.getMainUserId())
                .dealStaffName(staffInfo.getEmpName())
                .remark(transferReq.getRemark()).build();
        flowLogService.addFlowLog(flowLog);

        pendingTaskService.updatePendingStatus(project.getPendingCode());

        if (project.getDingTaskId() != null) {
            pendingTaskService.updateDingNotifyStatus(project.getDingTaskId(), "已转发");
        }

        String pendingCode = tranceNoTool.getCommonId(IFsipConstants.TaskType.LXSQ);
        String title = "立项申请-".concat(project.getProjectName()).concat("-").concat(project.getApplierName());

        List<ApprovalApplyReq.ApprovalNode> approvalNodeList = new ArrayList<>();
        approvalNodeList.add(ApprovalApplyReq.ApprovalNode.builder().approveId(transferReq.getTransferStaffId())
                .approveName(transferReq.getTransferStaffName()).build());

        PendingModel pendingModel = PendingModel.builder().operType(IConstants.OperType.BL).approvalId(projectId)
                .approvalReq(ApprovalApplyReq.builder().approvalNodeList(approvalNodeList).build())
                .pendingCode(pendingCode).pendingTitle(title)
                .pendingUrl(verifyProperties.getProjectPcUrl()).mobileUrl(verifyProperties.getProjectMobileUrl())
                .taskStatus(IConstants.State.BMLDSP).build();

        pendingTaskService.sendPendingTask(pendingModel, staffInfo);

        UrlModel urlModel = UrlModel.builder().mobileUrl(verifyProperties.getProjectMobileUrl())
                .title(title).build();

        Long dingTaskId = pendingTaskService.sendDingOaMessage(projectId, urlModel, title, transferReq.getTransferStaffId(), staffInfo);

        FsipProjectInitiationEntity entity = FsipProjectInitiationEntity.builder()
                .projectId(projectId).apprNodeCode(IFsipConstants.NodeCode.ZFTRSP).pendingCode(pendingCode)
                .dingTaskId(dingTaskId).build();
        fsipProjectInitiationMapper.updateById(entity);
    }

    @Override
    public ModifyProjectRsp modifyProject(ModifyProjectReq modifyProjectReq) {
        FsipProjectInitiationEntity entity = fsipProjectInitiationMapper.selectById(modifyProjectReq.getProjectId());
        if (StringUtils.isEmpty(entity.getProjectId())) {
            return ModifyProjectRsp.builder().rspCode("8888").rspMsg("项目[".concat(modifyProjectReq.getProjectId()).concat("]不存在")).projectId(modifyProjectReq.getProjectId()).build();
        }
        if (!entity.getStatus().equals(IFsipConstants.Status.ZC) && !entity.getStatus().equals(IFsipConstants.Status.TH) && !entity.getStatus().equals(IFsipConstants.Status.CH)) {
            return ModifyProjectRsp.builder().rspCode("8888").rspMsg("项目[".concat(modifyProjectReq.getProjectId()).concat("]必须为暂存、撤回或退回修改状态才能修改！")).projectId(modifyProjectReq.getProjectId()).build();
        }
        entity.setProjectName(modifyProjectReq.getProjectName());
        entity.setStartDate(this.stringToDate(modifyProjectReq.getStartTime()));
        entity.setEndDate(this.stringToDate(modifyProjectReq.getEndTime()));
        entity.setExpectedBenefits(StringUtils.isEmpty(modifyProjectReq.getEconomicBenefit()) ? new BigDecimal(0) : new BigDecimal(modifyProjectReq.getEconomicBenefit()));
        entity.setInnovationType(modifyProjectReq.getTypeAttrCode());
        entity.setProjectType(modifyProjectReq.getProjectAttrCode());
        //更新项目信息
        fsipProjectInitiationMapper.updateById(entity);
        //删除原项目属性，重新添加
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("PROJECT_ID", modifyProjectReq.getProjectId());
        fsipProjectInitiationItemMapper.deleteByMap(conditionMap);
        if (!CollectionUtils.isEmpty(modifyProjectReq.getProjectAttrList())) {
            FsipProjectInitiationItemEntity item;
            int sort = 1;
            for (ModifyProjectReq.ProjectAttr attr : modifyProjectReq.getProjectAttrList()) {
                item = new FsipProjectInitiationItemEntity();
                item.setProjectId(modifyProjectReq.getProjectId());
                item.setItemType(attr.getAttrType());
                item.setItemCode(attr.getAttrCode());
                item.setItemName(attr.getAttrName());
                item.setItemValue(attr.getAttrValue());
                item.setSort(sort++);
                fsipProjectInitiationItemMapper.insert(item);
            }
        }
        return ModifyProjectRsp.builder().rspCode("0000").rspMsg("ok").projectId(modifyProjectReq.getProjectId()).build();
    }

    @Transactional
    @Override
    public RelocateProjectRsp relocateProject(RelocateProjectReq relocateProjectReq) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        FsipProjectInitiationEntity entity = fsipProjectInitiationMapper.selectById(relocateProjectReq.getProjectId());
        if (StringUtils.isEmpty(entity.getProjectId())) {
            return RelocateProjectRsp.builder().rspCode("8888").rspMsg("项目[".concat(relocateProjectReq.getProjectId()).concat("]不存在")).projectId(relocateProjectReq.getProjectId()).build();
        }
        entity.setStatus(IFsipConstants.Status.CH);
        entity.setApprNodeCode(IConstants.NodeCode.SQRCH);
        entity.setPendingCode(null);
        entity.setDingTaskId(null);
        //更新项目信息
        fsipProjectInitiationMapper.updateById(entity);

        //插入轨迹表
        flowLogService.addFlowLog(FsipFlowLogEntity.builder().flowType(IFsipConstants.TaskType.LXSQ).extId(relocateProjectReq.getProjectId())
                .nodeCode(IConstants.NodeCode.SQRCH).nodeName(IConstants.NodeCodeName.SQRCH).nodeState(IFsipConstants.Status.CH)
                .dealStaffId(staffInfo.getMainUserId()).dealStaffName(staffInfo.getEmpName()).remark(relocateProjectReq.getRemark()).build());

        pendingTaskService.updatePendingStatus(entity.getPendingCode());

        return RelocateProjectRsp.builder().rspCode("0000").rspMsg("ok").projectId(relocateProjectReq.getProjectId()).build();
    }

    @Override
    public void expertAdviceScore(String expertAdviceId, float score, StaffInfo staffInfo) {
        FsipExpertAdviceEntity expertAdviceEntity = expertAdviceMapper.selectById(Integer.parseInt(expertAdviceId));
        if (ObjectUtils.isEmpty(expertAdviceEntity) || !"0".equals(expertAdviceEntity.getStatus())) {
            throw new BusinessException("500", "查无数据");
        }
        FsipProjectInitiationEntity project = fsipProjectInitiationMapper.selectById(expertAdviceEntity.getTargetId());
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
    public Map<String, List<String>> delProjects(String[] projectIds) {
        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        List<FsipProjectInitiationEntity> projectList = fsipProjectInitiationMapper.selectBatchIds(Arrays.asList(projectIds));

        projectList.stream().forEach(it -> {
                    if (staffInfo.getMainUserId().equals(it.getApplierId()) && (IFsipConstants.Status.TH.equals(it.getStatus()) || IFsipConstants.Status.ZC.equals(it.getStatus()) || IFsipConstants.Status.CH.equals(it.getStatus()))) {//暂存 退回  撤回 可以被删除
                        fsipProjectInitiationMapper.deleteById(it.getProjectId());
                        successList.add(it.getProjectId());
                    } else {
                        failList.add(it.getProjectId());
                    }

                });

        Map<String, List<String>> map = new HashMap<>();
        map.put("successList", successList);
        map.put("failList", failList);
        return map;
    }


    private Map<String, String> getStaticParamMap(String... attrTypes) {
        Map<String, String> staticParamMap = new HashMap<>();
        if (attrTypes != null && attrTypes.length > 0) {
            for (String attrType : attrTypes) {
                FsipStaticParamEntity entity = new FsipStaticParamEntity();
                entity.setAttrType(attrType);
                List<FsipStaticParamEntity> paramEntityList = fsipStaticParamMapper.selectByProp(entity);
                if (!CollectionUtils.isEmpty(paramEntityList)) {
                    paramEntityList.forEach(v -> staticParamMap.put(v.getAttrType().concat("_").concat(v.getAttrCode()), v.getAttrValue()));
                }
            }
        }
        return staticParamMap;
    }

    private String getMapValue(Map<String, String> map, String key) {
        if (CollectionUtils.isEmpty(map) || StringUtils.isEmpty(key)) return "";
        String value = map.get(key);
        return StringUtils.isEmpty(value) ? "" : value;
    }

    private Date stringToDate(String s) {
        Date date = null;
        try {
            date = StringUtils.isEmpty(s) ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
        } catch (ParseException e) {
            log.error("解析时间失败:{}", e.getMessage());
            e.printStackTrace();
        }
        return date;
    }
}