package com.asiainfo.mcp.tmc;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"com.asiainfo.mcp.tmc"})
@EnableMPP
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableAsync
@EnableSwagger2
public class FsipRestApplication {
    public static void main(String[] args) {
        SpringApplication.run(FsipRestApplication.class, args);
    }
}
