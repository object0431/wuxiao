package com.asiainfo.fsip.controller;

import com.asiainfo.fsip.constants.IFsipConstants;
import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.ApprovalService;
import com.asiainfo.fsip.service.CacheService;
import com.asiainfo.fsip.service.ProjectInitiationService;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.util.ExcelUtil;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("initiation")
@Slf4j
@Api("项目立项相关接口")
public class InitiationController {

    @Resource
    private ProjectInitiationService projectInitiationService;
    @Resource
    private ApprovalService approvalService;

    @ApiOperation("项目暂存/办理")
    @PostMapping("/saveProject")
    public SaveProjectRsp saveProject(@RequestBody SaveProjectReq req) {
        if (StringUtils.isEmpty(req.getOperateType())) {
            return SaveProjectRsp.builder().rspCode("8888").rspMsg("操作类型不能为空").build();
        }
        if (StringUtils.isEmpty(req.getProjectName())) {
            return SaveProjectRsp.builder().rspCode("8888").rspMsg("项目名称不能为空").build();
        }
        if ("1".equals(req.getOperateType())) {
            if (req.getApplyReq()==null) {
                return SaveProjectRsp.builder().rspCode("8888").rspMsg("审批信息不能为空").build();
            }
        }
        return projectInitiationService.saveProject(req);
    }

    @ApiOperation("项目列表查询")
    @PostMapping("/queryProject")
    public ProjectQueryRsp queryProject(@RequestBody ProjectQueryReq req) {
        return projectInitiationService.queryProject(req);
    }

    @Autowired
    private CacheService cacheService;
    @ApiOperation("项目列表导出")
    @PostMapping("/queryProjectExport")
    public void queryProjectExport(@RequestBody ProjectQueryReq req, HttpServletResponse response) throws IOException {
        req.setPageNum(1);
        req.setPageSize(Integer.MAX_VALUE);
        Map<String, String> state = cacheService.getParamListByType("STATE");

        ProjectQueryRsp projectQueryRsp = projectInitiationService.queryProject(req);
        projectQueryRsp.getDataList().forEach(rspData -> {
            rspData.setStatus(state.get(rspData.getStatus()));
        });
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("项目列表导出.xlsx", "UTF-8").replaceAll("\\+", "%20"));
        ExcelUtil.exportData(projectQueryRsp.getDataList(), "项目列表", ProjectQueryRsp.RspData.class,response.getOutputStream());


    }

    @ApiOperation("项目详情查询")
    @PostMapping("/detailProject")
    public BaseRsp<ProjectDetailRsp> detailProject(@RequestBody ProjectDetailReq req) {
        if (StringUtils.isEmpty(req.getProjectId())) return RspHelp.fail("8888","项目编码不能为空");
        ProjectDetailRsp projectDetailRsp = projectInitiationService.detailProject(req);
        if (projectDetailRsp == null) {
            return RspHelp.fail("8888","没有查询到项目[".concat(req.getProjectId()).concat("]信息"));
        } else {
            return RspHelp.success(projectDetailRsp);
        }
    }

    @ApiOperation("项目审批")
    @PostMapping("/approvalProject")
    public BaseRsp<Object> approvalProject(@RequestBody ProjectApprovalReq req) {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        ApprovalRetModel approvalRetModel = ApprovalRetModel.builder()
                .targetType(IFsipConstants.TaskType.LXSQ)
                .targetId(req.getProjectId())
                .retType(req.getRetType())
                .remark(req.getRemark())
                .notifyBySms("0").build();
        approvalService.approvalTask(approvalRetModel,staffInfo);
        return RspHelp.success(req.getProjectId());
    }

    @ApiOperation("项目转发")
    @PostMapping("/transferProject")
    public BaseRsp<Object> transferProject(@RequestBody ProjectTransferReq req) {
        if (StringUtils.isEmpty(req.getProjectId())) {
            return RspHelp.fail("8888","项目编码不能为空");
        }
        if (StringUtils.isEmpty(req.getTransferStaffId())) {
            return RspHelp.fail("8888","人员编码不能为空");
        }
        if (StringUtils.isEmpty(req.getTransferStaffName())) {
            return RspHelp.fail("8888","人员名称不能为空");
        }
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        projectInitiationService.transferProject(req, staffInfo);
        return RspHelp.success(req.getProjectId());
    }

    @ApiOperation("项目修改")
    @PostMapping("/modifyProject")
    public ModifyProjectRsp modifyProject(@RequestBody ModifyProjectReq req) {
        if (StringUtils.isEmpty(req.getProjectId())) {
            return  ModifyProjectRsp.builder().rspCode("8888").rspMsg("项目编码不能为空").build();
        }
        if (StringUtils.isEmpty(req.getProjectName())) {
            return  ModifyProjectRsp.builder().rspCode("8888").rspMsg("项目名称不能为空").build();
        }
        return projectInitiationService.modifyProject(req);
    }

    @ApiOperation("撤回项目")
    @PostMapping("/relocateProject")
    public RelocateProjectRsp relocateProject(@RequestBody RelocateProjectReq req) {
        if (StringUtils.isEmpty(req.getProjectId())) {
            return  RelocateProjectRsp.builder().rspCode("8888").rspMsg("项目编码不能为空").build();
        }
        if (StringUtils.isEmpty(req.getProjectName())) {
            return  RelocateProjectRsp.builder().rspCode("8888").rspMsg("项目名称不能为空").build();
        }
        return projectInitiationService.relocateProject(req);
    }

    @ApiOperation("对专家建议评分")
    @PostMapping(path = "/expertAdviceScore")
    public BaseRsp<Void> expertAdviceScore(@RequestBody Map<String, String> conditionMap) {
        try {
            String expertAdviceId = conditionMap.get("expertAdviceId");
            String score = conditionMap.get("score");
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            projectInitiationService.expertAdviceScore(expertAdviceId, Float.parseFloat(score), staffInfo);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute expertAdviceScore", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("删除项目")
    @PostMapping(path = "/del")
    public BaseRsp<Map<String, List<String>>> delProject(@RequestBody Map<String, String> conditionMap) {
        try {
            String projectIds = conditionMap.get("projectIds");
            return RspHelp.success(projectInitiationService.delProjects(projectIds.split(",")));
        } catch (Exception e) {
            log.info("Could not execute del", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }
}
