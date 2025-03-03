package com.asiainfo.fsip.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.entity.AchievementArchiveReq;
import com.asiainfo.fsip.entity.FsipAppendixEntity;
import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.FsipAppendixService;
import com.asiainfo.fsip.service.ProjectAchievementService;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.ExcelUtil;
import com.asiainfo.mcp.tmc.common.util.FileUtils;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("achievement")
@Slf4j
@Api(tags = "项目成果相关接口")
public class AchievementController {
    @Resource
    private ProjectAchievementService projectService;
    @Resource
    private FsipAppendixService appendixService;

    @ApiOperation("保存项目")
    @PostMapping(path = "/saveProject")
    public BaseRsp<String> saveProject(@RequestBody ProjectAchievementModel projectModel) {
        try {
            log.info("saveProject req str::" + JSONObject.toJSONString(projectModel));
            String operateType = IConstants.State.ZC;
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            String projectId = projectService.saveProject(projectModel, operateType, staffInfo).getAchievementId();
            log.info("saveProject rsp str::" + projectId);
            return RspHelp.success(projectId);
        } catch (Exception e) {
            log.info("Could not execute saveProject", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("项目办理接口")
    @PostMapping(path = "/handleProject")
    public BaseRsp<Object> handleProject(@RequestBody ProjectAchievementAddReq req) {
        log.info("handleProject req str::" + JSONObject.toJSONString(req));
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        BaseRsp<Object> rsp = projectService.handleProject(req, staffInfo);
        log.info("handleProject rsp str::" + JSONObject.toJSONString(rsp));
        return rsp;
    }

    @ApiOperation("获取下个环节信息")
    @PostMapping(path = "/next-approval-info")
    public BaseRsp<NextApprovalNodeModel> getNextApprovalInfo(@RequestBody ProjectAchievementModel projectModel) {
        try {
            if (ObjectUtils.isEmpty(projectModel.getProjectId())) {
                throw new BusinessException("500", "参数错误");
            }
            NextApprovalNodeModel model = projectService.getNextApprovalInfo(projectModel);
            return RspHelp.success(model);
        } catch (Exception e) {
            log.info("Could not execute getNextApprovalInfo", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("项目详情")
    @PostMapping(path = "/getProject")
    public BaseRsp<ProjectAchievementModel> getProject(@RequestBody Map<String, String> conditionMap) {
        try {
            log.info("getProject req str::" + JSONObject.toJSONString(conditionMap));
            String projectId = conditionMap.get("projectId");
            ProjectAchievementModel model = projectService.getProject(projectId);
            log.info("getProject rsp str::" + model);
            return RspHelp.success(model);
        } catch (Exception e) {
            log.info("Could not execute getProject", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("检索项目")
    @PostMapping(path = "/search")
    public BaseRsp<PageInfo<ProjectAchievementModel>> searchProject(@RequestBody PageReq<ProjectAchievementSearchModel> pageReq) {
        try {
            log.info("search req str::" + JSONObject.toJSONString(pageReq));
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            PageInfo<ProjectAchievementModel> rsp = projectService.searchProject(pageReq, staffInfo);
            log.info("search rsp str::" + JSONObject.toJSONString(rsp));
            return RspHelp.success(rsp);
        } catch (Exception e) {
            log.info("Could not execute search", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("删除项目")
    @PostMapping(path = "/del")
    public BaseRsp<Map<String, List<String>>> delProject(@RequestBody Map<String, String> conditionMap) {
        try {
            log.info("del req str::" + JSONObject.toJSONString(conditionMap));
            String projectIds = conditionMap.get("projectIds");
            Map<String, List<String>> rsp = projectService.delProjects(projectIds.split(","));
            log.info("del rsp str::" + JSONObject.toJSONString(rsp));
            return RspHelp.success(rsp);
        } catch (Exception e) {
            log.info("Could not execute del", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("确认项目")
    @PostMapping(path = "/confirm")
    public String confirmProject(@RequestBody ProjectAchievementModel projectModel) {
        return projectService.confirmProject(projectModel);
    }

    @ApiOperation("专家建议")
    @PostMapping(path = "/expertAdvice")
    public BaseRsp<Void> expertAdvice(@RequestBody Map<String, String> conditionMap) {
        try {
            log.info("expertAdvice req str::" + JSONObject.toJSONString(conditionMap));
            String projectId = conditionMap.get("projectId");
            String suggestion = conditionMap.get("suggestion");
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            projectService.expertAdvice(projectId, suggestion, staffInfo);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute expertAdvice", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("对专家建议评分")
    @PostMapping(path = "/expertAdviceScore")
    public BaseRsp<Void> expertAdviceScore(@RequestBody Map<String, String> conditionMap) {
        try {
            log.info("expertAdviceScore req str::" + JSONObject.toJSONString(conditionMap));
            String expertAdviceId = conditionMap.get("expertAdviceId");
            String score = conditionMap.get("score");
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            projectService.expertAdviceScore(expertAdviceId, Float.parseFloat(score), staffInfo);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute expertAdviceScore", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("评委会评分")
    @PostMapping(path = "/achievementReview")
    public BaseRsp<Void> achievementReview(@RequestBody AchievementReviewReq req) {
        try {
            log.info("achievementReview req str::" + JSONObject.toJSONString(req));
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            projectService.achievementReview(req, staffInfo);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute achievementReview", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("撤回项目")
    @PostMapping(path = "/recallProject")
    public BaseRsp<Void> recallProject(@RequestBody Map<String, String> conditionMap) {
        try {
            log.info("recallProject req str::" + JSONObject.toJSONString(conditionMap));
            String projectId = conditionMap.get("projectId");
            projectService.recallProject(projectId);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute achievementReview", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("成果评分列表查询")
    @PostMapping(path = "/zgSearch")
    public BaseRsp<ZgSearchModel> zgSearch(@RequestBody PageReq<AchievementPushQryReq> pageReq) {
        try {
            log.info("zgSearch req str::" + JSONObject.toJSONString(pageReq));
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            PageInfo<ProjectAchievementModel> rsp = projectService.queryReviewList(pageReq, staffInfo);

            ZgSearchModel model = new ZgSearchModel();
            model.setModelPageInfo(rsp);
            String pendingCode = pageReq.getReqParam().getPendingCode();
            if (!StringUtils.isEmpty(pendingCode)) {
                List<FsipAppendixEntity> appendixList = appendixService.lambdaQuery()
                        .eq(FsipAppendixEntity::getExtId, pendingCode)
                        .list();
                if (!ObjectUtils.isEmpty(appendixList)) {
                    model.setAppendixList(appendixList.parallelStream().map(t -> t.getFilePath()).collect(Collectors.toList()));
                }
            }
            log.info("zgSearch rsp str::" + JSONObject.toJSONString(model));
            return RspHelp.success(model);
        } catch (Exception e) {
            log.info("Could not execute zgSearch", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @PostMapping("/zgSearchExport")
    @RspResult
    @ApiOperation(value = "成果评分列表查询导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "成果评分列表查询导出", value = "成果评分列表查询导出", paramType = "query", dataType = "req")
    })
    public void zgSearchExport(@RequestBody PageReq<AchievementPushQryReq> req, HttpServletResponse response) throws IOException {
        StaffInfo staffInfo = StaffInfoUtil.getStaff();
        req.setPageSize(Integer.MAX_VALUE);
        PageInfo<ProjectAchievementModel> p = projectService.queryReviewList(req, staffInfo);

        List<ProjectAchievementExportModel> dataList = new ArrayList<>();
        if(null!=p && null!=p.getList() && p.getList().size()>0) {
            p.getList().forEach(entity -> {
                ProjectAchievementExportModel model = new ProjectAchievementExportModel();
                BeanUtil.copyProperties(entity, model);
                dataList.add(model);
            });
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("成果评分列表导出.xlsx", "UTF-8").replaceAll("\\+", "%20"));
        ExcelUtil.exportData(dataList, "成果评分列表", ProjectAchievementExportModel.class,response.getOutputStream());
    }

    @ApiOperation("专干推送给评委会")
    @PostMapping(path = "/zgPush")
    public BaseRsp<Void> zgPush(@RequestBody ZgPushReq req) {
        try {
            log.info("zgPush req str::" + JSONObject.toJSONString(req));
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            projectService.zgPush(req, staffInfo);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute zgPush", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("市级转省级成果申請")
    @PostMapping(path = "/city2ProvApply")
    @RspResult
    public void city2ProvApply(@RequestBody City2ProvAchievementApplyReq req) {
        try {
            log.info("city2ProvApply req str::" + JSONObject.toJSONString(req));
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            projectService.city2ProvApply(req, staffInfo);
        } catch (Exception e) {
            log.info("Could not execute city2ProvApply", e);
            throw e;
        }
    }

    @ApiOperation("导出项目成果信息")
    @PostMapping(path = "/exportProjectResults")
    public void exportProjectResults(@RequestBody ProjectAchievementSearchModel req,HttpServletResponse response, HttpServletRequest request) {
        try {
            List<ProjectAchievementModel> listData = projectService.searchProject(req);

            // 查询登陆表相关信息
            String fileName = FileUtils.buildFileName(request, "项目成果信息表.xlsx");
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            ExcelUtil.exportData(listData, "项目成果信息", ProjectAchievementModel.class, response.getOutputStream());
        }catch (Exception e){
            log.error("导出项目成果信息表失败！",e);
            throw new BusinessException(e.getMessage());
        }
    }

    @ApiOperation("查询未完成评分的评审人员列表")
    @PostMapping(path = "/queryReviewJudgeList")
    @RspResult
    public List<StaffInfo> queryReviewJudgeList(@RequestParam String queryType) {
        try {
            log.info("queryReviewJudgeList queryType = " + queryType);
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            return projectService.queryReviewJudgeList(queryType, staffInfo);
        } catch (Exception e) {
            log.info("Could not execute urgingReview", e);
            throw e;
        }
    }

    @ApiOperation("评分催办")
    @PostMapping(path = "/urgingReview")
    @RspResult
    public void urgingReview(@RequestBody ReviewUrgingReq req) {
        try {
            log.info("urgingReview req str::" + JSONObject.toJSONString(req));
            projectService.urgingReview(req);
        } catch (Exception e) {
            log.info("Could not execute urgingReview", e);
            throw e;
        }
    }

    @ApiOperation("成果归档")
    @PostMapping(path = "/achievementArchive")
    public BaseRsp achievementArchive(@RequestBody AchievementArchiveReq req) {
        try {
            log.info("achievementArchive req str::" + JSONObject.toJSONString(req));
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            projectService.achievementArchive(req, staffInfo);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute achievementArchive", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }


}
