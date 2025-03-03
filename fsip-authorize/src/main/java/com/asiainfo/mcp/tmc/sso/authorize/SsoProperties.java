package com.asiainfo.mcp.tmc.sso.authorize;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class SsoProperties {

    @Value("${sso.server.url}")
    private String ssoServerUrl;

    @Value("${sso.server.appId}")
    private String ssoAppId;

    @Value("${sso.server.whiteUrlList}")
    private String whiteUrlList;

    @Value("${sso.server.getUserInfo}")
    private String qryUserInfoUrl;

    @Value("${sso.igore.url}")
    private String igoreUrl;

}
