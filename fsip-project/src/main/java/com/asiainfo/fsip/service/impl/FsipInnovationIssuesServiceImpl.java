package com.asiainfo.fsip.service.impl;

import com.asiainfo.fsip.config.VerifyProperties;
import com.asiainfo.fsip.entity.*;
import com.asiainfo.fsip.mapper.fsip.*;
import com.asiainfo.fsip.mapper.tmc.TmcEmployeeMapper;
import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.fsip.service.FsipInnovationIssuesService;
import com.asiainfo.fsip.service.PendingTaskService;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.common.util.TranceNoTool;
import com.asiainfo.mcp.tmc.dingding.service.MessageService;
import com.asiainfo.mcp.tmc.entity.MiniUserEntity;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class FsipInnovationIssuesServiceImpl implements FsipInnovationIssuesService {

    @Resource
    private FsipInnovationIssuesMapper fsipInnovationIssuesMapper;

    @Resource
    private FsipInnovationIssuesScopeMapper fsipInnovationIssuesScopeMapper;

    @Resource
    private FsipInnovationIssuesItemMapper fsipInnovationIssuesItemMapper;

    @Resource
    private FsipInnovationIssuesPartnerMapper fsipInnovationIssuesPartnerMapper;

    @Resource
    private TranceNoTool tranceNoTool;

    @Resource
    private CacheService cacheService;

    @Resource
    private TmcEmployeeMapper tmcEmployeeMapper;

    @Resource
    private PendingTaskService pendingTaskService;

    @Resource
    private FsipInnovationIssuesEvaluateMapper fsipInnovationIssuesEvaluateMapper;

    @Resource
    private FsipInnovationIssuesCommentMapper fsipInnovationIssuesCommentMapper;

    @Resource
    private FsipIssuesPartnerApplyLogMapper fsipIssuesPartnerApplyLogMapper;

    @Resource
    private VerifyProperties verifyProperties;

    @Resource
    private FsipNoticeLogMapper fsipNoticeLogMapper;

    @Resource
    private MessageService messageService;

    @Resource
    private FsipInnovationIssuesFollowMapper fsipInnovationIssuesFollowMapper;


    @Override
    public Object publish(InnovationIssuesPublishReq publishReq) {
        String issuesId = tranceNoTool.getTranceNoSecondUnique("", "yyyyMMddHHmmssSSS", 3);
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        FsipInnovationIssuesEntity entity = FsipInnovationIssuesEntity.builder()
                .issuesId(issuesId)
                .issuesTitle(publishReq.getTitle())
                .content(publishReq.getContent())
                .applierId(staffInfo.getMainUserId())
                .applierName(staffInfo.getEmpName())
                .applierCompanyId(staffInfo.getCompanyId())
                .applierDeptId(staffInfo.getDeptId())
                .applyDate(Calendar.getInstance().getTime())
                .canJoin(publishReq.getCanJoin())
                .partnerNum(publishReq.getPartnerNum() == null ? null : Long.valueOf(publishReq.getPartnerNum().toString()))
                .build();
        fsipInnovationIssuesMapper.insert(entity);
        if (publishReq.getScope() != null
                && !StringUtils.isEmpty(publishReq.getScope().getType())
                && !CollectionUtils.isEmpty(publishReq.getScope().getValues())) {
            final List<String> sendDingTextStaffIdArray = new ArrayList<>();
            for (InnovationIssuesPublishReq.Value value : publishReq.getScope().getValues()) {
                FsipInnovationIssuesScopeEntity scopeEntity = FsipInnovationIssuesScopeEntity.builder()
                        .issuesId(issuesId)
                        .scopeType(publishReq.getScope().getType())
                        .scopeId(value.getCode())
                        .scopeName(value.getName()).build();
                fsipInnovationIssuesScopeMapper.insert(scopeEntity);
                if ("01".equals(publishReq.getScope().getType())) {
                    List<String> codeList = new ArrayList<>();
                    codeList.add(value.getCode());
                    List<MiniUserEntity> userEntityList = tmcEmployeeMapper.selectByProp(null, null, codeList);
                    if (!CollectionUtils.isEmpty(userEntityList)) {
                        userEntityList.forEach(v -> sendDingTextStaffIdArray.add(v.getAccountCode()));
                    }
                } else if ("02".equals(publishReq.getScope().getType())) {
                    sendDingTextStaffIdArray.add(value.getCode());
                }
            }

            CompletableFuture.supplyAsync(() -> {
                if (!CollectionUtils.isEmpty(sendDingTextStaffIdArray)) {
                    for (String staffId : sendDingTextStaffIdArray) {
                        Long dingTaskId = pendingTaskService.sendDingLinkMessage(verifyProperties.getIssuesPublishPcUrl().concat(issuesId)
                                , publishReq.getTitle(), publishReq.getContent(), staffId);
                        FsipNoticeLogEntity noticeLog = FsipNoticeLogEntity.builder()
                                .issuesId(issuesId).staffId(staffId).msgType("FBTZ").dingTaskId(dingTaskId).build();
                        fsipNoticeLogMapper.insert(noticeLog);
                    }
                }
                return true;
            });
        }

        if (!CollectionUtils.isEmpty(publishReq.getAttrList())) {
            for (InnovationIssuesPublishReq.Attr attr : publishReq.getAttrList()) {
                FsipInnovationIssuesItemEntity itemEntity = FsipInnovationIssuesItemEntity.builder()
                        .issuesId(issuesId)
                        .attrType(attr.getAttrType())
                        .attrCode(attr.getAttrCode())
                        .attrValue(attr.getAttrValue()).build();
                fsipInnovationIssuesItemMapper.insert(itemEntity);
            }
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", RspHelp.SUCCESS_CODE);
        resultMap.put("message", RspHelp.SUCCESS_DESC);
        resultMap.put("issuesId", issuesId);
        return resultMap;
    }

    @Override
    public InnovationIssuesDetailRsp detailQuery(InnovationIssuesDetailReq detailReq) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        String issuesId = detailReq.getIssuesId();
        FsipInnovationIssuesEntity entity = fsipInnovationIssuesMapper.selectById(issuesId);
        if (entity == null)
            throw new BusinessException(RspHelp.PARAM_VALID_ERROR_CODE, "没有查询到[".concat(issuesId).concat("]信息"));
        QueryWrapper<FsipInnovationIssuesPartnerEntity> queryPartnerWrapper = new QueryWrapper<>();
        queryPartnerWrapper.eq("ISSUES_ID", issuesId);
        final List<FsipInnovationIssuesPartnerEntity> partnerList = fsipInnovationIssuesPartnerMapper.selectList(queryPartnerWrapper);
        List<InnovationIssuesDetailRsp.Partner> rspPartnerList = new ArrayList<>();
        // 从缓存读取公司和部门列表
        Map<String, String> companyMap = cacheService.getCompanyMap();
        if (!CollectionUtils.isEmpty(partnerList)) {
            partnerList.forEach(v -> rspPartnerList.add(InnovationIssuesDetailRsp.Partner.builder()
                    .partnerId(v.getPartnerId())
                    .partnerName(v.getPartnerName())
                    .partnerCompanyId(v.getCompanyId())
                    .partnerDepartId(v.getDeptId())
                    .partnerCompanyName(companyMap.get(v.getCompanyId()))
                    .partnerDepartName(cacheService.getDepartment(v.getDeptId()).getDeptName())
                    .build())
            );
        }
        int likeNum = 0, dislikeNum = 0;
        String evaluateFlag = "0";
        String evaluateType = null;
        QueryWrapper<FsipInnovationIssuesEvaluateEntity> queryEvaluateWrapper = new QueryWrapper<>();
        queryEvaluateWrapper.eq("ISSUES_ID", issuesId);
        List<FsipInnovationIssuesEvaluateEntity> evaluateEntityList = fsipInnovationIssuesEvaluateMapper.selectList(queryEvaluateWrapper);
        if (!CollectionUtils.isEmpty(evaluateEntityList)) {
            for (FsipInnovationIssuesEvaluateEntity evaluate : evaluateEntityList) {
                if ("LIKE".equals(evaluate.getEvaluateType())) {
                    likeNum++;
                } else if ("DISLIKE".equals(evaluate.getEvaluateType())) {
                    dislikeNum++;
                }
                if (evaluate.getStaffId().equals(staffInfo.getMainUserId())) {
                    evaluateFlag = "1";
                    evaluateType = evaluate.getEvaluateType();
                }
            }
        }
        InnovationIssuesPublishReq.Scope scope = null;
        QueryWrapper<FsipInnovationIssuesScopeEntity> queryScopeWrapper = new QueryWrapper<>();
        queryScopeWrapper.eq("ISSUES_ID", issuesId);
        List<FsipInnovationIssuesScopeEntity> scopeEntityList = fsipInnovationIssuesScopeMapper.selectList(queryScopeWrapper);
        if (!CollectionUtils.isEmpty(scopeEntityList)) {
            String type = null;
            List<InnovationIssuesPublishReq.Value> values = new ArrayList<>();
            for (FsipInnovationIssuesScopeEntity scopeEntity : scopeEntityList) {
                if (StringUtils.isEmpty(type)) type = scopeEntity.getScopeType();
                values.add(InnovationIssuesPublishReq.Value.builder().code(scopeEntity.getScopeId()).name(scopeEntity.getScopeName()).build());
            }
            scope = InnovationIssuesPublishReq.Scope.builder().type(type).values(values).build();
        }
        final List<InnovationIssuesDetailRsp.Comment> rspCommentList = new ArrayList<>();
        QueryWrapper<FsipInnovationIssuesCommentEntity> queryCommentWrapper = new QueryWrapper<>();
        queryCommentWrapper.eq("ISSUES_ID", issuesId);
        List<FsipInnovationIssuesCommentEntity> commentEntityList = fsipInnovationIssuesCommentMapper.selectList(queryCommentWrapper);
        if (!CollectionUtils.isEmpty(commentEntityList)) {
            commentEntityList.forEach(v -> rspCommentList.add(InnovationIssuesDetailRsp.Comment.builder()
                    .commentId(v.getCommentId())
                    .content(v.getContent())
                    .staffId(v.getStaffId())
                    .staffName(v.getStaffName())
                    .commentDate(v.getCommentTime() == null ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(v.getCommentTime())).build()));
        }
        final List<InnovationIssuesDetailRsp.Attr> rspAttrList = new ArrayList<>();
        QueryWrapper<FsipInnovationIssuesItemEntity> queryAttrWrapper = new QueryWrapper<>();
        queryAttrWrapper.eq("ISSUES_ID", issuesId);
        List<FsipInnovationIssuesItemEntity> itemEntityList = fsipInnovationIssuesItemMapper.selectList(queryAttrWrapper);
        if (!CollectionUtils.isEmpty(itemEntityList)) {
            itemEntityList.forEach(v -> rspAttrList.add(InnovationIssuesDetailRsp.Attr.builder()
                    .attrType(v.getAttrType())
                    .attrCode(v.getAttrCode())
                    .attrValue(v.getAttrValue()).build()));
        }
        String followFlag = "0";
        QueryWrapper<FsipInnovationIssuesFollowEntity> queryFollowWrapper = new QueryWrapper<>();
        queryFollowWrapper.eq("ISSUES_ID", issuesId);
        queryFollowWrapper.eq("STAFF_ID", staffInfo.getMainUserId());
        List<FsipInnovationIssuesFollowEntity> followEntityList = fsipInnovationIssuesFollowMapper.selectList(queryFollowWrapper);
        if (!CollectionUtils.isEmpty(followEntityList)) {
            followFlag = "1";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<FsipIssuesPartnerApplyLogEntity> queryApplyLogWrapper = new QueryWrapper<>();
        queryApplyLogWrapper.eq("ISSUES_ID", issuesId);
        List<FsipIssuesPartnerApplyLogEntity> applyLogEntityList = fsipIssuesPartnerApplyLogMapper.selectList(queryApplyLogWrapper);
        FsipIssuesPartnerApplyLogEntity currentApplyLog = null;
        final List<InnovationIssuesDetailRsp.ApplyLog> applyLogList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(applyLogEntityList)) {
            for (FsipIssuesPartnerApplyLogEntity applyLogEntity : applyLogEntityList) {
                if (applyLogEntity.getPartnerId().equals(staffInfo.getMainUserId())
                        && ("00".equals(applyLogEntity.getApplyState()) || "01".equals(applyLogEntity.getApplyState()))) {
                    currentApplyLog = applyLogEntity;
                }
                applyLogList.add(
                        InnovationIssuesDetailRsp.ApplyLog.builder()
                                .partnerId(applyLogEntity.getPartnerId())
                                .partnerName(applyLogEntity.getPartnerName())
                                .applyReason(applyLogEntity.getJoinReason())
                                .applyDate(applyLogEntity.getApplyTime() == null ? "" : format.format(applyLogEntity.getApplyTime()))
                                .applyState(applyLogEntity.getApplyState())
                                .replyContent(applyLogEntity.getReplyContent())
                                .replyDate(applyLogEntity.getReplyTime() == null ? "" : format.format(applyLogEntity.getReplyTime()))
                                .build()
                );
            }
        }

        return InnovationIssuesDetailRsp.builder()
                .title(entity.getIssuesTitle())
                .content(entity.getContent())
                .canJoin(entity.getCanJoin())
                .partnerNum(entity.getPartnerNum() == null ? 0 : Integer.parseInt(entity.getPartnerNum().toString()))
                .havePartnerNum(rspPartnerList.size())
                .applierId(entity.getApplierId())
                .applierName(entity.getApplierName())
                .applyCompanyId(entity.getApplierCompanyId())
                .applyCompanyName(cacheService.getCompanyMap().get(entity.getApplierCompanyId()))
                .applyDeptId(entity.getApplierDeptId())
                .applyDate(entity.getApplyDate() == null ? "" : format.format(entity.getApplyDate()))
                .followFlag(followFlag)
                .evaluateFlag(evaluateFlag)
                .evaluateType(evaluateType)
                .applyPartnerFlag(currentApplyLog == null ? "0" : "1")
                .applyPartnerDate(currentApplyLog == null ? "" : (currentApplyLog.getApplyTime() == null ? "" : format.format(currentApplyLog.getApplyTime())))
                .applyPartnerReason(currentApplyLog == null ? "" : currentApplyLog.getJoinReason())
                .partnerApprovalOpinion(currentApplyLog == null ? "" : currentApplyLog.getReplyContent())
                .likeNum(likeNum)
                .dislikeNum(dislikeNum)
                .scope(scope)
                .partnerList(rspPartnerList)
                .commentList(rspCommentList)
                .attrList(rspAttrList)
                .applyLogList(applyLogList).build();
    }

    @Override
    public String partnerJoin(IssuesJoinPartnerReq joinPartnerReq) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        FsipInnovationIssuesEntity issuesEntity = fsipInnovationIssuesMapper.selectById(joinPartnerReq.getIssuesId());
        if (issuesEntity == null) {
            throw new BusinessException("8888", "创新议题[".concat(joinPartnerReq.getIssuesId()).concat("]不存在"));
        }

        String issuesId = joinPartnerReq.getIssuesId();
        String partnerId = staffInfo.getMainUserId();
        QueryWrapper<FsipIssuesPartnerApplyLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ISSUES_ID", issuesId);
        queryWrapper.eq("PARTNER_ID", partnerId);
        List<FsipIssuesPartnerApplyLogEntity> applyLogEntityList = fsipIssuesPartnerApplyLogMapper.selectList(queryWrapper);
        boolean isHaveApply = false;
        if (!CollectionUtils.isEmpty(applyLogEntityList)) {
            for (FsipIssuesPartnerApplyLogEntity entity : applyLogEntityList) {
                if ("01".equals(entity.getApplyState())) {
                    isHaveApply = true;
                    break;
                }
            }
        }
        if (isHaveApply) throw new BusinessException("8888", "您已存在未审批的申请记录,不能重复提交申请");
        String applyLogId = tranceNoTool.getTranceNoSecondUnique("APP", "yyyyMMddHHmmssSSS", 3);
        FsipIssuesPartnerApplyLogEntity entity = FsipIssuesPartnerApplyLogEntity.builder()
                .id(applyLogId)
                .issuesId(joinPartnerReq.getIssuesId())
                .partnerId(staffInfo.getMainUserId())
                .partnerName(staffInfo.getEmpName())
                .companyId(staffInfo.getCompanyId())
                .deptId(staffInfo.getDeptId())
                .joinReason(joinPartnerReq.getJoinReason())
                .applyTime(Calendar.getInstance().getTime())
                .applyState("01").build();

        UrlModel urlModel = UrlModel.builder().mobileUrl(verifyProperties.getIssuesjoinPcUrl().replaceAll("#APPLIER#", partnerId))
                .title(issuesEntity.getIssuesTitle()).build();

        fsipIssuesPartnerApplyLogMapper.insert(entity);
        Long dingTaskId = pendingTaskService.sendDingOaMessage(issuesEntity.getIssuesId(), urlModel,
                issuesEntity.getIssuesTitle(),
                issuesEntity.getApplierId(),
                staffInfo);
        log.info("complete send dingding message , dingTaskId = " + dingTaskId + ", issuesId = " + issuesId);
        FsipNoticeLogEntity noticeLog = FsipNoticeLogEntity.builder()
                .issuesId(issuesEntity.getIssuesId()).staffId(issuesEntity.getApplierId()).msgType("SQTZ").dingTaskId(dingTaskId).applyId(applyLogId).build();
        fsipNoticeLogMapper.insert(noticeLog);
        return applyLogId;
    }

    @Override
    public PageInfo<InnovationIssuesListResp> selIssuesList(PageReq<InnovationIssuesListReq> req) throws Exception {
        InnovationIssuesListReq innovationIssuesListReq = req.getReqParam();
        if (!("01".equals(innovationIssuesListReq.getSelType())
                || "02".equals(innovationIssuesListReq.getSelType()))) {
            throw new BusinessException("请传入正确的查询类型");
        }


        if ("01".equals(innovationIssuesListReq.getSelType())) {
            if (!("01".equals(innovationIssuesListReq.getSelfSelType())
                    || "02".equals(innovationIssuesListReq.getSelfSelType())
                    || "03".equals(innovationIssuesListReq.getSelfSelType()))) {
                throw new BusinessException("selType为01的时候，selfSelType的值必须要传入，01：我发布的 02：我参与的 03：我关注的");
            }
        }

        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        innovationIssuesListReq.setApplierDeptId(staffInfo.getDeptId());
        innovationIssuesListReq.setApplierId(staffInfo.getMainUserId());
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        PageInfo<InnovationIssuesListResp> pageInfo = new PageInfo<>();
        List<InnovationIssuesListResp> retList
                = fsipInnovationIssuesMapper.getIssuesList(innovationIssuesListReq);
        if (!CollectionUtils.isEmpty(retList)) {
            retList.parallelStream().forEach(item -> {
                        item.setApplierDeptId(cacheService.getDepartment(item.getApplierDeptId()).getDeptName());
                        item.setApplierCompanyId(cacheService.getCompanyMap().get(item.getApplierCompanyId()));
                        item.setCanJoin(cacheService.getParamValue("CAN_JOIN", item.getCanJoin()));
                    }
            );
        }
        if (null != retList && !retList.isEmpty()) {
            pageInfo = PageInfo.of(retList);
        }
        return pageInfo;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public InnovationIssuesUpdateResp updateIssues(InnovationIssuesUpdateReq req) throws Exception {
        if (!StringUtils.isEmpty(req.getIssuesId())) {
            FsipInnovationIssuesEntity fsipInnovationIssuesEntity = fsipInnovationIssuesMapper.selectById(req.getIssuesId());
            if (null == fsipInnovationIssuesEntity) {
                return InnovationIssuesUpdateResp.builder()
                        .respCode("8888")
                        .respMsg("请传入编码未找到相关需要修改的数据")
                        .build();
            }
            if ("0".equals(req.getCanJoin()) && "1".equals(fsipInnovationIssuesEntity.getCanJoin())) {
                if (null != fsipInnovationIssuesEntity.getPartnerNum() && fsipInnovationIssuesEntity.getPartnerNum() > 0) {
                    List<FsipInnovationIssuesPartnerEntity> partnerEntityList = getPartnerEntityList(req.getIssuesId());
                    if (null != partnerEntityList && !partnerEntityList.isEmpty()) {
                        return InnovationIssuesUpdateResp.builder()
                                .respCode("0002")
                                .respMsg("已经有" + partnerEntityList.size() + "名合伙人加入，是否要求合伙不能修改为否")
                                .build();
                    }
                }
            }

            if ("1".equals(req.getCanJoin()) && null != req.getPartnerNum() && req.getPartnerNum() > 0) {
                List<FsipInnovationIssuesPartnerEntity> partnerEntityList = getPartnerEntityList(req.getIssuesId());
                if (null != partnerEntityList && !partnerEntityList.isEmpty()) {
                    if (partnerEntityList.size() > req.getPartnerNum()) {
                        return InnovationIssuesUpdateResp.builder()
                                .respCode("0002")
                                .respMsg("已经有" + partnerEntityList.size() + "名合伙人加入，合伙人人数不能修改比现在加入的少")
                                .build();
                    }
                }
            }
            if (!StringUtils.isEmpty(req.getPartnerNum())) {
                fsipInnovationIssuesEntity.setPartnerNum(Long.valueOf(req.getPartnerNum()));
            }
            if (!StringUtils.isEmpty(req.getCanJoin())) {
                fsipInnovationIssuesEntity.setCanJoin(req.getCanJoin());
            }
            if (!StringUtils.isEmpty(req.getTitle())) {
                fsipInnovationIssuesEntity.setIssuesTitle(req.getTitle());
            }
            if (!StringUtils.isEmpty(req.getContent())) {
                fsipInnovationIssuesEntity.setContent(req.getContent());
            }
            int upCount = fsipInnovationIssuesMapper.updateById(fsipInnovationIssuesEntity);

            InnovationIssuesUpdateReq.Scope scope = req.getScope();
            List<InnovationIssuesUpdateReq.Value> scopeValueList = scope.getValues();
            if (null != scope && null != scopeValueList && !scopeValueList.isEmpty()) {
                Map<String, Object> scopeMap = new HashMap<String, Object>();
                scopeMap.put("ISSUES_ID", req.getIssuesId());
                fsipInnovationIssuesScopeMapper.deleteByMap(scopeMap);

                for (InnovationIssuesUpdateReq.Value value : scope.getValues()) {
                    FsipInnovationIssuesScopeEntity entity = new FsipInnovationIssuesScopeEntity();
                    entity.setIssuesId(req.getIssuesId());
                    entity.setScopeType(scope.getType());
                    entity.setScopeName(value.getName());
                    entity.setScopeId(value.getCode());
                    fsipInnovationIssuesScopeMapper.insert(entity);
                }
                CompletableFuture.supplyAsync(() -> {
                    try {
                        dingDingMessageSend(req.getIssuesId(), req.getTitle(), req.getContent(), scope);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
                });
            }
            if (upCount > 0) {
                return InnovationIssuesUpdateResp.builder()
                        .respCode("0000")
                        .respMsg("修改成功")
                        .build();
            } else {
                return InnovationIssuesUpdateResp.builder()
                        .respCode("8888")
                        .respMsg("修改失败")
                        .build();
            }
        }
        return InnovationIssuesUpdateResp.builder()
                .respCode("8888")
                .respMsg("请传入正确的参数")
                .build();
    }


    public List<FsipInnovationIssuesPartnerEntity> getPartnerEntityList(String issuesId) {
        Map<String, Object> partnerMap = new HashMap<String, Object>();
        partnerMap.put("ISSUES_ID", issuesId);
        return fsipInnovationIssuesPartnerMapper.selectByMap(partnerMap);
    }


    /**
     * 钉钉消息推送，并更新表
     *
     * @param issuesId
     * @param title
     * @param scope
     */
    public void dingDingMessageSend(String issuesId, String title, String content, InnovationIssuesUpdateReq.Scope scope) throws Exception {
        List<String> sendDingTextStaffIdArray = new ArrayList<>();
        List<String> codeList = new ArrayList<>();
        for (InnovationIssuesUpdateReq.Value value : scope.getValues()) {
            codeList.add(value.getCode());
        }
        if ("01".equals(scope.getType())) {
            List<MiniUserEntity> userEntityList = tmcEmployeeMapper.selectByProp(null, null, codeList);
            if (!CollectionUtils.isEmpty(userEntityList)) {
                userEntityList.forEach(temp -> sendDingTextStaffIdArray.add(temp.getAccountCode()));
            }
        } else if ("02".equals(scope.getType())) {
            codeList.forEach(temp -> sendDingTextStaffIdArray.add(temp));
        }
        if (!CollectionUtils.isEmpty(sendDingTextStaffIdArray)) {
            //查出已经发送的信息
            Map<String, Object> noticeLogMap = new HashMap<String, Object>();
            noticeLogMap.put("ISSUES_ID", issuesId);
            List<FsipNoticeLogEntity> scopeEntityList = fsipNoticeLogMapper.selectByMap(noticeLogMap);
            for (String staffId : sendDingTextStaffIdArray) {
                if (null != scopeEntityList && !scopeEntityList.isEmpty()) {
                    boolean upFlag = true;
                    for (FsipNoticeLogEntity entity : scopeEntityList) {
                        if (staffId.equals(entity.getStaffId())) {
                            upFlag = false;
                            break;
                        }
                    }
                    if (upFlag) {
                        Long dingTaskId = pendingTaskService.sendDingLinkMessage(verifyProperties.getIssuesPublishPcUrl(), title, content, staffId);
                        FsipNoticeLogEntity noticeLog = FsipNoticeLogEntity.builder()
                                .issuesId(issuesId).staffId(staffId).msgType("FBTZ").dingTaskId(dingTaskId).build();
                        fsipNoticeLogMapper.insert(noticeLog);
                    }
                } else {
                    Long dingTaskId = pendingTaskService.sendDingLinkMessage(verifyProperties.getIssuesPublishPcUrl(), title, content, staffId);
                    FsipNoticeLogEntity noticeLog = FsipNoticeLogEntity.builder()
                            .issuesId(issuesId).staffId(staffId).msgType("FBTZ").dingTaskId(dingTaskId).build();
                    fsipNoticeLogMapper.insert(noticeLog);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InnovationIssuesDelResp delIssues(FsipInnovationIssuesEntity req) throws Exception {
        String issuesId = req.getIssuesId();
        if (!StringUtils.isEmpty(issuesId)) {
            FsipInnovationIssuesEntity fsipInnovationIssuesEntity = fsipInnovationIssuesMapper.selectById(issuesId);
            if (null == fsipInnovationIssuesEntity) {
                return InnovationIssuesDelResp.builder()
                        .respCode("8888")
                        .respMsg("请传入编码未找到相关需要删除的数据")
                        .build();
            }

            //判断如果有合伙人加入了，则不让删除
            List<FsipInnovationIssuesPartnerEntity> partnerEntityList = getPartnerEntityList(req.getIssuesId());
            if (null != partnerEntityList && !partnerEntityList.isEmpty()) {
                return InnovationIssuesDelResp.builder()
                        .respCode("8888")
                        .respMsg(String.format("已经有%s个合伙人加入了，不能删除该创新议题", partnerEntityList.size()))
                        .build();
            } else {
                Map<String, Object> delMap = new HashMap<String, Object>();
                delMap.put("ISSUES_ID", issuesId);
                fsipInnovationIssuesMapper.deleteById(issuesId);
                fsipInnovationIssuesScopeMapper.deleteByMap(delMap);

                return InnovationIssuesDelResp.builder()
                        .respCode("0000")
                        .respMsg("删除成功")
                        .build();
            }
        }
        return InnovationIssuesDelResp.builder()
                .respCode("8888")
                .respMsg("请传入正确的参数")
                .build();
    }

    @Override
    public Object updateDingState(UpdateDingStateReq updateDingStateReq) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        QueryWrapper<FsipNoticeLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ISSUES_ID", updateDingStateReq.getIssuesId());
        queryWrapper.eq("MSG_TYPE", updateDingStateReq.getMessageType());
        queryWrapper.eq("STAFF_ID", staffInfo.getMainUserId());
        List<FsipNoticeLogEntity> logEntities = fsipNoticeLogMapper.selectList(queryWrapper);
        Long dingTaskId = null;
        if (!CollectionUtils.isEmpty(logEntities)) {
            dingTaskId = logEntities.get(0).getDingTaskId();
            pendingTaskService.updateDingNotifyStatus(dingTaskId, "已阅");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", "0000");
        resultMap.put("message", "操作成功");
        resultMap.put("dingTaskId", dingTaskId);
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InnovationIssuesPartnerApplyResp partnerApply(InnovationIssuesPartnerApplyReq req) throws Exception {
        Map<String, Object> selMap = new HashMap<>();
        selMap.put("ISSUES_ID", req.getIssuesId());
        selMap.put("PARTNER_ID", req.getPartnerId());
        selMap.put("APPLY_STATE", "01");//查询待处理数据
        List<FsipIssuesPartnerApplyLogEntity> applyLoglist = fsipIssuesPartnerApplyLogMapper.selectByMap(selMap);
        if (null != applyLoglist && !applyLoglist.isEmpty()) {
            FsipInnovationIssuesEntity fsipInnovationIssuesEntity = fsipInnovationIssuesMapper.selectById(req.getIssuesId());
            FsipIssuesPartnerApplyLogEntity fsipIssuesPartnerApplyLogEntity = applyLoglist.get(0);
            fsipIssuesPartnerApplyLogEntity.setApplyState(req.getApplyState());
            fsipIssuesPartnerApplyLogEntity.setReplyContent(req.getReplyContent());
            fsipIssuesPartnerApplyLogMapper.updateById(fsipIssuesPartnerApplyLogEntity);

            if ("00".equals(req.getApplyState())) {
                FsipInnovationIssuesPartnerEntity fiipe = new FsipInnovationIssuesPartnerEntity();
                fiipe.setIssuesId(fsipIssuesPartnerApplyLogEntity.getIssuesId());
                fiipe.setPartnerId(fsipIssuesPartnerApplyLogEntity.getPartnerId());
                fiipe.setPartnerName(fsipIssuesPartnerApplyLogEntity.getPartnerName());
                fiipe.setDeptId(fsipIssuesPartnerApplyLogEntity.getDeptId());
                fiipe.setCompanyId(fsipIssuesPartnerApplyLogEntity.getCompanyId());
                fsipInnovationIssuesPartnerMapper.insert(fiipe);
            }
            Map<String, Object> noticeLogMap = new HashMap<>();
            noticeLogMap.put("ISSUES_ID", req.getIssuesId());
            noticeLogMap.put("MSG_TYPE", "SQTZ");
            noticeLogMap.put("STAFF_ID", req.getPartnerId());
            noticeLogMap.put("APPLY_ID", fsipIssuesPartnerApplyLogEntity.getId());

            List<FsipNoticeLogEntity> noticeLoglist = fsipNoticeLogMapper.selectByMap(noticeLogMap);
            if (null != noticeLoglist && !noticeLoglist.isEmpty()) {
                messageService.recallMsg(Long.valueOf(noticeLoglist.get(0).getDingTaskId()));
            }

            String message = "";
            if ("00".equals(req.getApplyState())) {
//                message = "您的申请加入[" + fsipInnovationIssuesEntity.getIssuesTitle() + "]，审核通过，恭喜您成为我们[" + fsipInnovationIssuesEntity.getIssuesTitle() + "]创新议题的一员";
                message = "您的申请加入[" + fsipInnovationIssuesEntity.getIssuesTitle() + "]创新议题，审核通过";
            }
            if ("02".equals(req.getApplyState())) {
                message = "您的申请加入[" + fsipInnovationIssuesEntity.getIssuesTitle() + "]创新议题，审核被拒绝，拒绝原因：" + req.getReplyContent();
//                message = "您的申请加入[" + fsipInnovationIssuesEntity.getIssuesTitle() + "]，审核失败，很遗憾不能与您一起参与[" + fsipInnovationIssuesEntity.getIssuesTitle() + "],期待下一个议题我们有合作的机会";
            }
            pendingTaskService.sendDingTextMessage(message, fsipIssuesPartnerApplyLogEntity.getPartnerId());
            return InnovationIssuesPartnerApplyResp.builder().respCode("0000").respMsg("success").build();
        } else {
            return InnovationIssuesPartnerApplyResp.builder().respCode("0001").respMsg("根据传入的信息未查询申请记录").build();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InnovationIssuesCommentResp issuesComment(InnovationIssuesCommentReq req) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        String issuesId = req.getIssuesId();
        if (StringUtils.isEmpty(issuesId)) {
            return InnovationIssuesCommentResp.builder().respCode("0001").respMsg("请传入创新议题编码").build();
        }

        if (StringUtils.isEmpty(req.getDelCommentId())
                && StringUtils.isEmpty(req.getComment())
                && StringUtils.isEmpty(req.getLike())
                && StringUtils.isEmpty(req.getDisLike())
                && StringUtils.isEmpty(req.getFollow())
                && StringUtils.isEmpty(req.getDelCommentId())
                && StringUtils.isEmpty(req.getCancelFollow())
                && StringUtils.isEmpty(req.getCancelLike())
                && StringUtils.isEmpty(req.getCancelDisLike())) {
            return InnovationIssuesCommentResp.builder()
                    .respCode("8888")
                    .respMsg("请传入必要的业务参数")
                    .build();
        }

        FsipInnovationIssuesEntity fsipInnovationIssuesEntity = fsipInnovationIssuesMapper.selectById(issuesId);
        if (null == fsipInnovationIssuesEntity) {
            return InnovationIssuesCommentResp.builder()
                    .respCode("8888")
                    .respMsg("请传入编码未找到相关需要修改的数据")
                    .build();
        }

        int upCount = 0;
        //评论
        if (!StringUtils.isEmpty(req.getComment())) {
            FsipInnovationIssuesCommentEntity fiic = new FsipInnovationIssuesCommentEntity();
            fiic.setIssuesId(issuesId);
            fiic.setCommentTime(new Date());
            fiic.setContent(req.getComment());
            fiic.setStaffId(staffInfo.getMainUserId());
            fiic.setStaffName(staffInfo.getEmpName());
            fiic.setCommentId(tranceNoTool.getTimeId("C"));
            upCount = fsipInnovationIssuesCommentMapper.insert(fiic);
        }

        //删除评论
        if (!StringUtils.isEmpty(req.getDelCommentId())) {
            upCount = fsipInnovationIssuesCommentMapper.deleteById(req.getDelCommentId());
        }

        if (!StringUtils.isEmpty(req.getLike()) && !StringUtils.isEmpty(req.getDisLike())) {
            return InnovationIssuesCommentResp.builder()
                    .respCode("8888")
                    .respMsg("点赞和踩不能同时提交")
                    .build();
        }

        if (!StringUtils.isEmpty(req.getLike()) && !StringUtils.isEmpty(req.getCancelLike())) {
            return InnovationIssuesCommentResp.builder()
                    .respCode("8888")
                    .respMsg("点赞和取消点赞不能同时提交")
                    .build();
        }

        //点赞
        if (!StringUtils.isEmpty(req.getLike())) {
            FsipInnovationIssuesEvaluateEntity fiiee = new FsipInnovationIssuesEvaluateEntity();
            fiiee.setIssuesId(issuesId);
            fiiee.setEvaluateId(tranceNoTool.getTimeId("E"));
            fiiee.setEvaluateType("LIKE");
            fiiee.setStaffId(staffInfo.getMainUserId());
            fiiee.setStaffName(staffInfo.getEmpName());
            upCount = fsipInnovationIssuesEvaluateMapper.insert(fiiee);
        }

        //取消点赞
        if (!StringUtils.isEmpty(req.getCancelLike())) {
            Map<String, Object> delMap = new HashMap<String, Object>();
            delMap.put("ISSUES_ID", issuesId);
            delMap.put("EVALUATE_TYPE", "LIKE");
            delMap.put("STAFF_ID", staffInfo.getMainUserId());
            upCount = fsipInnovationIssuesEvaluateMapper.deleteByMap(delMap);
        }

        if (!StringUtils.isEmpty(req.getDisLike()) && !StringUtils.isEmpty(req.getCancelDisLike())) {
            return InnovationIssuesCommentResp.builder()
                    .respCode("8888")
                    .respMsg("踩和取消踩不能同时提交")
                    .build();
        }
        //踩
        if (!StringUtils.isEmpty(req.getDisLike())) {
            FsipInnovationIssuesEvaluateEntity fiiee = new FsipInnovationIssuesEvaluateEntity();
            fiiee.setIssuesId(issuesId);
            fiiee.setEvaluateId(tranceNoTool.getTimeId("E"));
            fiiee.setEvaluateType("DISLIKE");
            fiiee.setStaffId(staffInfo.getMainUserId());
            fiiee.setStaffName(staffInfo.getEmpName());
            upCount = fsipInnovationIssuesEvaluateMapper.insert(fiiee);
        }
        //取消踩
        if (!StringUtils.isEmpty(req.getCancelDisLike())) {
            Map<String, Object> delMap = new HashMap<String, Object>();
            delMap.put("ISSUES_ID", issuesId);
            delMap.put("EVALUATE_TYPE", "DISLIKE");
            delMap.put("STAFF_ID", staffInfo.getMainUserId());
            upCount = fsipInnovationIssuesEvaluateMapper.deleteByMap(delMap);
        }

        if (!StringUtils.isEmpty(req.getFollow()) && !StringUtils.isEmpty(req.getCancelFollow())) {
            return InnovationIssuesCommentResp.builder()
                    .respCode("8888")
                    .respMsg("关注和取消关注不能同时提交")
                    .build();
        }

        //关注
        if (!StringUtils.isEmpty(req.getFollow())) {
            FsipInnovationIssuesFollowEntity fiife = new FsipInnovationIssuesFollowEntity();
            fiife.setIssuesId(issuesId);
            fiife.setStaffId(staffInfo.getMainUserId());
            fiife.setStaffName(staffInfo.getEmpName());
            upCount = fsipInnovationIssuesFollowMapper.insert(fiife);
        }

        //取消关注
        if (!StringUtils.isEmpty(req.getCancelFollow())) {
            Map<String, Object> delMap = new HashMap<String, Object>();
            delMap.put("ISSUES_ID", issuesId);
            delMap.put("STAFF_ID", staffInfo.getMainUserId());
            upCount = fsipInnovationIssuesFollowMapper.deleteByMap(delMap);
        }

        if (upCount > 0) {
            return InnovationIssuesCommentResp.builder()
                    .respCode("0000").respMsg("数据更新成功")
                    .build();
        } else {
            return InnovationIssuesCommentResp.builder()
                    .respCode("8888").respMsg("数据更新失败")
                    .build();
        }
    }

    @Override
    public InnovationIssuesPartnerApplyResp partnerApplySel(InnovationIssuesPartnerApplyReq req) {
        Map<String, Object> selMap = new HashMap<String, Object>();
        selMap.put("ISSUES_ID", req.getIssuesId());
        selMap.put("PARTNER_ID", req.getPartnerId());
        selMap.put("APPLY_STATE", "01");//查询待处理数据
        List<FsipIssuesPartnerApplyLogEntity> applyLoglist = fsipIssuesPartnerApplyLogMapper.selectByMap(selMap);
        if (null != applyLoglist && !applyLoglist.isEmpty()) {
            FsipIssuesPartnerApplyLogEntity entity = applyLoglist.get(0);
            return InnovationIssuesPartnerApplyResp.builder().issuesId(entity.getIssuesId())
                    .partnerId(entity.getPartnerId())
                    .deptId(cacheService.getDepartment(entity.getDeptId()).getDeptName())
                    .companyId(cacheService.getCompanyMap().get(entity.getCompanyId()))
                    .partnerName(entity.getPartnerName())
                    .applyTime(entity.getApplyTime())
                    .joinReason(entity.getJoinReason())
                    .respCode("0000")
                    .respMsg("SUCCESS").build();
        }
        return InnovationIssuesPartnerApplyResp.builder().respCode("8888").respMsg("根据参数未查询到数据").build();
    }
}
