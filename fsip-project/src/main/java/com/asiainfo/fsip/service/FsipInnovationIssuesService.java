package com.asiainfo.fsip.service;

import com.asiainfo.fsip.entity.FsipInnovationIssuesEntity;
import com.asiainfo.fsip.model.*;
import com.asiainfo.mcp.tmc.common.entity.base.PageReq;
import com.github.pagehelper.PageInfo;

public interface FsipInnovationIssuesService {

    Object publish(InnovationIssuesPublishReq publishReq);

    PageInfo<InnovationIssuesListResp> selIssuesList(PageReq<InnovationIssuesListReq> req) throws Exception;

    InnovationIssuesDetailRsp detailQuery(InnovationIssuesDetailReq detailReq);

    String partnerJoin(IssuesJoinPartnerReq joinPartnerReq);

    InnovationIssuesUpdateResp updateIssues(InnovationIssuesUpdateReq req) throws Exception;

    InnovationIssuesDelResp delIssues(FsipInnovationIssuesEntity req) throws Exception;

    Object updateDingState(UpdateDingStateReq updateDingStateReq);

    InnovationIssuesPartnerApplyResp partnerApply(InnovationIssuesPartnerApplyReq req) throws Exception;

    InnovationIssuesCommentResp issuesComment(InnovationIssuesCommentReq req);

    InnovationIssuesPartnerApplyResp partnerApplySel(InnovationIssuesPartnerApplyReq req);
}




