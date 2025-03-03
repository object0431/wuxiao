package com.asiainfo.fsip.service.impl;

import com.asiainfo.fsip.entity.*;
import com.asiainfo.fsip.mapper.fsip.*;
import com.asiainfo.fsip.model.ProjectProcurementInfoModel;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author BEJSON
 * @description project_procurement_info服务层
 * @date 2024-04-15
 */
@Service
public class ProjectProcurementInfoService extends ServiceImpl<ProjectProcurementInfoMapper, ProjectProcurementInfo> {

    @Autowired
    private ProjectProcurementDraftingInfoMapper draftingInfoMapper;
    @Autowired
    private ProjectProcurementMeetingInfoMapper meetingInfoMapper;
    @Autowired
    private ProjectProcurementReviewMainMapper reviewMainMapper;
    @Autowired
    private ProjectProcurementReviewSubMapper reviewSubMapper;
    @Autowired
    private ProjectProcurementContractInfoMapper contractMapper;
    @Autowired
    private ProjectProcurementAttachService attachService;
    @Autowired
    private ProjectProcurementEnrollmentTimeMapper enrollmentTimeMapper;
    @Autowired
    private ProjectProcurementProposalApprovalMapper proposalApprovalMapper;

    private static final String SEPARATOR = "丨";


    @Transactional
    public void mySave(ProjectProcurementInfoModel model) throws ParseException {
        Integer id = model.getId();
        if (id == null) {
            saveAll(model);
        } else {
            //先删除，再添加
            Map<String, Object> columnMap = new HashMap<String, Object>() {{
                put("info_id", id);
            }};
            draftingInfoMapper.deleteByMap(columnMap);
            meetingInfoMapper.deleteByMap(columnMap);
            reviewMainMapper.deleteByMap(columnMap);
            reviewSubMapper.deleteByMap(columnMap);
            contractMapper.deleteByMap(columnMap);
            attachService.removeByMap(columnMap);
            enrollmentTimeMapper.deleteByMap(columnMap);
            proposalApprovalMapper.deleteByMap(columnMap);
            saveAll(model);
        }
    }

    /**
     * 新增
     *
     * @param model
     */
    private void saveAll(ProjectProcurementInfoModel model) throws ParseException {
        Integer infoId = model.getId();
        //info
        ProjectProcurementInfo info = new ProjectProcurementInfo();
        info.setName(model.getName());
        info.setRegion(model.getRegion());
        info.setProjectManager(model.getProjectManager());
        info.setRequirementManager(model.getRequirementManager());
        info.setProcurementManager(model.getProcurementManager());
        info.setTenderAgent(model.getTenderAgent());
        info.setBudgetAmount(new BigDecimal(model.getBudgetAmount()));
        info.setProcurementMethod(model.getProcurementMethod());
        info.setAgencyServiceFee(new BigDecimal(model.getAgencyServiceFee()));
        info.setRequirementIntegration(model.getRequirementIntegration());
        info.setRequirementApproval(model.getRequirementApproval());
        info.setPerformanceBondRefund(model.getPerformanceBondRefund());
        info.setPerformanceBondPayment(model.getPerformanceBondPayment());
        info.setNegotiationFailureToTender(model.getNegotiationFailureToTender());
        info.setRemarks(model.getRemarks());
        if (infoId != null) {
            info.setId(infoId);
            info.setUpdateTime(new Date());
            updateById(info);
        } else {
            info.setCreateTime(new Date());
            info.setStaffId(StaffInfoUtil.getStaff().getId());
            save(info);
        }


        infoId = info.getId();

        //enrollmentTime
        List<List<String>> enrollmentTimes = model.getEnrollmentTime();
        if (!CollectionUtils.isEmpty(enrollmentTimes)) {
            for (List<String> enrollmentTime : enrollmentTimes) {
                if (enrollmentTime != null) {
                    ProjectProcurementEnrollmentTime enrollmentTimeEntity = new ProjectProcurementEnrollmentTime();
                    enrollmentTimeEntity.setInfoId(infoId);
                    if (enrollmentTime.size() == 1) {
                        enrollmentTimeEntity.setStartTime(enrollmentTime.get(0));
                        enrollmentTimeMapper.insert(enrollmentTimeEntity);
                    }else if (enrollmentTime.size() == 2) {
                        enrollmentTimeEntity.setStartTime(enrollmentTime.get(0));
                        enrollmentTimeEntity.setEndTime(enrollmentTime.get(1));
                        enrollmentTimeMapper.insert(enrollmentTimeEntity);
                    }
                }
            }
        }

        //proposalApproval
        List<String> proposalApprovals = model.getProposalApproval();
        if (!CollectionUtils.isEmpty(proposalApprovals)) {
            for (String proposalApproval : proposalApprovals) {
                ProjectProcurementProposalApproval proposalApprovalEntity = new ProjectProcurementProposalApproval();
                proposalApprovalEntity.setInfoId(infoId);
                proposalApprovalEntity.setContent(proposalApproval);
                proposalApprovalMapper.insert(proposalApprovalEntity);
            }
        }

        List<ProjectProcurementAttach> attaches = new ArrayList<>();
        //drafting 编制过程
        List<ProjectProcurementInfoModel.Drafting> draftings = model.getDraftings();
        if (draftings != null && !draftings.isEmpty()) {
            for (ProjectProcurementInfoModel.Drafting drafting : draftings) {
                ProjectProcurementDraftingInfo draftingInfo = new ProjectProcurementDraftingInfo();
                BeanUtils.copyProperties(drafting, draftingInfo);
                draftingInfo.setInfoId(infoId);
                draftingInfo.setContent(drafting.getContent());
                draftingInfo.setTime(drafting.getTime());
                draftingInfoMapper.insert(draftingInfo);
                List<ProjectProcurementInfoModel.Attach> draftingAttaches = drafting.getAttaches();
                if (!CollectionUtils.isEmpty(draftingAttaches)) {
                    for (ProjectProcurementInfoModel.Attach attach : draftingAttaches) {
                        ProjectProcurementAttach addTemp = new ProjectProcurementAttach();
                        addTemp.setInfoId(infoId);
                        addTemp.setOtherId(draftingInfo.getId());
                        addTemp.setName(attach.getName());
                        addTemp.setType("编制过程");
                        attaches.add(addTemp);
                    }
                }

            }
        }

        //meeting 上会情况
        List<ProjectProcurementInfoModel.Meeting> meetings = model.getMeetings();
        if (!CollectionUtils.isEmpty(meetings)) {
            for (ProjectProcurementInfoModel.Meeting meeting : meetings) {
                ProjectProcurementMeetingInfo meetingInfo = new ProjectProcurementMeetingInfo();
                meetingInfo.setInfoId(infoId);
                meetingInfo.setType(meeting.getType());
                meetingInfo.setTime(meeting.getTime());
                meetingInfoMapper.insert(meetingInfo);
                List<ProjectProcurementInfoModel.Attach> meetingAttaches = meeting.getAttaches();
                if (!CollectionUtils.isEmpty(meetingAttaches)) {
                    for (ProjectProcurementInfoModel.Attach attach : meetingAttaches) {
                        ProjectProcurementAttach addTemp = new ProjectProcurementAttach();
                        addTemp.setInfoId(infoId);
                        addTemp.setOtherId(meetingInfo.getId());
                        addTemp.setName(attach.getName());
                        addTemp.setType("上会情况");
                        attaches.add(addTemp);
                    }
                }


            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        //review 评审信息
        List<ProjectProcurementInfoModel.Review> reviews = model.getReviews();
        if (!CollectionUtils.isEmpty(reviews)) {
            for (ProjectProcurementInfoModel.Review review : reviews) {
                ProjectProcurementReviewMain reviewMain = new ProjectProcurementReviewMain();
                reviewMain.setInfoId(infoId);
                reviewMain.setStartTime(sdf.parse(review.getStartTime()));
                reviewMain.setEndTime(sdf.parse(review.getEndTime()));
                reviewMainMapper.insert(reviewMain);
                List<ProjectProcurementInfoModel.Judge> judges = review.getJudges();
                if (!CollectionUtils.isEmpty(judges)) {
                    for (ProjectProcurementInfoModel.Judge judge : judges) {
                        ProjectProcurementReviewSub reviewSub = new ProjectProcurementReviewSub();
                        reviewSub.setReviewId(reviewMain.getId());
                        reviewSub.setInfoId(infoId);
                        reviewSub.setJudgeType(judge.getJudgeType());
                        reviewSub.setJudge(String.join(SEPARATOR, judge.getJudge()));
                        reviewSubMapper.insert(reviewSub);
                    }
                }

            }
        }

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        //合同签订情况
        List<ProjectProcurementInfoModel.Contract> contracts = model.getContracts();
        if (!CollectionUtils.isEmpty(contracts)) {
            for (ProjectProcurementInfoModel.Contract contract : contracts) {
                ProjectProcurementContractInfo contractInfo = new ProjectProcurementContractInfo();
                contractInfo.setInfoId(infoId);
                contractInfo.setSection(contract.getSection());
                contractInfo.setShare(contract.getShare());
                contractInfo.setSupplierName(contract.getSupplierName());
                contractInfo.setSigningStatus(contract.getSigningStatus());
                contractInfo.setBidNoticeDate(sdf2.parse(contract.getBidNoticeDate()));
                contractInfo.setPerformanceBondPaymentDate(sdf2.parse(contract.getPerformanceBondPaymentDate()));
                contractInfo.setContractEffectiveDate(sdf2.parse(contract.getContractEffectiveDate()));
                contractMapper.insert(contractInfo);
            }
        }

        //公示期异议情况
        ProjectProcurementInfoModel.Other gsqyyqk = model.getGsqyyqk();
        //结果审批情况
        ProjectProcurementInfoModel.Other resultApproval = model.getResultApproval();
        boolean needUpdate = false;
        info = new ProjectProcurementInfo();
        info.setId(infoId);
        if (gsqyyqk != null) {
            needUpdate = true;

            String content = gsqyyqk.getContent() == null ? "" : gsqyyqk.getContent();
            info.setObjectionPublicity(content);
            List<ProjectProcurementInfoModel.Attach> gsqyyqkAttaches = gsqyyqk.getAttaches();
            if (!CollectionUtils.isEmpty(gsqyyqkAttaches)) {
                for (ProjectProcurementInfoModel.Attach attach : gsqyyqkAttaches) {
                    ProjectProcurementAttach addTemp = new ProjectProcurementAttach();
                    addTemp.setInfoId(infoId);
                    addTemp.setName(attach.getName());
                    addTemp.setType("公示期异议情况");
                    addTemp.setPath(attach.getPath());
                    addTemp.setName(attach.getName());
                    attaches.add(addTemp);
                }
            }
        }
        if (resultApproval != null) {
            needUpdate = true;

            String content = resultApproval.getContent() == null ? "" : resultApproval.getContent();
            info.setResultApproval(content);
            List<ProjectProcurementInfoModel.Attach> resultApprovalAttaches = resultApproval.getAttaches();
            if (!CollectionUtils.isEmpty(resultApprovalAttaches)) {
                for (ProjectProcurementInfoModel.Attach attach : resultApprovalAttaches) {
                    ProjectProcurementAttach addTemp = new ProjectProcurementAttach();
                    addTemp.setInfoId(infoId);
                    addTemp.setName(attach.getName());
                    addTemp.setType("结果审批情况");
                    addTemp.setPath(attach.getPath());
                    addTemp.setName(attach.getName());
                    attaches.add(addTemp);
                }
            }
        }
        if (needUpdate) {
            this.updateById(info);
        }
        //保存附件
        if (!CollectionUtils.isEmpty(attaches)) {
            attachService.saveBatch(attaches);
        }
    }

    public ProjectProcurementInfoModel detail(Integer id) {
        ProjectProcurementInfo projectProcurementInfo = this.getBaseMapper().selectById(id);
        ProjectProcurementInfoModel model = new ProjectProcurementInfoModel();
        BeanUtils.copyProperties(projectProcurementInfo, model);
        model.setBudgetAmount(projectProcurementInfo.getBudgetAmount().toString());
        model.setAgencyServiceFee(projectProcurementInfo.getAgencyServiceFee().toString());
        //附件
        List<ProjectProcurementAttach> attaches = attachService.list(new QueryWrapper<ProjectProcurementAttach>().eq("info_id", id));
        Map<String, List<ProjectProcurementAttach>> attachMap = attaches.stream().collect(Collectors.groupingBy(ProjectProcurementAttach::getType));


        List<ProjectProcurementDraftingInfo> draftingInfos = draftingInfoMapper.selectList(new QueryWrapper<ProjectProcurementDraftingInfo>().eq("info_id", id));
        List<ProjectProcurementInfoModel.Drafting> draftings = new ArrayList<>();
        for (ProjectProcurementDraftingInfo draftingInfo : draftingInfos) {
            ProjectProcurementInfoModel.Drafting drafting = new ProjectProcurementInfoModel.Drafting();
            BeanUtils.copyProperties(draftingInfo, drafting);
            draftings.add(drafting);
            drafting.setAttaches(new ArrayList<>());
            Optional.ofNullable(attachMap.get("编制过程")).ifPresent(list -> {
                list.forEach(attach -> {
                    if (attach.getOtherId().equals(draftingInfo.getId())) {
                        ProjectProcurementInfoModel.Attach attachTemp = new ProjectProcurementInfoModel.Attach();
                        BeanUtils.copyProperties(attach, attachTemp);
                        drafting.getAttaches().add(attachTemp);
                    }
                });
            });
        }
        model.setDraftings(draftings);

        List<ProjectProcurementMeetingInfo> meetingInfos = meetingInfoMapper.selectList(new QueryWrapper<ProjectProcurementMeetingInfo>().eq("info_id", id));
        List<ProjectProcurementInfoModel.Meeting> meetings = new ArrayList<>();
        for (ProjectProcurementMeetingInfo meetingInfo : meetingInfos) {
            ProjectProcurementInfoModel.Meeting meeting = new ProjectProcurementInfoModel.Meeting();
            BeanUtils.copyProperties(meetingInfo, meeting);
            meetings.add(meeting);
            meeting.setAttaches(new ArrayList<>());
            Optional.ofNullable(attachMap.get("上会情况")).ifPresent(list -> {
                list.forEach(attach -> {
                    if (attach.getOtherId().equals(meetingInfo.getId())) {
                        ProjectProcurementInfoModel.Attach attachTemp = new ProjectProcurementInfoModel.Attach();
                        BeanUtils.copyProperties(attach, attachTemp);
                        meeting.getAttaches().add(attachTemp);
                    }
                });
            });
        }
        model.setMeetings(meetings);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        List<ProjectProcurementReviewMain> reviewMains = reviewMainMapper.selectList(new QueryWrapper<ProjectProcurementReviewMain>().eq("info_id", id));
        List<ProjectProcurementInfoModel.Review> reviews = new ArrayList<>();
        for (ProjectProcurementReviewMain reviewMain : reviewMains) {
            ProjectProcurementInfoModel.Review review = new ProjectProcurementInfoModel.Review();
            review.setStartTime(sdf.format(reviewMain.getStartTime()));
            review.setEndTime(sdf.format(reviewMain.getEndTime()));
            reviews.add(review);
            List<ProjectProcurementReviewSub> reviewSubs = reviewSubMapper.selectList(new QueryWrapper<ProjectProcurementReviewSub>().eq("review_id", reviewMain.getId()));
            review.setJudges(new ArrayList<>());
            for (ProjectProcurementReviewSub reviewSub : reviewSubs) {
                ProjectProcurementInfoModel.Judge judge = new ProjectProcurementInfoModel.Judge();
                judge.setJudgeType(reviewSub.getJudgeType());
                judge.setJudge(Arrays.asList(reviewSub.getJudge().split(SEPARATOR)));
                review.getJudges().add(judge);
            }
        }
        model.setReviews(reviews);

        List<ProjectProcurementContractInfo> contractInfos = contractMapper.selectList(new QueryWrapper<ProjectProcurementContractInfo>().eq("info_id", id));
        List<ProjectProcurementInfoModel.Contract> contracts = new ArrayList<>();
        for (ProjectProcurementContractInfo contractInfo : contractInfos) {
            ProjectProcurementInfoModel.Contract contract = new ProjectProcurementInfoModel.Contract();
            BeanUtils.copyProperties(contractInfo, contract);
            contract.setBidNoticeDate(new SimpleDateFormat("yyyy-MM-dd").format(contractInfo.getBidNoticeDate()));
            contract.setPerformanceBondPaymentDate(new SimpleDateFormat("yyyy-MM-dd").format(contractInfo.getPerformanceBondPaymentDate()));
            contract.setContractEffectiveDate(new SimpleDateFormat("yyyy-MM-dd").format(contractInfo.getContractEffectiveDate()));
            contracts.add(contract);
        }
        model.setContracts(contracts);


        ProjectProcurementInfoModel.Other gsqyyqk = new ProjectProcurementInfoModel.Other();
        gsqyyqk.setAttaches(new ArrayList<>());
        gsqyyqk.setContent(projectProcurementInfo.getObjectionPublicity());
        List<ProjectProcurementAttach> gsqyyqkAttaches = attachMap.get("公示期异议情况");
        if (!CollectionUtils.isEmpty(gsqyyqkAttaches)) {
            gsqyyqkAttaches.forEach(attach -> {
                ProjectProcurementInfoModel.Attach attachTemp = new ProjectProcurementInfoModel.Attach();
                BeanUtils.copyProperties(attach, attachTemp);
                gsqyyqk.getAttaches().add(attachTemp);
            });
        }
        model.setGsqyyqk(gsqyyqk);

        ProjectProcurementInfoModel.Other resultApproval = new ProjectProcurementInfoModel.Other();
        resultApproval.setAttaches(new ArrayList<>());
        resultApproval.setContent(projectProcurementInfo.getResultApproval());
        List<ProjectProcurementAttach> resultApprovalAttaches = attachMap.get("结果审批情况");
        if (!CollectionUtils.isEmpty(resultApprovalAttaches)) {
            resultApprovalAttaches.forEach(attach -> {
                ProjectProcurementInfoModel.Attach attachTemp = new ProjectProcurementInfoModel.Attach();
                BeanUtils.copyProperties(attach, attachTemp);
                resultApproval.getAttaches().add(attachTemp);
            });
        }
        model.setResultApproval(resultApproval);

        //enrollmentTime
        List<ProjectProcurementEnrollmentTime> enrollmentTimes = enrollmentTimeMapper.selectList(new QueryWrapper<ProjectProcurementEnrollmentTime>().eq("info_id", id));
        List<List<String>> enrollmentTime = new ArrayList<>();
        for (ProjectProcurementEnrollmentTime enrollmentTimeEntity : enrollmentTimes) {
            List<String> temp = new ArrayList<>();
            temp.add(enrollmentTimeEntity.getStartTime());
            temp.add(enrollmentTimeEntity.getEndTime());
            enrollmentTime.add(temp);
        }
        model.setEnrollmentTime(enrollmentTime);

        //proposalApproval
        List<ProjectProcurementProposalApproval> proposalApprovals = proposalApprovalMapper.selectList(new QueryWrapper<ProjectProcurementProposalApproval>().eq("info_id", id));
        List<String> proposalApproval = new ArrayList<>();
        for (ProjectProcurementProposalApproval proposalApprovalEntity : proposalApprovals) {
            proposalApproval.add(proposalApprovalEntity.getContent());
        }
        model.setProposalApproval(proposalApproval);

        return model;
    }
}
