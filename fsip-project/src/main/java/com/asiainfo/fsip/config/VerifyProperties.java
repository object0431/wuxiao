package com.asiainfo.fsip.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "verify")
@Data
public class VerifyProperties {

    @Value("${verify.filterOneself:0}")
    private String filterOneself;

    @Value("${verify.url.project.pc}")
    private String projectPcUrl;

    @Value("${verify.url.project.mobile:}")
    private String projectMobileUrl;

    @Value("${verify.url.consult.pc:}")
    private String consultPcUrl;

    @Value("${verify.url.consult.mobile:}")
    private String consultMobileUrl;

    @Value("${verify.url.city2Prov.pc:}")
    private String city2ProvPcUrl;

    @Value("${verify.url.cgsq.approval.pc:}")
    private String cgsqApprovalUrl;

    @Value("${verify.url.cgsq.review.pc:}")
    private String cgsqReviewUrl;

    @Value("${verify.url.issues.publish.pc:}")
    private String issuesPublishPcUrl;

    @Value("${verify.url.issues.join.pc:}")
    private String issuesjoinPcUrl;
}
