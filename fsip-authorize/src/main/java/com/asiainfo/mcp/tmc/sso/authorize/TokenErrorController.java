package com.asiainfo.mcp.tmc.sso.authorize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.chinaunicom.usercenter.sso.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wzl
 * @Description:
 * @date 2022/9/19 10:55
 */
@RestController
@Slf4j
public class TokenErrorController extends BasicErrorController {

    @Resource
    private SsoProperties ssoProperties;

    public TokenErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    @Override
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        if (status.value() == 500 && StringUtils.isBlank(request.getParameter("dingToken"))) {
            Map map = new HashMap();
            map.put("rspCode", "0909");
            map.put("rspDesc", ssoLogin());
            return new ResponseEntity(JSON.toJSONString(map), status);
        }

        Object errorMsg = request.getAttribute("ERROR_MSG");

        Map map = new HashMap();
        map.put("rspCode", "9999");
        map.put("rspDesc", errorMsg != null ? errorMsg.toString() : status.getReasonPhrase());
        return new ResponseEntity(JSON.toJSONString(map), status);
    }


    @Override
    protected Map<String, Object> getErrorAttributes(HttpServletRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorMap = super.getErrorAttributes(request, options);

        errorMap.remove("message");
        errorMap.remove("path");
        return errorMap;
    }

    /**
     * 获取登录地址
     *
     * @return
     */
    private String ssoLogin() {
        try {
            HttpUtil getHttpUtil = HttpUtil.getIstance(ssoProperties.getSsoAppId(), ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config", ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config");
            String xml = getHttpUtil.getUserCenterAddr(HttpUtil.PRO_LOGIN, ssoProperties.getSsoServerUrl() + "/uac-sso/pro_login");
            return xml;
        } catch (Exception e) {
            new BusinessException("9999", "登录服务调用异常");
        }
        return null;
    }
}
