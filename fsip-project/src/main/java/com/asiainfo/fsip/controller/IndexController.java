package com.asiainfo.fsip.controller;

import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.IndexService;
import com.asiainfo.fsip.service.JobService;
import com.asiainfo.fsip.utils.FileUtils;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.util.ExcelUtil;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("index")
@Slf4j
@Api("首页相关接口")
public class IndexController {
    @Resource
    private IndexService indexService;

    @Resource
    private JobService jobService;

    @ApiOperation("成果信息查询")
    @PostMapping("/achievementQuery")
    public BaseRsp<IndexAchievementQueryRsp> achievementQuery(HttpServletRequest request, @RequestBody IndexAchievementQueryReq req) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();

        String dingToken = request.getParameter("dingToken");
        String deviceType = StringUtils.isEmpty(dingToken) ? "PC" : "DD";
        req.setDeviceType(deviceType);

        IndexAchievementQueryRsp queryRsp = indexService.achievementQuery(req, staffInfo);
        return RspHelp.success(queryRsp);
    }

    @ApiOperation("导出统计信息：包括公司、部门、部门总人数、登录数、登录率、立项数、立项率")
    @GetMapping(value = "/exportStatData")
    public void exportStatData(@RequestParam String statMonth, HttpServletRequest request
            , HttpServletResponse response) throws Exception {
        try {
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            List<LoginStatModel> dataList = jobService.queryStatDataList(statMonth, staffInfo);

            String fileName = FileUtils.buildFileName(request, "登录数据统计.xlsx");

            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);

            ExcelUtil.exportData(dataList, "专业能力认证", LoginStatModel.class, response.getOutputStream());
        } catch (Exception e) {
            log.error("Could not export stat data", e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(e.getMessage());
        }
    }
}
