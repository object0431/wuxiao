package com.asiainfo.fsip.service;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.constants.IFsipConstants;
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
public class StaffInfoServiceTest {

    @Resource
    private StaffInfoService staffInfoService;
    @Resource
    private ApprovalService approvalService;
    @Resource
    private ProjectAchievementService achievementService;

    @Test
    public void testQueryStaffInfoList() {
        QryEmployeeReq queryReq = QryEmployeeReq.builder().orgCode("004305").staffName("眭映庄").build();
        PageReq<QryEmployeeReq> req = new PageReq<>();
        req.setPageSize(1);
        req.setPageNum(10);
        req.setReqParam(queryReq);

        PageInfo<OrganizerStrucRsp.EmployeeChildrenBean> pageInfo = staffInfoService.queryStaffInfoList(req);
        log.info(JSONObject.toJSONString(pageInfo));
    }

    @Test
    public void testGetApprovalList() throws Exception {
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        List<ApprovalNodeModel> staffList = staffInfoService.getApprovalList(IFsipConstants.TaskType.CGSQ, null, staffInfo);
        log.info(JSONObject.toJSONString(staffList));
    }

    @Test
    public void testApprovalTask() throws Exception {
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("niefx").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004314")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        String json = "{\"remark\":\"同意\",\"retType\":\"TG\",\"targetId\":\"SJCG202403180001\",\"targetType\":\"SJCGZSJ\",\"ext\":{\"projectId\":\"HN240116173657160912\"}}";

        ApprovalRetModel approvalRetModel = JSONObject.parseObject(json, ApprovalRetModel.class);
        approvalService.approvalTask(approvalRetModel, staffInfo);

    }

    @Test
    public void handleProject() throws Exception {
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();
        ProjectAchievementModel model = new ProjectAchievementModel();
        model.setProjectName("测试1");
        model.setStartDate("2023-08-09");
        model.setEndDate("2023-09-28");
        model.setBenefit("12");
        model.setInnovationType("XSJ");
        model.setProjectType("XTL");
        List<ProjectAchievementModel.AchievementItem> items = new ArrayList<>();
        items.add(ProjectAchievementModel.AchievementItem.builder().itemCode("XJX").itemName("先进性").itemValue("测试").sort(1).build());
        items.add(ProjectAchievementModel.AchievementItem.builder().itemCode("SYX").itemName("实用性").itemValue("测试").sort(2).build());
        items.add(ProjectAchievementModel.AchievementItem.builder().itemCode("KJHL").itemName("科技含量").itemValue("测试").sort(3).build());
        items.add(ProjectAchievementModel.AchievementItem.builder().itemCode("TGJZ").itemName("推广价值").itemValue("测试").sort(4).build());
        items.add(ProjectAchievementModel.AchievementItem.builder().itemCode("YJXY").itemName("预计效益").itemValue("测试").sort(5).build());
        items.add(ProjectAchievementModel.AchievementItem.builder().itemCode("QT").itemName("其他").itemValue("测试").sort(5).build());
        model.setItemList(items);
        ApprovalApplyReq applyReq = new ApprovalApplyReq();
        applyReq.setOpinion("我是办理意见");
        List<ApprovalApplyReq.ApprovalNode> approvalNodeList = new ArrayList<>();
        approvalNodeList.add(ApprovalApplyReq.ApprovalNode.builder().apprOfficer("BMJL").approveId("yuanzhao").approveName("赵媛").nodeCode("BMLDSP").nodeName("部门领导审批").build());
        applyReq.setApprovalNodeList(approvalNodeList);
        ProjectAchievementAddReq req = new ProjectAchievementAddReq();
        req.setApplyReq(applyReq);
        req.setProjectModel(model);
        achievementService.handleProject(req, staffInfo);

    }

    @Test
    public void zgSearch() throws Exception {
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();
        PageReq<Map<String, String>> req = new PageReq();
        req.setPageNum(1);
        req.setPageSize(5);
        Map<String, String> map = new HashMap<>();
        map.put("achievementType", "PROV");
        map.put("pendingCode", "CGSQ202402270001");
        req.setReqParam(map);

        PageInfo<ProjectAchievementModel> rsp = achievementService.zgSearchProject(req, staffInfo);
        log.info(JSONObject.toJSONString(rsp));
    }

    @Test
    public void zgPush() throws Exception {
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        String json = "{\n" +
                "  \"list\": [\n" +
                "    {\n" +
                "      \"staffId\": \"zhanglw57\",\n" +
                "      \"staffName\": \"张量伟\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"achievementType\": \"PROV\",\n" +
                "  \"achievementIdList\": [\n" +
                "    \"HN240229102527132106\"\n" +
                "  ]\n" +
                "}";
        ZgPushReq req = JSONObject.parseObject(json, ZgPushReq.class);

        achievementService.zgPush(req, staffInfo);

    }

    @Test
    public void achievementReview() throws Exception {
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("liujun225").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        String json = "{\"pendingCode\":\"CGSQ202402290005\",\"status\":\"00\",\"scoreNodeList\":[{\"itemScoreList\":[{\"itemCode\":\"CXX\",\"itemName\":\"创新性\",\"score\":\"86\"},{\"itemCode\":\"XJX\",\"itemName\":\"先进性\",\"score\":\"86\"},{\"itemCode\":\"SYX\",\"itemName\":\"实用性\",\"score\":\"86\"},{\"itemCode\":\"KJHL\",\"itemName\":\"科技含量\",\"score\":\"86\"},{\"itemCode\":\"TGJZ\",\"itemName\":\"推广价值\",\"score\":\"86\"},{\"itemCode\":\"CZJJXY\",\"itemName\":\"创造的经济效益/社会效益\",\"score\":\"86\"},{\"itemCode\":\"HJQK\",\"itemName\":\"获奖情况\",\"score\":\"86\"}],\"projectId\":\"HN240229102527132106\"}],\"appendixList\":[],\"achievementType\":\"CITY\"}";

        AchievementReviewReq req = JSONObject.parseObject(json, AchievementReviewReq.class);

        achievementService.achievementReview(req, staffInfo);

    }
}
