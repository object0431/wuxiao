package com.asiainfo.mcp.tmc.sso.authorize;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.mcp.tmc.common.entity.GetUserInfoRspModel;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.service.EmployeeService;
import com.asiainfo.mcp.tmc.sso.service.LoggerService;
import com.asiainfo.mcp.tmc.sso.service.LoginService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author wzl
 * @Description:
 * @date 2022/9/16 10:31
 */
@Component
@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private SsoProperties ssoProperties;

    @Resource
    private LoginService loginService;

    @Resource
    private LoggerService loggerService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String igoreUrl = ssoProperties.getIgoreUrl();
        if (StringUtils.isEmpty(igoreUrl)) {
            return request.getRequestURL().toString().contains("/userCenter/");
        }

        String[] urlArr = igoreUrl.split(",");

        for (String url : urlArr) {
            if (request.getRequestURL().toString().contains(url)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            //获取token
            String userid;
            StaffInfo loginUser = new StaffInfo();

            String deviceType = "PC";

            String dingToken = request.getParameter("dingToken");
            if (!StringUtils.isEmpty(dingToken)) {
                loginUser = loginService.getDingLoginUser(dingToken);
                deviceType = "DD";
            } else {
                Cookie[] cookies = request.getCookies();
//                if (cookies == null) {
//                    throw new BusinessException("0909");
//                }
//
//                // 获取token
//                Optional<Cookie> tokenOp = Arrays.stream(cookies).filter(val -> "token".equals(val.getName())).findFirst();
//                Optional<Cookie> mobileOp = Arrays.stream(cookies).filter(val -> "accesstoken".equals(val.getName())).findFirst();

//                if (!tokenOp.isPresent() && !mobileOp.isPresent()) {
//                    log.info("could not get token, tokenOp = {}, mobileOp = {}, dingToken = {}", tokenOp.isPresent(), mobileOp.isPresent(), dingToken);
//                    //跳转登录
//                    throw new BusinessException("0909", ssoLogin());
//                }
//                if (tokenOp.isPresent()) {
//                    try {
//                        // 解析token
//                        Claims claims = JwtUtil.parseJWT(tokenOp.get().getValue());
//                        userid = claims.getSubject();
//                    } catch (Exception e) {
//                        log.error("Could not get userid by token  = " + tokenOp.get().getValue(), e);
//                        throw new BusinessException("0909", ssoLogin());
//                    }
//                    // 从redis中获取用户信息
//                    String redisKey = "USER:" + userid;
//                    String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
//                    log.debug("redisValue = " + redisValue + ", redisKey = " + redisKey);
//                    if (StringUtils.isEmpty(redisKey)) {
//                        throw new BusinessException("0909", ssoLogin());
//                    }
//                    loginUser = JSONObject.parseObject(redisValue, StaffInfo.class);
//                } else if (mobileOp.isPresent()) {
//                    loginUser = getMobileLoginUser(mobileOp.get().getValue());
//                }
            }

//            if (loginUser == null) {
//                throw new BusinessException("0909", ssoLogin());
//            }
//
//            loggerService.logLogin(loginUser, deviceType);
            //自己加的
            loginUser.setHrEmpCode("1");


            loginUser.setEmpName("系统管理员");
//            loginUser.setMainUserId("admin");
            loginUser.setMainUserId("yangs229");
            loginUser.setOrgName("中国联通湖南省分公司");
            loginUser.setSex("M");
            loginUser.setMobileTel("1234567989");
            loginUser.setDeptId("20035194");
            loginUser.setDeptName("生产支撑中心");
            loginUser.setCompanyId("0043");
            loginUser.setCompanyName("省级本部");
            loginUser.setEmailAddress("admin@chinaunicom.cn");

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        }catch (Exception e){
            request.setAttribute("ERROR_MSG", e.getMessage());
            throw e;
        }
    }

    private StaffInfo getMobileLoginUser(String mobileToken) {
        String userId = stringRedisTemplate.opsForValue().get(mobileToken);
        if (StringUtils.isEmpty(userId)) {
            GetUserInfoRspModel getUserInfoRspModel = employeeService.getUserInfo(mobileToken);
            if (getUserInfoRspModel == null || getUserInfoRspModel.getRet() > 0) {
                throw new BusinessException("0909", this.ssoLogin());
            }

            userId = getUserInfoRspModel.getUserid();

            if (!StringUtils.isEmpty(userId)) {
                stringRedisTemplate.opsForValue().set(mobileToken, userId, 60 * 30, TimeUnit.SECONDS);
            }
        }

        return loginService.getLoginUser(userId);
    }

    /**
     * 获取登录地址
     *
     * @return
     */
    private String ssoLogin() {
        return "用户信息校验失败！";
    }

}
