package com.asiainfo.mcp.tmc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.entity.StaffInfo;
import com.asiainfo.mcp.tmc.common.exception.BusinessException;
import com.asiainfo.mcp.tmc.common.util.CacheRedisUtil;
import com.asiainfo.mcp.tmc.common.util.DateUtils;
import com.asiainfo.mcp.tmc.common.util.SequenceUtils;
import com.asiainfo.mcp.tmc.service.TitleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class TitleServiceImpl implements TitleService {

    @Resource
    private CacheRedisUtil cacheRedisUtil;

    @Override
    public String generateTitle(String type, StaffInfo staffInfo, String... keyArr) {
        log.info("type = " + type + ", keyArr = " + JSONObject.toJSONString(keyArr));
        if (StringUtils.isBlank(type) || staffInfo == null) {
            throw new BusinessException("无效的请求参数");
        }
        if (IConstants.TaskType.GRRZSQ.equals(type)) {
            return getGrrzsqTitle(staffInfo, keyArr);
        }

        if (IConstants.TaskType.GRRZJS.equals(type) || IConstants.TaskType.RZLSSJLR.equals(type)) {
            return getGrrzjsTitle(staffInfo, keyArr);
        }

        if (IConstants.TaskType.GRPXSQ.equals(type)) {
            return getGrpxsqTitle(staffInfo, keyArr);
        }

        if (IConstants.TaskType.GRPXJSSQ.equals(type)) {
            return getGrpxjsTitle(staffInfo, keyArr);
        }

        if (IConstants.TaskType.JSSQ.equals(type)) {
            return getPxxmJsTitle(staffInfo);
        }

        if (IConstants.TaskType.GRQJSQ.equals(type)) {
            return getGrqjsqTitle(staffInfo, keyArr);
        }

        if (IConstants.TaskType.RCNS.equals(type)) {
            return getTalentReviewApprovalTitle(staffInfo, keyArr);
        }

        if (IConstants.TaskType.RCLX.equals(type)) {
            return getTalentSelectionApprovalTitle(staffInfo, keyArr);
        }

        if (IConstants.TaskType.RCNSCS.equals(type)) {
            return getFirstExamApprovalTitle(keyArr);
        }

        throw new BusinessException("不支持的任务类型=" + type);
    }

    /**
     * 个人认证申请
     */
    private String getGrrzsqTitle(StaffInfo staffInfo, String[] keyArr) {
        StringBuilder builder = new StringBuilder();
        for (String key : keyArr) {
            builder.append(key).append("-");
        }

        builder.append(buildCommonValue(IConstants.TaskType.GRRZSQ, staffInfo));

        return builder.toString();
    }

    /**
     * 个人认证结算
     */
    private String getGrrzjsTitle(StaffInfo staffInfo, String[] keyArr) {
        StringBuilder builder = new StringBuilder();
        for (String key : keyArr) {
            builder.append(key).append("-");
        }

        builder.append(buildCommonValue(IConstants.TaskType.GRRZJS, staffInfo));

        return builder.toString();
    }

    /**
     * 个人培训申请
     */
    private String getGrpxsqTitle(StaffInfo staffInfo, String[] keyArr) {
        StringBuilder builder = new StringBuilder();
        for (String key : keyArr) {
            builder.append(key).append("-");
        }

        builder.append(staffInfo.getCompanyName()).append("-").append(buildCommonValue(IConstants.TaskType.GRRZSQ, staffInfo));

        return builder.toString();
    }

    /**
     * 个人培训结算
     */
    private String getGrpxjsTitle(StaffInfo staffInfo, String[] keyArr) {
        StringBuilder builder = new StringBuilder();
        for (String key : keyArr) {
            builder.append(key).append("-");
        }

        builder.append(staffInfo.getCompanyName()).append("-").append(buildCommonValue(IConstants.TaskType.GRRZJS, staffInfo));

        return builder.toString();
    }

    /**
     * 个人请假申请
     */
    private String getGrqjsqTitle(StaffInfo staffInfo, String[] keyArr) {
        StringBuilder builder = new StringBuilder();
        for (String key : keyArr) {
            builder.append(key).append("-");
        }

        builder.append(staffInfo.getCompanyName()).append("-").append(buildCommonValue(IConstants.TaskType.GRQJSQ, staffInfo));

        return builder.toString();
    }

    /**
     * 培训项目结算
     */
    private String getPxxmJsTitle(StaffInfo staffInfo) {
        StringBuilder builder = new StringBuilder(staffInfo.getCompanyName()).append("-").append(staffInfo.getEmpName())
                .append("-培训结算单-").append(getSequence(IConstants.TaskType.JSSQ));

        return builder.toString();
    }

    /**
     * 人才年审
     */
    private String getTalentReviewApprovalTitle(StaffInfo staffInfo, String[] keyArr) {
        StringBuilder builder = new StringBuilder();
        for (String key : keyArr) {
            builder.append(key).append("-");
        }

        builder.append(buildCommonValue(IConstants.TaskType.RCNS, staffInfo));

        return builder.toString();
    }

    /**
     * 人才年审初审
     */
    private String getFirstExamApprovalTitle(String[] keyArr) {
        StringBuilder builder = new StringBuilder();
        for (String key : keyArr) {
            builder.append(key).append("-");
        }

        builder.append(getSequence(IConstants.TaskType.RCNSCS));

        return builder.toString();
    }

    /**
     * 人才遴选
     */
    private String getTalentSelectionApprovalTitle(StaffInfo staffInfo, String[] keyArr) {
        StringBuilder builder = new StringBuilder();
        for (String key : keyArr) {
            builder.append(key).append("-");
        }

        builder.append(buildCommonValue(IConstants.TaskType.RCLX, staffInfo));

        return builder.toString();
    }

    private String buildCommonValue(String type, StaffInfo staffInfo) {
        StringBuilder builder = new StringBuilder(staffInfo.getEmpName()).append("-").append(getSequence(type));

        return builder.toString();
    }

    private String getSequence(String type) {
        String today = DateUtils.getShortDate();
        String key = type + today;
        long sequence = cacheRedisUtil.incr(key);
        cacheRedisUtil.expireHours(key, 24);

        StringBuilder builder = new StringBuilder(today).append(SequenceUtils.lpad(sequence, 2));

        return builder.toString();
    }
}
