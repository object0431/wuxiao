package com.asiainfo.mcp.tmc.sso.util;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.DateUtils;
import com.asiainfo.mcp.tmc.entity.MiniUserEntity;
import com.asiainfo.mcp.tmc.sso.authorize.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Objects;

/**
 * @author wzl
 * @Description:
 * @date 2022/9/16 20:13
 */
@Slf4j
public class StaffInfoUtil {

    @Resource
    @Qualifier("fastRedisTemplate")
    private static RedisTemplate redisTemplate;

    public static StaffInfo getStaff() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication.getPrincipal())) {
            throw new BusinessException("9999", "获取登录信息失败");
        }
        log.info("StaffInfoUtil {}", authentication.getPrincipal());
        StaffInfo entity = (StaffInfo) authentication.getPrincipal();
        return entity;
    }

//    public static LoginStaffInfo getLoginStaff() {
//        // 获取当前的用户
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (Objects.isNull(authentication.getPrincipal())) {
//            throw new BusinessException("9999", "获取登录信息失败");
//        }
//        StaffInfo staffInfo = (StaffInfo) authentication.getPrincipal();
//        LoginStaffInfo loginStaffInfo = new LoginStaffInfo();
//        BeanUtils.copyProperties(staffInfo, loginStaffInfo);
//        return loginStaffInfo;
//    }

    public static StaffInfo getStaffByRedis(HttpServletRequest request) {
        //获取token
        String token = "";
        if (request.getCookies() == null) {
            throw new BusinessException("9999","Cookie 不存在");
        }
        for (Cookie cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) {
                token = cookie.getValue();
            }
        }
        log.info("staffInfo:token{}",token);
        if (!StringUtils.isEmpty(token)) {
            //解析token
            String userid = "";
            try {
                Claims claims = JwtUtil.parseJWT(token);
                userid = claims.getSubject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //从redis中获取用户信息
            String redisKey = "USER:" + userid;
            JSONObject data = (JSONObject) redisTemplate.opsForValue().get(redisKey);
            StaffInfo loginUser = JSONObject.toJavaObject(data, StaffInfo.class);
            if (Objects.isNull(loginUser)) {
                throw new BusinessException("9999", "用户信息不存在！");
            }
            //存入SecurityContextHolder
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            return loginUser;
        }

        return new StaffInfo();
    }

    public static StaffInfo copyProperties(MiniUserEntity userInfo){
        StaffInfo staffInfo =  StaffInfo.builder().id(userInfo.getId()).mainUserId(userInfo.getAccountCode()).hrEmpCode(userInfo.getStaffId())
                .empName(userInfo.getStaffName()).identityNumber(userInfo.getPsptId()).companyId(userInfo.getCompanyId())
                .companyName(userInfo.getCompanyName()).deptId(userInfo.getDeptId()).deptName(userInfo.getDeptName())
                .mobileTel(userInfo.getSerialNumber()).sex(userInfo.getSex()).talentLevel(userInfo.getTalentLevel())
                .talentLevelName(userInfo.getTalentLevelName()).emailAddress(userInfo.getEmail())
                .postType(userInfo.getPostType()).birthday(DateUtils.getBirthdayFromIdCard(userInfo.getPsptId()))
                .mdmDeptCode(userInfo.getDeptId()).build();

        if(StringUtils.isEmpty(userInfo.getPostType())){
            staffInfo.setIdentity(userInfo.getDeptName());
        }else{
            staffInfo.setIdentity(userInfo.getDeptName().concat("(").concat(userInfo.getPostType()).concat(")"));
        }

        return staffInfo;
    }
}
