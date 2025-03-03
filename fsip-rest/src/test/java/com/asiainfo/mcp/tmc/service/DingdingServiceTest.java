package com.asiainfo.mcp.tmc.service;

import com.asiainfo.mcp.tmc.common.util.TokenUtils;
import com.asiainfo.mcp.tmc.dingding.service.DingdingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class DingdingServiceTest {

    @Resource
    private DingdingService dingdingService;

    @Test
    public void test() throws Exception {
        String accessToken = dingdingService.getAccessToken();

        log.info("accessToken = " + accessToken);
    }

    @Test
    public void testToken(){
        String token =  TokenUtils.encode("hn_xxl_job");
        log.info(token);
    }


}
