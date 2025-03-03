package com.asiainfo.fsip.client.hystrix;

import com.asiainfo.fsip.client.TmcRestClient;
import com.asiainfo.mcp.tmc.common.entity.TitleInfo;
import com.asiainfo.mcp.tmc.common.entity.base.BaseRsp;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingEntity;
import com.asiainfo.mcp.tmc.common.entity.pending.PendingUpEntity;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * 默认值处理
 * <p>
 * CacheClient的fallbackFactory类，该类需实现FallbackFactory接口，并覆写create方法
 * The fallback factory must produce instances of fallback classes that
 * implement the interface annotated by {@link FeignClient}.
 */
@Component
@Slf4j
public class TmcRestClientFallbackFactory implements FallbackFactory<TmcRestClient> {

    @Override
    public TmcRestClient create(Throwable cause) {
        log.error("cache-service over load or timeout:{}", cause.getMessage());

        return new TmcRestClient() {

            @Override
            public BaseRsp<Void> addPending(PendingEntity[] data) {
                return null;
            }

            @Override
            public BaseRsp<Void> updatePendingStatus(PendingUpEntity[] data) {
                return null;
            }

            @Override
            public String getTitle(String type, TitleInfo titleInfo) {
                return null;
            }
        };
    }
}