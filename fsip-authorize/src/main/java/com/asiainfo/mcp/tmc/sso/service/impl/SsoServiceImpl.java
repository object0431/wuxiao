package com.asiainfo.mcp.tmc.sso.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.util.TranceNoTool;
import com.asiainfo.mcp.tmc.sso.authorize.SsoProperties;
import com.asiainfo.mcp.tmc.sso.mapper.SsoTmcLecturerInfoMapper;
import com.asiainfo.mcp.tmc.sso.mapper.SsoTmcPermissionMapper;
import com.asiainfo.mcp.tmc.sso.service.LoginService;
import com.asiainfo.mcp.tmc.sso.service.SsoService;
//import com.chinaunicom.usercenter.sso.util.HttpUtil;
//import com.chinaunicom.usercenter.sso.util.UserEntry;
//import com.chinaunicom.usercenter.sso.util.XmlHelper;
//import com.chinaunicom.usercenter.sso.util.app.HttpUtilApp;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author wzl
 * @Description:
 * @date 2022/9/7 10:52
 */
@Service
@Slf4j
public class SsoServiceImpl implements SsoService {

    @Resource
    private TranceNoTool tranceNoTool;

    @Resource
    private SsoProperties ssoProperties;

    @Resource
    private SsoTmcLecturerInfoMapper ssoTmcLecturerInfoMapper;

    @Resource
    private SsoTmcPermissionMapper ssoTmcPermissionMapper;

    @Resource
    private LoginService loginService;

    @Override
    public Object ssoLogin() {
        try {
            log.info("SSO登录地址开始");
            //HttpUtil getHttpUtil = HttpUtil.getIstance(ssoProperties.getSsoAppId(), ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config", ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config");
            //String xml = getHttpUtil.getUserCenterAddr(HttpUtil.PRO_LOGIN, ssoProperties.getSsoServerUrl() + "/uac-sso/pro_login");
            //log.info("SSO登录地址：" + xml);
            //return xml;
        } catch (Exception e) {
            log.error("登录服务调用异常", e);
        }
        return null;
    }

    @Override
    public String userCenterCheck(String getToken) throws Exception {
        //APP_CHECK_LOGIN
        String messageID = tranceNoTool.getTranceNoSecondUnique("", "", 4);
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String dataNow = dateFormat.format(now);
        int i = 0;
        //HttpUtilApp getHttpUti2 = HttpUtilApp.getIstance(ssoProperties.getSsoAppId(),
        //ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config",
        //ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config");
        log.info("\n<<<------------------------start APP_CHECK_LOGIN----------------------------------->>>\n");
        //各平台参数数据SvcCont数据
        JSONObject token = new JSONObject();
        token.put("token", getToken);
        JSONObject parameters = new JSONObject();
        parameters.put("parameters", token);
        //1.各平台报文头数据TcpCont数据
        JSONObject TcpCont2 = new JSONObject();
        TcpCont2.put("appID", ssoProperties.getSsoAppId());
        TcpCont2.put("messageID", messageID);//不能重复，需自动生成流水号,规则年月日时分秒+随机数，如：2018113014503402632822
        TcpCont2.put("reqTime", dataNow);//请求时间格式
        TcpCont2.put("serviceKey", "app_check_login");//服务编码
        //2.组装报文传输
        JSONObject param2 = new JSONObject();
        param2.put("tcpCont", TcpCont2);//报文头
        param2.put("svcCont", parameters);//报文体(加密后)
        log.info("<<<-APP_CHECK_LOGIN发送内容ReqParam->>> " + param2);
        //String getHttpUti3 = getHttpUti2.getUserCenterAddr(HttpUtilApp.APP_CHECK_LOGIN, ssoProperties.getSsoServerUrl() + "/uac-sso-app/app_check_login");
        //log.info(i + "请求对象：" + getHttpUti2 + ",请求url=:" + getHttpUti3);
        //String rs3 = HttpUtil.postJson(getHttpUti3, param2.toString());
        //log.info("===APP_CHECK_LOGIN=：" + rs3);
        //return rs3;
        //自己加的
        return null;
    }

    @Override
    public Object sdkObtainsService() throws Exception {
        //HttpUtil getHttpUtil = HttpUtil.getIstance(ssoProperties.getSsoAppId(), ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config", ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config");
        //return getHttpUtil;
        //自己加的
        return null;
    }

    @Override
    public Object ssoLoginStatusCheck() throws Exception {
        //HttpUtil getHttpUtil = HttpUtil.getIstance(ssoProperties.getSsoAppId(), ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config", ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config");
        //String getUrl1 = getHttpUtil.getUserCenterAddr(HttpUtil.CHECK_LOGIN, ssoProperties.getSsoServerUrl() + "/uac-sso/check_login");
        //log.info("----动态获取用户中心调用地址----getUrl1=" + getUrl1);
        //return getUrl1;
        //自己加的
        return null;
    }

    @Override
    public String assertionsQuery() throws Exception {
        //HttpUtil getHttpUtil = HttpUtil.getIstance(ssoProperties.getSsoAppId(), ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config", ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config");
        //String getUrl1 = getHttpUtil.getUserCenterAddr(HttpUtil.CHECK_AUTHENTICATION, ssoProperties.getSsoServerUrl() + "/uac-sso/check_authentication");
        //log.info("----动态获取用户中心调用地址----getUrl1=" + getUrl1);
        //return getUrl1;
        //自己加的
        return null;
    }

    @Override
    public StaffInfo getEmployInfo(String userId) {
        log.info("begin to get employ info by userId = " + userId);
        return loginService.getLoginUser(userId);
    }

    @Override
    public Object heartBeat() throws Exception {
        //HttpUtil getHttpUtil = HttpUtil.getIstance(ssoProperties.getSsoAppId(), ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config", ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config");
        //String xml = getHttpUtil.getUserCenterAddr(HttpUtil.CHECK_AUTHENTICATION, ssoProperties.getSsoServerUrl() + "/uac-sso/pulse_keep");
        //return xml;
        //自己加的
        return null;
    }

//    @Override
//    public UserEntry checkAuthentication(String soap) throws Exception {
//        HttpUtil getHttpUtil = HttpUtil.getIstance(ssoProperties.getSsoAppId(), ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config", ssoProperties.getSsoServerUrl() + "/uac-sso-config/get_url_config");
//        String getUrl1 = getHttpUtil.getUserCenterAddr(HttpUtil.CHECK_AUTHENTICATION, ssoProperties.getSsoServerUrl() + "/uac-sso/check_authentication");
//        //精简包写法  推荐选此方法
//        String token = XmlHelper.GetToken_v1(soap);
//        log.info("token=" + token);
//        UserEntry userentry = null;
//        if (token != null && token.length() > 0) {
//            String urlxml = getUrl1 + "?token=" + token + "&appid=" + ssoProperties.getSsoAppId();
//            log.info("urlxml=" + urlxml);
//            Document doc = XmlHelper.XmlForSendRequest(urlxml); //获取XML返回信息
//            if (!Objects.isNull(doc)) {
//                log.info("doc=" + doc.asXML());
//                userentry = XmlHelper.get_userinfo(doc);//获取SAML中的用户信息
//            }
//        }
//        return userentry;
//    }
}
