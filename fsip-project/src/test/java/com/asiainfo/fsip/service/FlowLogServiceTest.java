package com.asiainfo.fsip.service;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.model.FlowLogModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class FlowLogServiceTest {

    @Resource
    private FlowLogService flowLogService;

    @Test
    public void testQueryFlowLogById() throws Exception {
        List<FlowLogModel> logModelList = flowLogService.queryFlowLogById(IFsipConstants.TaskType.CGSQ, "HN230815142855123746");
        log.info(JSONObject.toJSONString(logModelList));
    }
}
