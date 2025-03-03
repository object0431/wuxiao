package com.asiainfo.mcp.tmc.sso.controller;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.mcp.tmc.common.base.annotation.RspResult;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.sso.authorize.JwtUtil;
import com.asiainfo.mcp.tmc.sso.service.LoginService;
import com.asiainfo.mcp.tmc.sso.service.SsoService;
//import com.chinaunicom.usercenter.sso.util.UserEntry;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/userCenter")
@Slf4j
@RefreshScope
public class SsoController {

    @Resource
    private SsoService ssoService;

    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    private LoginService loginService;
    /**
     * 跳转成功页面的地址
     */
    @Value(value = "${sso.login.success}")
    private String toSuccessPageUrl;

    /**
     * 跳转成功页面的地址
     */
    @Value(value = "${sso.login.error}")
    private String toErrorPageUrl;

    /**
     * 登录校验
     *
     * @param getToken
     * @return
     * @throws Exception
     */
    @GetMapping("/userCenterCheck")
    public Object userCenterCheck(@RequestParam("getToken") String getToken) throws Exception {
        return ssoService.userCenterCheck(getToken);
    }

    /**
     * SDK获取服务列表对接
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/sdkObtainsService")
    public Object sdkObtainsService() throws Exception {
        return ssoService.sdkObtainsService();
    }

    /**
     * 登录
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/ssoLogin")
    public Object ssoLogin() {
        return ssoService.ssoLogin();
    }

    /**
     * 登录状态检查
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/ssoLoginStatusCheck")
    public Object ssoLoginStatusCheck() throws Exception {
        return ssoService.ssoLoginStatusCheck();
    }

    @GetMapping("/assertionsQuery")
    public Object assertionsQuery() throws Exception {
        return ssoService.assertionsQuery();
    }

    /**
     * 登录状态检查成功接口
     *
     * @param request
     * @param response
     * @throws Exception
     */
//    @RequestMapping("/loginCheckSuccess")
//    public void checkAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String soap = request.getParameter("soap");
//        log.info("【被SSO调用】loginCheckSuccess 接收参数soap:{}", soap);
//        UserEntry user = ssoService.checkAuthentication(soap);
//        StaffInfo loginUser = ssoService.getEmployInfo(user.getUserid());
//        if (Objects.isNull(loginUser)) {
//            sendToLoginError(response);
//            return;
//        }
//        String forwordUrl = request.getParameter("url");
//
//        //生成cookie
//        String jwt = JwtUtil.createJWT(user.getUserid());
//        StringBuilder stringBuilder = new StringBuilder(toSuccessPageUrl).append("?token=").append(jwt);
//        if (!StringUtils.isEmpty(forwordUrl)) {
//            stringBuilder.append("&forwordUrl=").append(encodeUrl(forwordUrl));
//        }
//        log.debug("loginCheckSuccess token: {}", jwt);
//
//        String data = JSONObject.toJSONString(loginUser);
//
//        log.info("data = {}", data);
//        //放入缓存
//        stringRedisTemplate.opsForValue().set("USER:" + user.getUserid(), data, 60 * 60 * 24, TimeUnit.SECONDS);
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html");
//        log.info("loginCheckSuccess stringBuilder {}", stringBuilder);
//        response.getWriter().print("<script>window.location.href='" + stringBuilder + "';</script>");
//        log.info("loginCheckSuccess end");
//    }

    /**
     * 登录状态检查成功接口
     *
     * @param request
     * @param response
     * @throws Exception
     */
//    @RequestMapping("/success")
//    public void checkAuthentication(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String soap = request.getParameter("soap");
//        log.info("【被SSO调用】success 接收参数soap:{}", soap);
//        UserEntry user = ssoService.checkAuthentication(soap);
//        StaffInfo loginUser = ssoService.getEmployInfo(user.getUserid());
//        if (Objects.isNull(loginUser)) {
//            throw new BusinessException("0909", (String) this.ssoLogin());
//        }
//
//        String forwordUrl = request.getParameter("url");
//
//        //生成cookie
//        String jwt = JwtUtil.createJWT(user.getUserid());
//        StringBuilder stringBuilder = new StringBuilder(toSuccessPageUrl).append("?token=").append(jwt);
//        if (!StringUtils.isEmpty(forwordUrl)) {
//            stringBuilder.append("&forwordUrl=").append(encodeUrl(forwordUrl));
//        }
//        log.debug("success token: {}", jwt);
//
//        String data = JSONObject.toJSONString(loginUser);
//
//        log.info("data = {}", data);
//
//        //放入缓存
//        stringRedisTemplate.opsForValue().set("USER:" + user.getUserid(), data, 60 * 60 * 24, TimeUnit.SECONDS);
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/html");
//        log.info("success stringBuilder {}", stringBuilder);
//        response.getWriter().print("<script>window.location.href='" + stringBuilder + "';</script>");
//        log.info("success end");
//    }

    /**
     * 登录失败接口
     */
    @GetMapping("/error")
    public void loginError() {
        throw new BusinessException("9999", "登录异常");
    }

    /**
     * 登录校验失败接口
     */
    @GetMapping("/loginCheckError")
    public void loginCheckError(@RequestParam("errorCode") String errorCode, HttpServletResponse response) throws Exception {
        log.info("【被SSO调用: loginCheckError】errorCode:{}", errorCode);
        sendToLoginError(response);
    }

    private void sendToLoginError(HttpServletResponse response) throws Exception{
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(toErrorPageUrl).append("?");
        stringBuilder.append((String) ssoLogin());
        response.getWriter().print("<script>window.location.href='" + stringBuilder + "';</script>");
    }

    /**
     * 心跳
     */
    @GetMapping("/heartBeat")
    public Object heartBeat() throws Exception {
        return ssoService.heartBeat();
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/getStaffInfo")
    @RspResult
    public StaffInfo getStaffInfo(HttpServletRequest request) {
        String dingToken = request.getParameter("dingToken");
        if (!StringUtils.isEmpty(dingToken)) {
            return loginService.getDingLoginUser(dingToken);
        }

        //获取token
        String token = "";
        if (request.getCookies() == null) {
            throw new BusinessException("9999", "Cookie 不存在");
        }
        for (Cookie cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }

        if (!StringUtils.isEmpty(token)) {
            //解析token
            String userid = "";
            try {
                Claims claims = JwtUtil.parseJWT(token);
                userid = claims.getSubject();
            } catch (Exception e) {
                log.error("Could not get userid from token", e);
            }

            StaffInfo staffInfo = loginService.getLoginUser(userid);
            //存入SecurityContextHolder
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(staffInfo, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            return staffInfo;
        }

        return new StaffInfo();
    }

    /**
     * 获取测试token
     */
    @GetMapping("/getTestToken")
    @RspResult
    public String getTestToken() {
        StaffInfo loginUser = new StaffInfo();
        loginUser.setEmpName("系统管理员");
        loginUser.setMainUserId("admin");
        loginUser.setOrgName("中国联通湖南省分公司");
        loginUser.setSex("M");
        loginUser.setMobileTel("1234567989");
        loginUser.setDeptId("20035194");
        loginUser.setDeptName("生产支撑中心");
        loginUser.setCompanyId("0043");
        loginUser.setCompanyName("省级本部");
        loginUser.setEmailAddress("admin@chinaunicom.cn");
        //存入SecurityContextHolder
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        String data = JSONObject.toJSONString(loginUser);
        stringRedisTemplate.opsForValue().set("USER:" + loginUser.getMainUserId(), data, 12, TimeUnit.HOURS);
        return JwtUtil.createJWT(loginUser.getMainUserId());

    }

    /**
     * 获取员工信息 （仅考试系统单点使用）
     *
     * @param token
     * @return
     */
    @GetMapping("/getEmployInfo")
    @RspResult
    public StaffInfo getEmployInfo(String token) {
        Claims claims;
        try {
            claims = JwtUtil.parseJWT(token);
            String userid = claims.getSubject();
            return ssoService.getEmployInfo(userid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取员工信息 （仅考试系统单点使用）
     *
     * @param userId
     * @return
     */
    @GetMapping("/reloadEmployee")
    @RspResult
    public StaffInfo reloadEmployee(String userId) {
        try {
            String redisKey = "USER:" + userId;
            stringRedisTemplate.delete(redisKey);

            return ssoService.getEmployInfo(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String encodeUrl(String url){
        String orgUrl = URLDecoder.decode(url, CharsetUtil.CHARSET_UTF_8);
        return URLEncodeUtil.encodeAll(orgUrl);
    }
}
