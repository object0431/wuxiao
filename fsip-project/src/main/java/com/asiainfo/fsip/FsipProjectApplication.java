package com.asiainfo.fsip;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"com.asiainfo", "com.chinaunicom.usercenter.sso"}, exclude = MybatisAutoConfiguration.class)
@EnableFeignClients
@EnableMPP
@EnableDiscoveryClient
@EnableAsync
@EnableSwagger2
public class FsipProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(FsipProjectApplication.class, args);
    }
}
