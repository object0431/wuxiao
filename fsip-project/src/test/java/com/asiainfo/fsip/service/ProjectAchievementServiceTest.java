package com.asiainfo.fsip.service;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.model.*;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class ProjectAchievementServiceTest {

    @Resource
    private ProjectAchievementService projectAchievementService;

    @Test
    public void testCity2Prov(){
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        List<String> achievementIdList = new ArrayList<>();
        achievementIdList.add("HN231226162200852229");
        achievementIdList.add("HN231226162200852229");
        achievementIdList.add("HN231226162514619951");

        List<ApprovalApplyReq.ApprovalNode> approvalNodeList = new ArrayList<>();
        approvalNodeList.add(ApprovalApplyReq.ApprovalNode.builder().nodeCode("GHZXSP").nodeName("管理层审批").nodeState("04")
                .approveId("zhangzx128").approveName("张振兴").build());

        ApprovalApplyReq approvalReq = ApprovalApplyReq.builder().opinion("请领导审批").approvalNodeList(approvalNodeList).build();

        City2ProvAchievementApplyReq req = City2ProvAchievementApplyReq.builder().achievementIdList(achievementIdList)
                .approvalReq(approvalReq).build();
        projectAchievementService.city2ProvApply(req, staffInfo);
    }

    @Test
    public void testGetProject(){
        ProjectAchievementModel achievementModel = projectAchievementService.getProject("HN240312150823782929");
        log.info(JSONObject.toJSONString(achievementModel));
    }

    @Test
    public void testSearchProject(){
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        PageReq<ProjectAchievementSearchModel> req = new PageReq<>();
        req.setPageSize(10);
        req.setPageNum(1);
        req.setReqParam(new ProjectAchievementSearchModel());

        PageInfo<ProjectAchievementModel> pageInfo = projectAchievementService.searchProject(req, staffInfo);
        log.info(JSONObject.toJSONString(pageInfo));
    }

    @Test
    public void testQueryReviewList() {
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("liujun225").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();
        PageReq<AchievementPushQryReq> req = new PageReq();
        req.setPageNum(1);
        req.setPageSize(5);
        req.setReqParam(AchievementPushQryReq.builder().achievementType("CITY")
                .queryType("0").build());

        PageInfo<ProjectAchievementModel> rsp = projectAchievementService.queryReviewList(req, staffInfo);
        log.info(JSONObject.toJSONString(rsp));
    }

    @Test
    public void testQueryReviewJudgeList() {
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("caijun11").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        List<StaffInfo> dataList = projectAchievementService.queryReviewJudgeList("1", staffInfo);
        log.info(JSONObject.toJSONString(dataList));
    }

    @Test
    public void testUrgingReview() {
        List<String> staffIdList = new ArrayList<>();
        staffIdList.add("xieyg");
        staffIdList.add("liujun5");
        ReviewUrgingReq req = ReviewUrgingReq.builder().staffIdList(staffIdList).content("五小创新平台评分催办测试").build();

        projectAchievementService.urgingReview(req);
    }
}
