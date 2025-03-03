package com.asiainfo.fsip.service;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.model.ProjectStatisticsProTypeModel;
import com.asiainfo.fsip.model.TimeScopeReq;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;


@SpringBootTest
@Slf4j
public class ProjectClassificationServiceTest {

    @Resource
    private ProjectClassificationService projectClassificationService;

    @Test
    public void testGetProjectStatisticsInfoByType() throws Exception {
        //"20231201", "20240101", IFsipConstants.StaticParamType.XMLX
        TimeScopeReq timeScopeReq = TimeScopeReq.builder().startDate("20231201").endDate("20240101").type(IFsipConstants.StaticParamType.XMLX).build();

        List<ProjectStatisticsProTypeModel> dataList =  projectClassificationService.getProjectStatisticsInfoByType(timeScopeReq);

        log.info(JSONObject.toJSONString(dataList));
    }

}
