package com.asiainfo.fsip.service;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.model.IndexAchievementQueryRsp;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class IndexServiceTest {

    @Resource
    private IndexService indexService;

    @Test
    public void test(){
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        IndexAchievementQueryRsp rsp = indexService.achievementQuery(null, staffInfo);
        log.info(JSONObject.toJSONString(rsp));
    }
}
