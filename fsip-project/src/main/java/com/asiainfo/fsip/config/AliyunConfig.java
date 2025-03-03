package com.asiainfo.fsip.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oss")
@Data
public class AliyunConfig {

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.accesskey}")
    private String accesskey;

    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.dir:}")
    private String dir;

    @Value("${oss.maxConnections:1024}")
    private int maxConnections;

    @Value("${oss.socketTimeout:50000}")
    private int socketTimeout;

    @Value("${oss.connectionTimeout:60000}")
    private int connectionTimeout;

    @Value("${oss.connectionRequestTimeout:60000}")
    private int connectionRequestTimeout;

    @Value("${oss.idleConnectionTime:60000}")
    private int idleConnectionTime;

    @Value("${oss.maxErrorRetry:3}")
    private int maxErrorRetry;

    @Bean("ossConfig")
    public ClientBuilderConfiguration builderConfiguration() {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();

        //设置OSSClient允许打开的最大连接数，默认为1024
        conf.setMaxConnections(maxConnections);
        //设置Socket传输数据的超时时间，默认为50000毫秒
        conf.setSocketTimeout(socketTimeout);
        //设置建立连接超时时间，默认为50000毫秒
        conf.setConnectionTimeout(connectionTimeout);
        //设置从连接池获取连接的超时时间，默认为不超时
        conf.setConnectionRequestTimeout(connectionRequestTimeout);
        //设置连接空闲超时时间，超时则关闭连接，默认为60000毫秒
        conf.setIdleConnectionTime(idleConnectionTime);
        //设置失败请求次数，默认为3次
        conf.setMaxErrorRetry(maxErrorRetry);
        //设置是否支持将自定义域名作为Endpoint，默认支持
        conf.setSupportCname(true);

        return conf;
    }


    @Bean("ossClient")
    public OSS buildOss(@Qualifier("ossConfig") ClientBuilderConfiguration conf) {
        return new OSSClientBuilder().build(endpoint, accesskey, accessKeySecret, conf);
    }
}
