package com.asiainfo.fsip.controller;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.fsip.entity.FsipInnovationIssuesEntity;
import com.asiainfo.fsip.model.*;
import com.asiainfo.fsip.service.FsipInnovationIssuesService;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("innovationIssues")
@Slf4j
@Api("创新议题相关接口")
public class InnovationIssuesController {

    @Resource
    private FsipInnovationIssuesService fsipInnovationIssuesService;

    @ApiOperation("创新议题发布")
    @PostMapping("/publish")
    public BaseRsp<Object> publish(@RequestBody InnovationIssuesPublishReq publishReq) {
        if (StringUtils.isEmpty(publishReq.getTitle())) {
            return RspHelp.fail(RspHelp.PARAM_VALID_ERROR_CODE, "议题标题不能为空");
        }
        if (StringUtils.isEmpty(publishReq.getContent())) {
            return RspHelp.fail(RspHelp.PARAM_VALID_ERROR_CODE, "议题内容不能为空");
        }
        if (publishReq.getScope() == null) {
            return RspHelp.fail(RspHelp.PARAM_VALID_ERROR_CODE, "议题发布范围不能为空");
        } else {
            if (StringUtils.isEmpty(publishReq.getScope().getType())) {
                return RspHelp.fail(RspHelp.PARAM_VALID_ERROR_CODE, "议题发布范围类型不能为空");
            }
            if (CollectionUtils.isEmpty(publishReq.getScope().getValues())) {
                return RspHelp.fail(RspHelp.PARAM_VALID_ERROR_CODE, "议题发布范围值不能为空");
            }
        }
        Object rspBody = fsipInnovationIssuesService.publish(publishReq);
        return RspHelp.success(rspBody);
    }

    @ApiOperation("创新议题明细查询")
    @PostMapping("/detailQuery")
    public BaseRsp<InnovationIssuesDetailRsp> detailQuery(@RequestBody InnovationIssuesDetailReq detailReq) {
        if (StringUtils.isEmpty(detailReq.getIssuesId())) {
            return RspHelp.fail(RspHelp.PARAM_VALID_ERROR_CODE, "议题编码不能为空");
        }
        InnovationIssuesDetailRsp rspBody = fsipInnovationIssuesService.detailQuery(detailReq);
        return RspHelp.success(rspBody);
    }

    @ApiOperation("创新议题加入合伙人")
    @PostMapping("/joinPartner")
    public BaseRsp<Object> joinPartner(@RequestBody IssuesJoinPartnerReq joinPartnerReq) {
        if (StringUtils.isEmpty(joinPartnerReq.getIssuesId())) {
            return RspHelp.fail(RspHelp.PARAM_VALID_ERROR_CODE, "议题编码不能为空");
        }
        if (StringUtils.isEmpty(joinPartnerReq.getJoinReason())) {
            return RspHelp.fail(RspHelp.PARAM_VALID_ERROR_CODE, "申请原因不能为空");
        }
        String joinRecordId = fsipInnovationIssuesService.partnerJoin(joinPartnerReq);
        return RspHelp.success(joinRecordId);
    }

    @ApiOperation("议题列表")
    @RspResult
    @PostMapping("/issuesList")
    public PageInfo<InnovationIssuesListResp> issuesList(@RequestBody PageReq<InnovationIssuesListReq> req) throws Exception {
        return fsipInnovationIssuesService.selIssuesList(req);
    }

    @ApiOperation("议题修改")
    @RspResult
    @PostMapping("/updateIssues")
    public InnovationIssuesUpdateResp updateIssues(@RequestBody InnovationIssuesUpdateReq req) {
        try {
            return fsipInnovationIssuesService.updateIssues(req);
        } catch (Exception e) {
            e.printStackTrace();
            return InnovationIssuesUpdateResp.builder()
                    .respMsg("8888")
                    .respMsg("修改失败,失败详情：" + e.getMessage())
                    .build();
        }
    }

    @ApiOperation("删除议题")
    @RspResult
    @PostMapping("/delIssues")
    public InnovationIssuesDelResp delIssues(@RequestBody FsipInnovationIssuesEntity req) {
        try {
            return fsipInnovationIssuesService.delIssues(req);
        } catch (Exception e) {
            e.printStackTrace();
            return InnovationIssuesDelResp.builder()
                    .respMsg("8888")
                    .respMsg("删除失败,失败详情：" + e.getMessage())
                    .build();
        }
    }

    @ApiOperation("合伙人审核")
    @RspResult
    @PostMapping("/partnerApply")
    public InnovationIssuesPartnerApplyResp partnerApply(@RequestBody InnovationIssuesPartnerApplyReq req) {
        try {
            return fsipInnovationIssuesService.partnerApply(req);
        } catch (Exception e) {
            log.error("Could not execute partnerApply, req = " + JSONObject.toJSONString(req), e);
            return InnovationIssuesPartnerApplyResp.builder()
                    .respMsg("8888")
                    .respMsg("合伙人审核,失败详情：" + e.getMessage())
                    .build();
        }
    }

    @ApiOperation("议题评论")
    @RspResult
    @PostMapping("/issuesComment")
    public InnovationIssuesCommentResp issuesComment(@RequestBody InnovationIssuesCommentReq req) {
        return fsipInnovationIssuesService.issuesComment(req);
    }

    @ApiOperation("更新钉钉消息状态")
    @PostMapping("/updateDingState")
    public BaseRsp<Object> updateDingState(@RequestBody UpdateDingStateReq req) {
        if (StringUtils.isEmpty(req.getIssuesId())) {
            return RspHelp.fail(RspHelp.PARAM_VALID_ERROR_CODE, "议题编码不能为空");
        }
        if (StringUtils.isEmpty(req.getMessageType())) {
            return RspHelp.fail(RspHelp.PARAM_VALID_ERROR_CODE, "消息类型不能为空");
        }
        Object rspBody = fsipInnovationIssuesService.updateDingState(req);
        return RspHelp.success(rspBody);
    }

    @ApiOperation("合伙人申请数据查询")
    @RspResult
    @PostMapping("/partnerApplySel")
    public InnovationIssuesPartnerApplyResp partnerApplySel(@RequestBody InnovationIssuesPartnerApplyReq req) {
        if (StringUtils.isEmpty(req.getIssuesId())) {
            return InnovationIssuesPartnerApplyResp.builder().respCode("8888").respMsg("议题编码不能为空").build();
        }
        if (StringUtils.isEmpty(req.getPartnerId())) {
            return InnovationIssuesPartnerApplyResp.builder().respCode("8888").respMsg("合伙人编码不能为空").build();
        }
        return fsipInnovationIssuesService.partnerApplySel(req);
    }

}
