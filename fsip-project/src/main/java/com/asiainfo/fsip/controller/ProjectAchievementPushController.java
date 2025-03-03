package com.asiainfo.fsip.controller;

import cn.hutool.core.bean.BeanUtil;
import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.FsipProjectAchievementService;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.util.ExcelUtil;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("projectAchievement")
@Slf4j
@Api("项目成果推送")
public class ProjectAchievementPushController {

    @Resource
    private FsipProjectAchievementService fsipProjectAchievementService;

    @PostMapping("/selPendingRatingList")
    @RspResult
    @ApiOperation(value = "级别待评定列表查询", notes = "author: liujun5 2023/08/07")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "级别评定查询条件", value = "标准查询字段", paramType = "query", dataType = "req")
    })
    public PageInfo<ProjectAchievementPushSelResp> selPendingRatingList(@RequestBody PageReq<ProjectAchievementPushSelReq> req) throws Exception{
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        return fsipProjectAchievementService.selPendingRatingList(req, staffInfo);
    }

    @PostMapping("/selPendingRatingListExport")
    @RspResult
    @ApiOperation(value = "市级成果评级列表导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "市级成果评级列表导出", value = "市级成果评级列表导出", paramType = "query", dataType = "req")
    })
    public void selPendingRatingListExport(@RequestBody PageReq<ProjectAchievementPushSelReq> req, HttpServletResponse response) throws Exception {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        req.setPageSize(Integer.MAX_VALUE);
        PageInfo<ProjectAchievementPushSelResp> p = fsipProjectAchievementService.selPendingRatingList(req, staffInfo);

        List<ProjectAchievementLevelExport> dataList = new ArrayList<>();
        if(null!=p && null!=p.getList() && p.getList().size()>0) {
            p.getList().forEach(entity -> {
                ProjectAchievementLevelExport model = new ProjectAchievementLevelExport();
                BeanUtil.copyProperties(entity, model);
                dataList.add(model);
            });
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("市级成果评级列表导出.xlsx", "UTF-8").replaceAll("\\+", "%20"));
        ExcelUtil.exportData(dataList, "市级成果评级列表", ProjectAchievementLevelExport.class,response.getOutputStream());
    }

    @PostMapping("/selPendingRatingDetail")
    @RspResult
    @ApiOperation(value = "级别评定详细查询", notes = "author: liujun5 2023/08/08")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "级别评定查询条件", value = "标准查询字段", paramType = "query", dataType = "req")
    })
    public ProjectAchievementPushDetailResp selPendingRatingDetail(@RequestBody ProjectAchievementPushSelReq req) {
        String achievementId = req.getAchievementId();
        return fsipProjectAchievementService.selPendingRatingDetail(achievementId,req.getType());
    }

    @PostMapping("/rating")
    @RspResult
    @ApiOperation(value = "奖项评定", notes = "author: liujun5 2023/08/08")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "级别评定", value = "标准查询字段", paramType = "update", dataType = "req")
    })
    public ProjectAchievementRatingResp rating(@RequestBody ProjectAchievementRatingReq req) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        return fsipProjectAchievementService.rating(req, staffInfo);
    }

    @PostMapping("/nationalProjectAchievementPush")
    @RspResult
    @ApiOperation(value = "国家级成果推送新增", notes = "author: liujun5 2023/08/11")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "国家级成果推送新增条件", value = "请求bean", paramType = "save", dataType = "req")
    })
    public NationalProjectAchievementResp nationalProjectAchievementPush(@RequestBody NationalProjectAchievementReq req) {
        return fsipProjectAchievementService.nationalProjectAchievementPush(req);
    }

    @PostMapping("/selNationalProjectAchievementList")
    @RspResult
    @ApiOperation(value = "国家级成果list查询", notes = "author: liujun5 2023/08/11")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "国家级成果list查询条件", value = "请求bean", paramType = "query", dataType = "req")
    })
    public PageInfo<NationalProjectAchievementSelResp> selNationalProjectAchievementList(@RequestBody PageReq<NationalProjectAchievementSelReq> req) {
        return fsipProjectAchievementService.selNationalProjectAchievementList(req);
    }

    @PostMapping("/selNationalProjectAchievementListExport")
    @RspResult
    @ApiOperation(value = "国家级成果列表导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "国家级成果列表导出", value = "市级成果评级列表导出", paramType = "query", dataType = "req")
    })
    public void selNationalProjectAchievementListExport(@RequestBody PageReq<NationalProjectAchievementSelReq> req, HttpServletResponse response) throws Exception {
        req.setPageSize(Integer.MAX_VALUE);
        PageInfo<NationalProjectAchievementSelResp> p = fsipProjectAchievementService.selNationalProjectAchievementList(req);

        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("国家级成果导出.xlsx", "UTF-8").replaceAll("\\+", "%20"));
        ExcelUtil.exportData(p.getList(), "国家级成果", NationalProjectAchievementSelResp.class,response.getOutputStream());
    }

    @PostMapping("/selNationalProjectAchievementDetail")
    @RspResult
    @ApiOperation(value = "国家级成果查询", notes = "author: liujun5 2023/08/11")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "国家级成果查询条件", value = "请求bean", paramType = "query", dataType = "req")
    })
    public NationalProjectAchievementSelResp selNationalProjectAchievementDetail(@RequestBody NationalProjectAchievementSelReq req) {
        return fsipProjectAchievementService.selNationalProjectAchievementDetail(req.getAchievementId());
    }

    @PostMapping("/selProjectAchievementStatistics")
    @RspResult
    @ApiOperation(value = "项目成果统计查询", notes = "author: liujun5 2023/08/10")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "项目成果统计查询", value = "标准查询字段", paramType = "query", dataType = "req")
    })
    public PageInfo<ProjectAchievementStatisticsResp> selProjectAchievementStatistics(@RequestBody PageReq<ProjectAchievementStatisticsReq> req) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        return fsipProjectAchievementService.queryAchievementStatistics(req, staffInfo);
    }
    @PostMapping("/selProjectAchievementStatisticsExport")
    @RspResult
    @ApiOperation(value = "项目成果统计查询导出", notes = "author: liujun5 2023/08/10")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "项目成果统计查询", value = "标准查询字段", paramType = "query", dataType = "req")
    })
    public void selProjectAchievementStatisticsExport(@RequestBody PageReq<ProjectAchievementStatisticsReq> req, HttpServletResponse response) throws IOException {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        req.setPageSize(Integer.MAX_VALUE);
        PageInfo<ProjectAchievementStatisticsResp> p = fsipProjectAchievementService.queryAchievementStatistics(req, staffInfo);
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("项目成果统计导出.xlsx", "UTF-8").replaceAll("\\+", "%20"));
        ExcelUtil.exportData(p.getList(), "项目列表", ProjectAchievementStatisticsResp.class,response.getOutputStream());

    }

    @PostMapping("/selRatingList")
    @RspResult
    @ApiOperation(value = "级别评定列表查询", notes = "author: liujun5 2023/08/09")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "级别评定查询条件", value = "标准查询字段", paramType = "query", dataType = "req")
    })
    public PageInfo<ProjectAchievementPushSelResp> selRatingList(@RequestBody PageReq<ProjectAchievementPushSelReq> req) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        ProjectAchievementPushSelReq reqParam = req.getReqParam();
        if (reqParam == null) {
            reqParam = ProjectAchievementPushSelReq.builder().build();
        }
        reqParam.setCityToProvFlag("0");
        reqParam.setCityToProvApplyFlag("1");
        return fsipProjectAchievementService.selRatingList(req, staffInfo);
    }

    @PostMapping("/selRatingListExport")
    @RspResult
    @ApiOperation(value = "市级成果转省级成果查询结果导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "市级成果转省级成果查询结果导出", value = "市级成果转省级成果查询结果导出", paramType = "query", dataType = "req")
    })
    public void selRatingListExport(@RequestBody PageReq<ProjectAchievementPushSelReq> req, HttpServletResponse response) throws IOException {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        req.setPageSize(Integer.MAX_VALUE);
        PageInfo<ProjectAchievementPushSelResp> p = fsipProjectAchievementService.selRatingList(req, staffInfo);
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("市级成果申请省级成果导出.xlsx", "UTF-8").replaceAll("\\+", "%20"));
        ExcelUtil.exportData(p.getList(), "成果", ProjectAchievementPushSelResp.class,response.getOutputStream());

    }

    @PostMapping("/selCity2ProvAuditAchievement")
    @RspResult
    @ApiOperation(value = "获取地市转省份列表", notes = "author: liujun5 2023/08/08")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "获取地市转省份列表", value = "标准查询字段", paramType = "update", dataType = "req")
    })
    public List<ProjectAchievementPushSelResp> selCity2ProvAuditAchievement(@RequestBody City2ProvAuditAchievementReq req) {
        return fsipProjectAchievementService.selCity2ProvAuditAchievement(req);
    }


    @PostMapping("/delNationalProjectAchievement")
    @RspResult
    @ApiOperation(value = "删除国家级成果查询", notes = "author: liujun5 2023/08/29")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "删除国家级成果查询条件", value = "请求bean", paramType = "query", dataType = "req")
    })
    public NationalProjectAchievementResp delNationalProjectAchievement(@RequestBody NationalProjectAchievementSelReq req) {
        return fsipProjectAchievementService.delNationalProjectAchievement(req.getAchievementId());
    }


}
