package com.asiainfo.fsip.service;

import com.asiainfo.fsip.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class CacheServiceTest {

    @Resource
    private CacheService cacheService;
    @Test
    public void testSelectByProp(){
        String value = cacheService.getParamValue("STATE", "ZC");
        log.info("value = " + value);
    }

}
