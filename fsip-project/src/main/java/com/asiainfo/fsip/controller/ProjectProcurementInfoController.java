package com.asiainfo.fsip.controller;

import com.alibaba.fastjson.JSON;
import com.asiainfo.fsip.entity.ProjectProcurementInfo;
import com.asiainfo.fsip.mapper.fsip.ProjectProcurementInfoMapper;
import com.asiainfo.fsip.model.ParamModel;
import com.asiainfo.fsip.model.ProjectProcurementInfoModel;
import com.asiainfo.fsip.service.ParamService;
import com.asiainfo.fsip.service.impl.ProjectProcurementInfoService;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author BEJSON
 * @description project_procurement_info控制器
 * @date 2024-04-15
 */
@Slf4j
@RestController
@RequestMapping("/projectProcurementInfo")
@Api(tags = "项目采购相关接口")
public class ProjectProcurementInfoController {

    @Autowired
    private ProjectProcurementInfoMapper projectProcurementInfoMapper;
    @Autowired
    ParamService paramService;
    @Autowired
    ProjectProcurementInfoService service;

    @ApiOperation("新增或编辑采购信息")
    @PostMapping("/save")
    public BaseRsp saveProcurementInfo(@RequestBody ProjectProcurementInfoModel model) throws ParseException {
        log.info("新增或编辑采购信息:" + JSON.toJSONString(model));
        service.mySave(model);
        return RspHelp.success("SUCCESS");
    }

    @ApiOperation("列表")
    @PostMapping("/list")
    public BaseRsp<IPage<ProjectProcurementInfoModel.ListRsp>> list(@RequestBody ProjectProcurementInfoModel.ListQuery req) {
        log.info("查询列表:" + JSON.toJSONString(req));
        QueryWrapper<ProjectProcurementInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("staff_id", StaffInfoUtil.getStaff().getId());
        if (req.getName() != null) {
            queryWrapper.like("name", req.getName());
        }
        if (req.getRegion() != null) {
            queryWrapper.eq("region", req.getRegion());
        }
        Map<String, String> collect1 = paramService.queryCompanyList().stream().collect(Collectors.toMap(ParamModel::getCode, ParamModel::getName));
        PageHelper.startPage(req.getPageIndex(), req.getPageSize());
        List<ProjectProcurementInfo> infos = projectProcurementInfoMapper.selectList(queryWrapper);
        List<ProjectProcurementInfoModel.ListRsp> collect = infos.stream().map(r -> {
            ProjectProcurementInfoModel.ListRsp rsp = new ProjectProcurementInfoModel.ListRsp();
            BeanUtils.copyProperties(r, rsp);
            rsp.setRegionName(collect1.get(r.getRegion()));
            return rsp;
        }).collect(Collectors.toList());
        PageInfo<ProjectProcurementInfo> pageInfo = new PageInfo<>(infos);
        IPage<ProjectProcurementInfoModel.ListRsp> page1 = new Page<>();
        page1.setTotal(pageInfo.getTotal());
        page1.setSize(pageInfo.getSize());
        page1.setCurrent(pageInfo.getPageNum());
        page1.setRecords(collect);
        return RspHelp.success(page1);
    }

    @ApiOperation("查看明细")
    @PostMapping("/detail")
    public BaseRsp<ProjectProcurementInfoModel> detail(@RequestBody ProjectProcurementInfoModel.DetailReq req) {
        log.info("查询列表:" + JSON.toJSONString(req));
        Map<String, String> collect = paramService.queryCompanyList().stream().collect(Collectors.toMap(ParamModel::getCode, ParamModel::getName));

        ProjectProcurementInfoModel detail = service.detail(req.getId());
        detail.setRegionName(collect.get(detail.getRegion()));
        return RspHelp.success(detail);
    }





    @ApiOperation("导出")
    @PostMapping("/export")
    public void export(@RequestBody ProjectProcurementInfoModel.ListQuery req, HttpServletRequest request, HttpServletResponse response) {
        log.info("导出:" + JSON.toJSONString(req));
        QueryWrapper<ProjectProcurementInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("staff_id", StaffInfoUtil.getStaff().getId());
        if (req.getName() != null) {
            queryWrapper.like("name", req.getName());
        }
        if (req.getRegion() != null) {
            queryWrapper.eq("region", req.getRegion());
        }
        Map<String, String> collect1 = paramService.queryCompanyList().stream().collect(Collectors.toMap(ParamModel::getCode, ParamModel::getName));
        List<ProjectProcurementInfo> infos = projectProcurementInfoMapper.selectList(queryWrapper);
        //查询一个最新的进程

        try (Workbook workbook = new XSSFWorkbook(this.getClass().getClassLoader().getResourceAsStream("templates/采购项目进度表）.xlsx"));) {
            CellStyle cellStyle = workbook.createCellStyle();

            // 设置航高为38磅
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 10);
            font.setFontName("仿宋");
            cellStyle.setFont(font);

            Sheet sheetAt = workbook.getSheetAt(0);
            int startROw = 3;
            for (int i = 0; i < infos.size(); i++) {
                int j = 0;
                ProjectProcurementInfo info = infos.get(i);
                Row row = sheetAt.createRow(startROw + i);
                row.createCell(j++).setCellValue(i+1);
                row.createCell(j++).setCellValue(info.getName());
                row.createCell(j++).setCellValue(collect1.get(info.getRegion()));
                row.createCell(j++).setCellValue(info.getTenderAgent());
                row.createCell(j++).setCellValue(info.getProjectManager());
                row.createCell(j++).setCellValue(info.getRequirementManager());
                row.createCell(j++).setCellValue(info.getProcurementManager());
                row.createCell(j++).setCellValue(String.valueOf(info.getBudgetAmount()));
                row.createCell(j++).setCellValue(info.getProcurementMethod());
                row.createCell(j++).setCellValue(String.valueOf(info.getAgencyServiceFee()));
//                row.createCell(j++).setCellValue(String.valueOf(info.getMeetingProcurementDecision()));
//                row.createCell(j++).setCellValue(String.valueOf(info.getMeetingExecutiveCommittee()));
                row.createCell(j++).setCellValue(String.valueOf(info.getRequirementIntegration()));
//                row.createCell(j++).setCellValue(String.valueOf(info.getProcessStatus()));
                row.createCell(j++).setCellValue(String.valueOf(info.getRequirementApproval()));
//                row.createCell(j++).setCellValue(String.valueOf(info.getProposalApproval()));
//                row.createCell(j++).setCellValue(String.valueOf(info.getEnrollmentTime()));
//                row.createCell(j++).setCellValue(String.valueOf(info.getReviewTime()));
//                row.createCell(j++).setCellValue(String.valueOf(info.getInternalReviewer()));
//                row.createCell(j++).setCellValue(String.valueOf(info.getExternalReviewer()));
                row.createCell(j++).setCellValue(String.valueOf(info.getObjectionPublicity()));
                row.createCell(j++).setCellValue(String.valueOf(info.getResultApproval()));
//                row.createCell(j++).setCellValue(String.valueOf(info.getContractSigningOrganization()));
//                row.createCell(j++).setCellValue(String.valueOf(info.getContractSigningStatus()));
                row.createCell(j++).setCellValue(String.valueOf(info.getPerformanceBondPayment()));
                row.createCell(j++).setCellValue(String.valueOf(info.getPerformanceBondRefund()));
                row.createCell(j++).setCellValue(String.valueOf(info.getNegotiationFailureToTender()));
                row.createCell(j++).setCellValue(String.valueOf(info.getRemarks()));
                row.setRowStyle(cellStyle);
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("采购项目进度表.xlsx", "UTF-8").replaceAll("\\+", "%20"));
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.error("导出失败", e);
        }
    }

}
