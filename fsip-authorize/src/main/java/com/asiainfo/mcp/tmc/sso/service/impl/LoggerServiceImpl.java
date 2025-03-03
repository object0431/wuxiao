package com.asiainfo.mcp.tmc.sso.service.impl;

import com.asiainfo.fsip.mapper.fsip.FsipLoginLogMapper;
import com.asiainfo.fsip.mapper.fsip.FsipMenuLogMapper;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.util.DateUtils;
import com.asiainfo.mcp.tmc.common.util.TranceNoTool;
import com.asiainfo.mcp.tmc.sso.entity.FsipLoginLogEntity;
import com.asiainfo.mcp.tmc.sso.entity.FsipMenuLogEntity;
import com.asiainfo.mcp.tmc.sso.service.LoggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoggerServiceImpl implements LoggerService {

    @Resource
    private RedisTemplate<String, String> stringRedisTemplate;

    @Resource
    private FsipLoginLogMapper fsipLoginLogMapper;

    @Resource
    private FsipMenuLogMapper fsipMenuLogMapper;

    @Resource
    private TranceNoTool tranceNoTool;

    @Override
    public void logLogin(StaffInfo staffInfo, String deviceType) {
        String today = DateUtils.getShortDate();

        String redisKey = "LOGIN_" + today + "_" + staffInfo.getMainUserId();
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        if(!StringUtils.isEmpty(redisValue)){
            return;
        }

        FsipLoginLogEntity loginLogEntity = FsipLoginLogEntity.builder().loginAccount(staffInfo.getMainUserId())
                .deptId(staffInfo.getDeptId()).companyId(staffInfo.getCompanyId()).deviceType(deviceType)
                .staffName(staffInfo.getEmpName()).loginTime(new Date()).build();

        fsipLoginLogMapper.insert(loginLogEntity);
        stringRedisTemplate.opsForValue().set(redisKey, "1", 20, TimeUnit.HOURS);
    }

    @Async
    @Override
    public void recordMenuLog(String meneId, String deviceType, StaffInfo staffInfo) {
        String id = tranceNoTool.getTranceNoSecondUnique("", "yyMMddHHmmssSSS", 3);

        FsipMenuLogEntity menuLogEntity = FsipMenuLogEntity.builder().id(id).deviceType(deviceType).menuId(meneId)
                .operatorId(staffInfo.getMainUserId()).operatorName(staffInfo.getEmpName())
                .deptId(staffInfo.getDeptId()).companyId(staffInfo.getCompanyId())
                .operateTime(new Date()).build();

        fsipMenuLogMapper.insert(menuLogEntity);
    }
}
