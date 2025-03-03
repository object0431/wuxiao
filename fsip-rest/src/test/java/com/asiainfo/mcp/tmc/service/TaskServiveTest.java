package com.asiainfo.mcp.tmc.service;

import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingUpEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class TaskServiveTest {

    @Resource
    private TaskServive taskServive;

    @Test
    public void testAddPending(){
        PendingEntity pendingEntity = PendingEntity.builder().pendingCode("HN202209231004")
                .pendingDate("20220920101500").pendingLevel(0).pendingSourceUserID("zhangzx128").pendingSource("测试")
                .pendingUserID("zhangzx128").pendingTitle("湖南数字化部2022年培训计划20220923").pendingURL("http://127.0.0.1")
                .taskId("GRPXSQ202306290008").applierId("zhangzx128").applierName("张振兴")
                .applierCompanyId("").build();

        PendingEntity [] pendingEntities = new PendingEntity[]{pendingEntity};

        taskServive.addPendingTask(pendingEntities);
    }

    @Test
    public void testUpdatePending(){
        PendingUpEntity pendingUpEntity = PendingUpEntity.builder().pendingCode("HN202209201904").build();

        taskServive.updatePendingStatus(new PendingUpEntity[]{pendingUpEntity});
    }
}
