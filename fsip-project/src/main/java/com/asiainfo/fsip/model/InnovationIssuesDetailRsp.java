package com.asiainfo.fsip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InnovationIssuesDetailRsp {
    private String title;
    private String content;
    private String canJoin;
    private int partnerNum;
    private int havePartnerNum;
    private String applierId;
    private String applierName;
    private String applyCompanyId;
    private String applyCompanyName;
    private String applyDeptId;
    private String applyDate;
    private String followFlag;
    private String evaluateFlag;
    private String evaluateType;
    private String applyPartnerFlag;
    private String applyPartnerDate;
    private String applyPartnerReason;
    private String partnerApprovalOpinion;
    private int likeNum;
    private int dislikeNum;
    private InnovationIssuesPublishReq.Scope scope;
    private String scopeValue;
    private List<Partner> partnerList;
    private List<Comment> commentList;
    private List<Attr> attrList;
    private List<ApplyLog> applyLogList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Partner{
        private String partnerId;
        private String partnerName;
        private String partnerCompanyId;
        private String partnerDepartId;
        private String partnerCompanyName;
        private String partnerDepartName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment{
        private String commentId;
        private String staffId;
        private String staffName;
        private String content;
        private String commentDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attr{
        private String attrType;
        private String attrCode;
        private String attrValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplyLog{
        private String partnerId;
        private String partnerName;
        private String applyReason;
        private String applyDate;
        private String applyState;
        private String replyContent;
        private String replyDate;
    }
}
