package com.asiainfo.fsip.service;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.model.ProjectTransferReq;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class ProjectInitiationServiceTest {

    @Resource
    private ProjectInitiationService projectInitiationService;

    @Test
    public void testTransferProject(){
        String json = "{\n" +
                "  \"projectId\": \"HN231008095825398744\",\n" +
                "  \"transferType\": \"ZJRC\",\n" +
                "  \"transferStaffId\": \"zhangzx128\",\n" +
                "  \"transferStaffName\": \"张振兴\",\n" +
                "  \"remark\": \"\"\n" +
                "}";

        ProjectTransferReq transferReq = JSONObject.parseObject(json, ProjectTransferReq.class);

        StaffInfo staffInfo = StaffInfo.builder().mainUserId("yuanzhao").empName("赵媛").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        projectInitiationService.transferProject(transferReq, staffInfo);
    }
}
