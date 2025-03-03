package com.asiainfo.fsip.controller;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.entity.FsipActivityInfoEntity;
import com.asiainfo.fsip.model.FsipActivityInfoReq;
import com.asiainfo.fsip.service.ActivityInfoService;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("activityInfo")
@Slf4j
@Api("活动相关接口")
public class ActivityInfoController {

    @Resource
    private ActivityInfoService activityInfoService;

    @PostMapping("/saveNews")
    @ApiOperation("新增新闻")
    public BaseRsp<Void> saveNews(@RequestBody FsipActivityInfoEntity activity) {
        log.info("activity::"+activity);
        try {
            activityInfoService.saveInfo(activity);
        }catch (Exception e) {
            return RspHelp.fail(RspHelp.ERROR_CODE,e.getMessage());
        }
        return RspHelp.success(null);
    }


    @PostMapping("/queryNews")
    @ApiOperation("新闻查询")
    public BaseRsp<PageInfo<FsipActivityInfoEntity>> queryNews(@RequestBody PageReq<FsipActivityInfoReq> pageReq) {
        try {
            log.info("search req str::" + JSONObject.toJSONString(pageReq));
            PageInfo<FsipActivityInfoEntity> rsp =activityInfoService.queryInfo(pageReq);
            log.info("search rsp str::" + JSONObject.toJSONString(rsp));
            return RspHelp.success(rsp);
        } catch (Exception e) {
            log.info("Could not execute search", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }


    @PostMapping("/queryDetail")
    @ApiOperation("新闻活动详情")
    public BaseRsp<FsipActivityInfoEntity> queryDetail(String id) {
        try {
            log.info("search req str::" + JSONObject.toJSONString(id));
            FsipActivityInfoEntity rsp =activityInfoService.queryDetail(id);
            log.info("search rsp str::" + JSONObject.toJSONString(rsp));
            return RspHelp.success(rsp);
        } catch (Exception e) {
            log.info("Could not execute search", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }


    @PostMapping("/deleteNews")
    @ApiOperation("删除新闻")
    public BaseRsp<Void> deleteNews(String id) {
        log.info(id);
        try {
            activityInfoService.deleteInfo(id);
        }catch (Exception e) {
            return RspHelp.fail(RspHelp.ERROR_CODE,e.getMessage());
        }
        return RspHelp.success(null);
    }


    @PostMapping("/saveActivity")
    @ApiOperation("新增活动")
    public BaseRsp<Void> saveActivity(@RequestBody FsipActivityInfoEntity activity) {
        log.info("activity::"+activity);
        try {
            activityInfoService.saveInfo(activity);
        }catch (Exception e) {
            return RspHelp.fail(RspHelp.ERROR_CODE,e.getMessage());
        }
        return RspHelp.success(null);
    }


    @PostMapping("/queryActivity")
    @ApiOperation("活动查询")
    public BaseRsp<PageInfo<FsipActivityInfoEntity>> queryActivity(@RequestBody PageReq<FsipActivityInfoReq> pageReq) {
        try {
            log.info("search req str::" + JSONObject.toJSONString(pageReq));
            PageInfo<FsipActivityInfoEntity> rsp =activityInfoService.queryInfo(pageReq);
            log.info("search rsp str::" + JSONObject.toJSONString(rsp));
            return RspHelp.success(rsp);
        } catch (Exception e) {
            log.info("Could not execute search", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }


    @PostMapping("/deleteActivity")
    @ApiOperation("删除活动")
    public BaseRsp<Void> deleteActivity(String id) {
        log.info(id);
        try {
            activityInfoService.deleteInfo(id);
        }catch (Exception e) {
            return RspHelp.fail(RspHelp.ERROR_CODE,e.getMessage());
        }
        return RspHelp.success(null);
    }

}
