package com.asiainfo.fsip.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class JobServiveTest {

    @Resource
    private JobService jobService;

    @Test
    public void testStatisticsLoginData() throws Exception{
        jobService.statisticsLoginData();
    }

    @Test
    public void testGenerateLoginStatData() throws Exception{
        jobService.generateLoginStatData("202312");
    }

    @Test
    public void testGenerateLoginStatData2() throws Exception{
        jobService.generateLoginStatData("20231201", "20240101");
    }

    @Test
    public void testModifyLoginDeptId() throws Exception{
        jobService.modifyLoginDeptId("20231201");
    }
}
