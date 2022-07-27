package com.ljt.study.dingtalk.config;

import com.caes.tickettrack.dingtalk.properties.DingTalkProperties;
import com.caes.tickettrack.dingtalk.service.DingTalkService;
import com.caes.tickettrack.dingtalk.service.impl.DingTalkWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LiJingTang
 * @date 2022-07-22 9:23
 */
@Configuration
public class MainConfig implements ApplicationRunner {

    @Autowired
    private DingTalkService dingTalkService;

    @Bean
    public DingTalkWebClient dingTalkWebClient(DingTalkProperties properties) {
        return new DingTalkWebClient(properties);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        dingTalkService.refreshUser();
    }

}
