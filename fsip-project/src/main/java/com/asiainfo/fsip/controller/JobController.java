package com.asiainfo.fsip.controller;

import com.asiainfo.fsip.service.JobService;
import com.asiainfo.fsip.utils.DateUtils;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("job")
@Slf4j
@Api("定时任务接口")
public class JobController {

    @Resource
    private JobService jobService;

    @ApiOperation("更新登陆人员组织信息")
    @GetMapping(value = "/modifyLoginDeptId")
    @RspResult
    public void modifyLoginDeptId() {
        jobService.modifyLoginDeptId(DateUtils.getLastMonthDate());
    }

    @ApiOperation("刷新登录统计数据")
    @GetMapping("/refreshLoginData")
    @RspResult
    public BaseRsp<Void> refreshLoginData() {
        try {
            // 按每日刷新一次，刷新当前月份
            String startMonth = DateUtils.formatDate(new Date(), "yyyyMM");
            log.info("刷新【{}】月份的登录统计数据",startMonth);
            jobService.generateLoginStatData(startMonth);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.error("Could not refresh login data", e);
            return RspHelp.fail(e.getMessage());
        }
    }

    @ApiOperation("生成上月统计数据")
    @GetMapping("/generateLastMonthData")
    @RspResult
    public BaseRsp<Void> generateLastMonthData(@RequestParam("month") int month) {
        try {
            String startMonth = DateUtils.getLastMonth(month);
            jobService.generateLoginStatData(startMonth);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.error("Could not refresh last month data", e);
            return RspHelp.fail(e.getMessage());
        }
    }
}
