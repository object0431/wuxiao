package com.asiainfo.fsip.mapper;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.mapper.fsip.FispStaff2RoleMapper;
import com.asiainfo.fsip.model.Staff2RoleModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class FsipStaff2RoleMapperTest {

    @Resource
    private FispStaff2RoleMapper fispStaff2RoleMapper;

    @Test
    public void testSelectByProp(){
        List<Staff2RoleModel> roleList = fispStaff2RoleMapper.selectByStaffId("qingxiao");
        log.info(JSONObject.toJSONString(roleList));
    }
}
