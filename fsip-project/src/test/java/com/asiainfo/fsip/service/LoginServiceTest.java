package com.asiainfo.fsip.service;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.sso.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class LoginServiceTest {

    @Resource
    private LoginService loginService;

    @Test
    public void test(){
        StaffInfo staffInfo = loginService.getLoginUser("zhangzx128");
        log.info(JSONObject.toJSONString(staffInfo));
    }
}
