package com.asiainfo.fsip.controller;

import com.asiainfo.fsip.entity.FsipConsultOrderEntity;
import com.asiainfo.fsip.model.ConsultationInitiateReq;
import com.asiainfo.fsip.model.ConsultationReplyReq;
import com.asiainfo.fsip.model.FsipConsultOrderRsp;
import com.asiainfo.fsip.service.ConsultationService;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("consultation")
@Slf4j
@Api("工单咨询服务")
public class ConsultationController {

    @Resource
    private ConsultationService consultService;

    @ApiOperation("咨询发起")
    @PostMapping("/initiate")
    @RspResult
    public void initiate(@RequestBody ConsultationInitiateReq req) {
        try {
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            consultService.initiate(req, staffInfo);
        } catch (Exception e) {
            log.info("Could not execute initiate", e);
            throw e;
        }
    }

    @ApiOperation("咨询回复")
    @PostMapping("/reply")
    @RspResult
    public void reply(@RequestBody ConsultationReplyReq req) {
        try {
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            consultService.reply(req, staffInfo);
        } catch (Exception e) {
            log.info("Could not execute reply", e);
            throw e;
        }
    }

    @ApiOperation("咨询评分")
    @GetMapping("/grade")
    @RspResult
    public void grade(@RequestParam String orderId, @RequestParam String score) {
        try {
            consultService.grade(orderId, score);
        } catch (Exception e) {
            log.info("Could not execute grade", e);
            throw e;
        }
    }

    @ApiOperation("咨询列表查询")
    @GetMapping("/queryList")
    @RspResult
    public List<FsipConsultOrderEntity> queryList(@RequestParam String targetId) {
        try {
            return consultService.queryByTargetId(targetId);
        } catch (Exception e) {
            log.info("Could not execute queryList", e);
            throw e;
        }
    }

    @ApiOperation("咨询工单明细查询")
    @GetMapping("/queryDetail")
    @RspResult
    public FsipConsultOrderEntity queryDetail(@RequestParam String orderId) {
        try {
            return consultService.queryByOrderId(orderId);
        } catch (Exception e) {
            log.info("Could not execute queryDetail", e);
            throw e;
        }
    }

    @ApiOperation("咨询工单回复页面查询历史工单")
    @GetMapping("/queryOrderList")
    @RspResult
    public FsipConsultOrderRsp queryOrderList(@RequestParam String orderId) {
        try {
            return consultService.queryListByOrderId(orderId);
        } catch (Exception e) {
            log.info("Could not execute queryOrderList", e);
            throw e;
        }
    }
}
