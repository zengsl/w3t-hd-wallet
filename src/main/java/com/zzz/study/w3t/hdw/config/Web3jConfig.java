package com.zzz.study.w3t.hdw.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {

    @Value("${MY_ENV.INFURA_URL}")
    private String infuraUrl;

    /**
     * 将 Web3j 对象注册为 Spring Bean
     * 这里连接的是本地 Ganache 的默认端口 7545
     */
    @Bean
    public Web3j web3j() {
        // 如果你用的是 Infura 或其他节点，把下面的 URL 换掉即可
        return Web3j.build(new HttpService(infuraUrl));
    }
}