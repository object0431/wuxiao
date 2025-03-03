package com.asiainfo.fsip.controller;

import com.asiainfo.fsip.model.LoginEmpInfoModel;
import com.asiainfo.fsip.model.LoginStatisticsModel;
import com.asiainfo.fsip.model.ProjectStatisticsProTypeModel;
import com.asiainfo.fsip.model.TimeScopeReq;
import com.asiainfo.fsip.service.ProjectClassificationService;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.ExcelUtil;
import com.asiainfo.mcp.tmc.common.util.FileUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @program: fsip-backend
 * @create: 2024-01-02 14:36
 **/
@RestController
@RequestMapping("projectStatistics")
@Slf4j
@Api("项目统计相关控制器")
public class ProjectStatisticsController {

    @Resource
    private ProjectClassificationService projectService;


    /**
     * 查找项目类型立项分类统计列表信息
     * 调用
     * {@link ProjectClassificationService#getProjectStatisticsInfoByType(PageReq)}
     * <p>直接查询SQL即可
     */
    @PostMapping("/projectClassification")
    @ApiOperation("项目分类统计信息列表接口")
    public PageInfo<ProjectStatisticsProTypeModel> findClassificationStatistics(@RequestBody PageReq<TimeScopeReq> dateMap){
        return projectService.getProjectStatisticsInfoByType(dateMap);
    }

    /**
     * 导出立项分类统计列表信息
     * 调用
     * {@link ProjectClassificationService#getProjectStatisticsInfoByType(TimeScopeReq)}
     * <p>获取数据导出数据统计报表
     */
    @GetMapping("/projectClassification")
    @ApiOperation("立项分类统计报表下载接口")
    public void downloadProjectClassification(HttpServletResponse response, HttpServletRequest request){
        try {
            // 测试
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String type = request.getParameter("type");
            String companyId = request.getParameter("companyId");
            String deptId = request.getParameter("deptId");
            TimeScopeReq timeScopeReq = TimeScopeReq.builder().startDate(startDate).endDate(endDate).companyId(companyId).deptId(deptId).type(type).build();

            List<ProjectStatisticsProTypeModel> listData = projectService.getProjectStatisticsInfoByType(timeScopeReq);

            String fileName = FileUtils.buildFileName(request, "项目分类统计报表.xlsx");

            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            // 根据类型利用反射修改 ProjectStatisticsProTypeModel#statType 字段 Excel name 值
            // getProjectStatisticsInfoByType() 已经判断 type 不为 null
            Class<?> dataClass = projectService.builderExcelName(type);

            ExcelUtil.exportData(listData, "统计数据", dataClass, response.getOutputStream());
        }catch (Exception e){
            log.error("立项分类统计报表导出失败！",e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 统计登录数列表信息
     */
    @PostMapping("/loginStatistics")
    @ApiOperation("统计登录数列表信息")
    public PageInfo<LoginStatisticsModel> findLoginStatInfo(@RequestBody PageReq<TimeScopeReq> pageReq){
        return projectService.findLoginStatisticsInfo(pageReq);
    }

    /**
     * 导出登录数列表信息
     */
    @GetMapping("/loginStatistics")
    @ApiOperation("导出统计登录数列表信息")
    public void findLoginStatInfo(HttpServletResponse response, HttpServletRequest request){
        try {
            // 测试
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String companyId = request.getParameter("companyId");
            String deptId = request.getParameter("deptId");
            TimeScopeReq timeScopeReq = TimeScopeReq.builder().startDate(startDate).endDate(endDate).companyId(companyId).deptId(deptId).build();

            List<LoginStatisticsModel> listData = projectService.findLoginStatisticsInfo(timeScopeReq);
            // 查询登陆表相关信息
            String fileName = FileUtils.buildFileName(request, "统计登陆报表.xlsx");

            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            ExcelUtil.exportData(listData, "统计数据", LoginStatisticsModel.class, response.getOutputStream());
        }catch (Exception e){
            log.error("统计登陆报表导出失败！",e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 查询登录人员信息列表
     */
    @PostMapping("/loginEmpInfo")
    @ApiOperation("查询登录人员信息列表")
    public PageInfo<LoginEmpInfoModel> findLoginEmpInfo(@RequestBody PageReq<TimeScopeReq> pageReq){
        return projectService.findLoginEmpInfo(pageReq);
    }

    /**
     * 查询登录人员信息列表
     */
    @GetMapping("/loginEmpInfo")
    @ApiOperation("导出登录人员信息报表")
    public void findLoginEmpInfo(HttpServletResponse response, HttpServletRequest request){
        try {
            // 测试
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String companyId = request.getParameter("companyId");
            String deptId = request.getParameter("deptId");
            String staffName = request.getParameter("staffName");
            TimeScopeReq timeScopeReq = TimeScopeReq.builder().startDate(startDate).endDate(endDate).staffName(staffName).companyId(companyId).deptId(deptId).build();
            // 更新登录相关表数据
            List<LoginEmpInfoModel> listData = projectService.findLoginEmpInfo(timeScopeReq);
            // 查询登陆表相关信息
            String fileName = FileUtils.buildFileName(request, "登录人员信息报表.xlsx");

            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            ExcelUtil.exportData(listData, "登录明细", LoginEmpInfoModel.class, response.getOutputStream());
        }catch (Exception e){
            log.error("导出登录人员信息报表失败！",e);
            throw new BusinessException(e.getMessage());
        }
    }



}
