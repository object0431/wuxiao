package com.asiainfo.fsip.client;


import com.asiainfo.fsip.client.hystrix.TmcRestClientFallbackFactory;
import com.asiainfo.mcp.tmc.common.entity.TitleInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingUpEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调用fsip-rest的客户端
 */
@FeignClient(name = "fsip-rest", fallbackFactory = TmcRestClientFallbackFactory.class)
public interface TmcRestClient {

    /**
     * 待办新增
     *
     * @return
     */
    @RequestMapping(value = "/fsip-rest/task/addPending")
    BaseRsp<Void> addPending(@RequestBody PendingEntity[] data);

    /**
     * 待办更新
     *
     * @return
     */
    @RequestMapping(value = "/fsip-rest/task/updatePendingStatus")
    BaseRsp<Void> updatePendingStatus(@RequestBody PendingUpEntity[] data);

    /**
     * 获取待办标题
     *
     * @return
     */
    @RequestMapping(value = "/fsip-rest/inner/getTitle")
    String getTitle(@RequestParam String type, @RequestBody TitleInfo titleInfo);

}
