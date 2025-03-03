package com.asiainfo.fsip.mapper;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.mapper.tmc.TmcEmployeeMapper;
import com.asiainfo.mcp.tmc.entity.MiniUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class TmcEmployeeMapperTest {

    @Resource
    private TmcEmployeeMapper tmcEmployeeMapper;

    @Test
    public void testSelectByProp(){
        List<MiniUserEntity> userList = tmcEmployeeMapper.selectByProp("张振兴", null, null);
        log.info(JSONObject.toJSONString(userList));
    }
}
