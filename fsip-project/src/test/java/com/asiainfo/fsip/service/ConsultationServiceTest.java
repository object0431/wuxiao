package com.asiainfo.fsip.service;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.entity.FsipConsultOrderEntity;
import com.asiainfo.fsip.model.ConsultationInitiateReq;
import com.asiainfo.fsip.model.ConsultationReplyReq;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class ConsultationServiceTest {

    @Resource
    private ConsultationService consultationService;

    @Test
    public void testInitiate(){
        List<ConsultationInitiateReq.Expert> expertList = new ArrayList<>();
        expertList.add(ConsultationInitiateReq.Expert.builder().staffId("qingxiao").staffName("肖晴").build());
        expertList.add(ConsultationInitiateReq.Expert.builder().staffId("pengjy18").staffName("彭佳垠").build());

        ConsultationInitiateReq req = ConsultationInitiateReq.builder().targetType(IFsipConstants.TaskType.LXSQ)
                .targetId("HN230811104107162111").questions("十万个为什么?").expertList(expertList).build();

        log.info(JSONObject.toJSONString(req));

        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        consultationService.initiate(req, staffInfo);
    }

    @Test
    public void testReply(){
        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        ConsultationReplyReq req = ConsultationReplyReq.builder().orderId("ZX202308110001").replyContent("测试测试1111").build();

        consultationService.reply(req, staffInfo);
    }

    @Test
    public void testGrade(){
         consultationService.grade("ZX202308110001", "4.5");
    }

    @Test
    public void testQueryByTargetId(){
        List<FsipConsultOrderEntity> orderList = consultationService.queryByTargetId("HN230811104107162111");
        log.info(JSONObject.toJSONString(orderList));
    }

    @Test
    public void testQueryByOrderId(){
        FsipConsultOrderEntity orderEntity = consultationService.queryByOrderId("ZX202308110004");
        log.info(JSONObject.toJSONString(orderEntity));
    }
}
