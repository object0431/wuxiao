package com.asiainfo.mcp.tmc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.mcp.tmc.common.ability.AbilityClient;
import com.asiainfo.mcp.tmc.common.consts.IConstants;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingAddEntity;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingUpEntity;
import com.asiainfo.mcp.tmc.common.util.RspHelp;
import com.asiainfo.mcp.tmc.entity.PortalPending;
import com.asiainfo.mcp.tmc.mapper.PortalPendingMapper;
import com.asiainfo.mcp.tmc.service.TaskServive;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TaskServiveImpl implements TaskServive {

    @Value("${eip.appId}")
    private String appId;

    @Value("${eip.authToken}")
    private String authToken;

    @Value("${eip.url.addPending}")
    private String addPendingUrl;

    @Value("${eip.url.updatePending}")
    private String updatePendingUrl;

    @Resource
    private PortalPendingMapper portalPendingMapper;

    @Override
    public BaseRsp<Void> addPendingTask(PendingEntity[] data) {
        try{
            log.info(JSONObject.toJSONString(data));
            List<PendingAddEntity> addEntities = new ArrayList<>();
            for(PendingEntity pendingEntity: data){
                pendingEntity.setPendingNote(appId);
                pendingEntity.setPendingCityCode(IConstants.PENDING_CITY_CODE);
                pendingEntity.setPendingStatus(IConstants.PendingState.DB);
                savePortalPending(pendingEntity);

                PendingAddEntity addEntity = PendingAddEntity.builder().build();
                BeanUtil.copyProperties(pendingEntity, addEntity);
                addEntities.add(addEntity);
            }

            String retValue = callEipService(addPendingUrl, addEntities);
            return handleRetValue(data[0].getPendingCode(), retValue);
        }catch (Exception e){
            return RspHelp.fail(RspHelp.CALL_INTF_ERROR_CODE, e.getMessage());
        }
    }

    private void savePortalPending(PendingEntity pendingEntity) {
        try {
            PortalPending portalPending = PortalPending.builder().operatorType(IConstants.OperatorType.XZDB).build();
            BeanUtil.copyProperties(pendingEntity, portalPending);
            portalPendingMapper.insert(portalPending);
        } catch (Exception e) {
            log.error("Could not execute savePortalPending, pendingEntity = " + JSONObject.toJSONString(pendingEntity), e);
        }
    }

    @Override
    public BaseRsp<Void> updatePendingStatus(PendingUpEntity[] data) {
        try{
            for(PendingUpEntity pendingUpEntity: data){
                pendingUpEntity.setPendingNote(appId);
                upPortalPending(pendingUpEntity);
            }

            String retValue = callEipService(updatePendingUrl, data);
            return handleRetValue(data[0].getPendingCode(), retValue);
        }catch (Exception e){
            return RspHelp.fail(RspHelp.CALL_INTF_ERROR_CODE, e.getMessage());
        }
    }

    private void upPortalPending(PendingUpEntity pendingEntity) {
        try {
            String operateType = IConstants.PendingState.SC.equals(pendingEntity.getPendingStatus())
                    ? IConstants.OperatorType.SCDB : IConstants.OperatorType.GXDB;
            PortalPending portalPending = PortalPending.builder().pendingCityCode(IConstants.PENDING_CITY_CODE).operatorType(operateType).build();
            BeanUtil.copyProperties(pendingEntity, portalPending);
            portalPendingMapper.updateById(portalPending);
        } catch (Exception e) {
            log.error("Could not execute upPortalPending, pendingEntity = " + JSONObject.toJSONString(pendingEntity), e);
        }
    }

    /***
     * post发送Json形式参数
     * @param url 请求地址
     * @param data 请求参数
     * @return
     */
    private String callEipService(String url, Object data) {
        long startTime = System.currentTimeMillis();
        StringBuilder urlBuilder = new StringBuilder(url).append("/").append(appId).append("/").append(authToken);
        String dataJson = JSONObject.toJSONString(data);
        url = urlBuilder.toString();
        try{
            String response = AbilityClient.callService(true, urlBuilder.toString(), dataJson);
            log.info("{} ^v^ {} ^v^ {} ^v^ {}", url, dataJson, response, (System.currentTimeMillis() - startTime) + "ms");

            return response;
        }catch (Exception e) {
            log.info("{} ^v^{} ^v^ {} ^v^ {}", url, dataJson, e, (System.currentTimeMillis() - startTime) + "ms");
            throw e;
        }
    }

    private BaseRsp<Void> handleRetValue(String pendingCode, String retValue){
        if(StringUtils.isBlank(retValue)){
            return RspHelp.fail(RspHelp.CALL_INTF_ERROR_CODE, RspHelp.CALL_INTF_ERROR_DESC);
        }

        JSONObject retJson = JSONObject.parseObject(retValue);
        String retDesc = retJson.getString(pendingCode);

        if("100-操作成功".equals(retDesc)){
            return RspHelp.success(null);
        }

        return RspHelp.fail(RspHelp.CALL_INTF_ERROR_CODE, retDesc);
    }
}
