package com.asiainfo.fsip.controller;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.entity.FsipOpinionsEntity;
import com.asiainfo.fsip.model.ApprovalNodeModel;
import com.asiainfo.fsip.model.ApprovalRetModel;
import com.asiainfo.fsip.model.FlowLogModel;
import com.asiainfo.fsip.service.ApprovalService;
import com.asiainfo.fsip.service.FlowLogService;
import com.asiainfo.fsip.service.OpinionsService;
import com.asiainfo.fsip.service.StaffInfoService;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.sso.util.StaffInfoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("approval")
@Slf4j
@Api("审批相关服务")
public class ApprovalController {

    @Resource
    private OpinionsService opinionsService;

    @Resource
    private ApprovalService approvalService;

    @Resource
    private FlowLogService flowLogService;

    @Resource
    private StaffInfoService staffInfoService;

    /**
     * 获取常见意见
     */
    @ApiOperation("获取常见意见")
    @GetMapping("/getOpinions")
    @RspResult
    public List<FsipOpinionsEntity> getOpinions() {
        try {
            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            return opinionsService.getOpinionsByStaffId(staffInfo.getHrEmpCode());
        } catch (Exception e) {
            log.info("Could not execute getOpinions", e);
            throw e;
        }
    }

    /**
     * 新增常见意见
     */
    @ApiOperation("新增常见意见")
    @PostMapping("/addOpinion")
    public BaseRsp<Void> addOpinion(@RequestParam("remark") String remark) {
        try {
            if(StringUtils.isNotEmpty(remark)) {
                StaffInfo staffInfo = StaffInfoUtil.getStaff();
                opinionsService.addOpinions(staffInfo.getHrEmpCode(), remark);
            }

            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute addOpinions", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    /**
     * 删除常见意见
     */
    @ApiOperation("删除常见意见")
    @GetMapping("/delOpinion")
    public BaseRsp<Void> delOpinion(@RequestParam("id") String id) {
        try {
            opinionsService.deleteOpinions(id);
            return RspHelp.success(null);
        } catch (Exception e) {
            log.info("Could not execute delOpinion", e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("查询审批人信息")
    @PostMapping("/getApprovalInfo")
    public BaseRsp<List<ApprovalNodeModel>> getApprovalInfo(@RequestBody Map<String, String> conditionMap) {
        try {
            String approvalType = conditionMap.get("approvalType");
            String extId = conditionMap.get("extId");
            // 是否是省级员工
            String hasProvince = conditionMap.get("hasProvince");
            if ("1".equals(hasProvince) && "LXSQ".equals(approvalType)){
                approvalType = "PLXSQ";
            }

            StaffInfo staffInfo = StaffInfoUtil.getStaff();
            List<ApprovalNodeModel> approvalModelList = staffInfoService.getApprovalList(approvalType, extId, staffInfo);
            return RspHelp.success(approvalModelList);
        } catch (Exception e) {
            log.error("Could not execute approvalTask, conditionMap = " + JSONObject.toJSONString(conditionMap), e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("通用审批")
    @PostMapping("/approvalTask")
    public BaseRsp<Void> approvalTask(@RequestBody ApprovalRetModel approvalRetModel) {
        try {
            approvalService.approvalTask(approvalRetModel, StaffInfoUtil.getStaff());
            return RspHelp.success(null);
        } catch (Exception e) {
            log.error("Could not execute approvalTask, approvalRetModel = " + JSONObject.toJSONString(approvalRetModel), e);
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("查询流转意见")
    @PostMapping("/getFlowLog")
    public BaseRsp<List<FlowLogModel>> getFlowLog(@RequestBody Map<String, String> queryMap) {
        try {
            String flowType = queryMap.get("flowType");
            List flowTypeList = new ArrayList();
            if (StringUtils.isNotEmpty(flowType)) {
                String[] flowTypes=  flowType.split(",");
                for (int i = 0; i < flowTypes.length; i++) {
                    flowTypeList.add(flowTypes[i]);
                }
            }
            String extId = queryMap.get("extId");

            List<FlowLogModel> flowLogModelList = flowLogService.queryFlowLogByFlowTypeExtId(flowTypeList, extId);
            return RspHelp.success(flowLogModelList);
        } catch (Exception e) {
            log.error("Could not execute getFlowLog, queryMap = " + JSONObject.toJSONString(queryMap));
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }

    @ApiOperation("查询当前节点")
    @PostMapping("/getCurrentNode")
    public BaseRsp<FlowLogModel> getCurrentNode(@RequestBody Map<String, String> queryMap) {
        try {
            String flowType = queryMap.get("flowType");
            String extId = queryMap.get("extId");

            FlowLogModel flowLogModel = flowLogService.queryCurrentNode(flowType, extId);
            return RspHelp.success(flowLogModel);
        } catch (Exception e) {
            log.error("Could not execute getCurrentNode, queryMap = " + JSONObject.toJSONString(queryMap));
            return RspHelp.fail(RspHelp.ERROR_CODE, e.getMessage());
        }
    }
}
