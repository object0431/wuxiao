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
import java.util.List;

@SpringBootTest
@Slf4j
public class FsipProjectAchievementServiceTest {

    @Resource
    private FsipProjectAchievementService fsipProjectAchievementService;

    @Test
    public void test(){
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430589892").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        PageReq<ProjectAchievementPushSelReq> req = new PageReq<>();
        req.setPageSize(1);
        req.setPageNum(10);

        PageInfo<ProjectAchievementPushSelResp> pageInfo = fsipProjectAchievementService.selRatingList(req, staffInfo);
        log.info(JSONObject.toJSONString(pageInfo));
    }

    @Test
    public void testSelPendingRatingList() throws Exception{
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        PageReq<ProjectAchievementPushSelReq> req = new PageReq<>();
        req.setPageSize(1);
        req.setPageNum(10);
        req.setReqParam(ProjectAchievementPushSelReq.builder().achievementType("CITY").build());

        PageInfo<ProjectAchievementPushSelResp> pageInfo = fsipProjectAchievementService.selPendingRatingList(req, staffInfo);
        log.info(JSONObject.toJSONString(pageInfo));
    }

    @Test
    public void testSelCity2ProvAuditAchievement() throws Exception{
        City2ProvAuditAchievementReq req = City2ProvAuditAchievementReq.builder().pendingCode("SJCG202310110006").build();

        List<ProjectAchievementPushSelResp> pageInfo = fsipProjectAchievementService.selCity2ProvAuditAchievement(req);
        log.info(JSONObject.toJSONString(pageInfo));
    }

    @Test
    public void testQueryAchievementStatistics() throws Exception{
        PageReq<ProjectAchievementStatisticsReq> req = new PageReq<>();
        req.setPageSize(10);
        req.setPageNum(1);
        req.setReqParam(ProjectAchievementStatisticsReq.builder().build());

        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        PageInfo<ProjectAchievementStatisticsResp> pageInfo = fsipProjectAchievementService.queryAchievementStatistics(req, staffInfo);
        log.info(JSONObject.toJSONString(pageInfo));
    }

    @Test
    public void testRating() throws Exception{
        String json = "{\"reqParam\":[{\"achievementId\":\"HN231011172413550516\",\"level\":\"PROV_2\"}],\"attachmentList\":[],\"type\":\"PROV\"}";
        ProjectAchievementRatingReq req = JSONObject.parseObject(json, ProjectAchievementRatingReq.class);

        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        ProjectAchievementRatingResp pageInfo = fsipProjectAchievementService.rating(req, staffInfo);
        log.info(JSONObject.toJSONString(pageInfo));
    }
}
