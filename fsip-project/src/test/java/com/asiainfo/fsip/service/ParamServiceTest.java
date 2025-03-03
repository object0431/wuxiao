package com.asiainfo.fsip.service;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.client.TmcRestClient;
import com.asiainfo.fsip.entity.FsipStaticParamEntity;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.TitleInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class ParamServiceTest {

    @Resource
    private ParamService paramService;

    @Resource
    private TmcRestClient restClient;

    @Test
    public void tesQueryParam(){
        FsipStaticParamEntity param = FsipStaticParamEntity.builder().attrType("CXLX").build();

        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        List<FsipStaticParamEntity> paramList =  paramService.queryParam(param, staffInfo);
        log.info(JSONObject.toJSONString(paramList));
    }

    @Test
    public void tesAddParam(){
        FsipStaticParamEntity param = FsipStaticParamEntity.builder().attrType("CXLX").attrCode("XCZ").attrValue("小创造")
                .attrDesc("所属类别：（小发明、小创造、小革新、小设计、小建议）").build();

        StaffInfo staffInfo = StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴").talentLevel("高级")
                .deptId("00430046780").deptName("湖南省分公司数字化部").companyId("004300")
                .hrEmpCode("0611672").companyName("中国联通湖南省分公司").identityNumber("430103198208141517").build();

        paramService.addParam(param, staffInfo);
    }

    @Test
    public void test(){
        TitleInfo titleInfo = TitleInfo.builder().staffInfo(StaffInfo.builder().mainUserId("zhangzx128").empName("张振兴")
                .build()).keys(new String[]{"四級人才年生"}).build();
        log.info(JSONObject.toJSONString(titleInfo));
    }

    @Test
    public void test2(){
        //调用待办接口
        PendingEntity addPendingReq = PendingEntity.builder()
                .pendingCode("11211")
                .pendingTitle("測試")
                .pendingDate(DateUtils.getDateString())
                .pendingUserID("zhangzx128")
                .pendingURL("www.baidu.com")
                .pendingLevel(0).pendingSourceUserID("zhangzx128")
                .pendingSource("張振興")
                .applierId("zhangzx128").applierName("張振興")
                .applierCompanyId("004300").taskId("zhangzx128")
                .taskType(IConstants.TaskType.RCNS).taskStatus("00").build();

        BaseRsp<Void> response = restClient.addPending(new PendingEntity[]{addPendingReq});
    }
}
