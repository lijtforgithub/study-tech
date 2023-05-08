package com.ljt.study.drools.config;

import lombok.extern.slf4j.Slf4j;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author LiJingTang
 * @date 2023-05-08 14:45
 */
@Slf4j
@Component
public class DynamicRuleManage {

    @Autowired
    private KieServices kieServices;

    public KieContainer loadRule(String key, String content) {
        KieFileSystem kfs = kieServices.newKieFileSystem();
        KieRepository repository = kieServices.getRepository();
        repository.addKieModule(repository::getDefaultReleaseId);
//        kfs.delete(path);
        kfs.write("src/main/resources/rules/" + key, content);
        Results results = kieServices.newKieBuilder(kfs).buildAll().getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            results.getMessages(Message.Level.ERROR).forEach(message -> log.error("规则文件语法异常：{}", message));
            throw new RuntimeException("规则文件语法异常");
        }
        return kieServices.newKieContainer(repository.getDefaultReleaseId());
    }

    public KieBase loadRule(String drools) {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add(ResourceFactory.newByteArrayResource(drools.getBytes(StandardCharsets.UTF_8)), ResourceType.DRL);
        if (builder.hasErrors()) {
            String error = builder.getErrors().toString();
            log.error("规则文件语法异常: {}", error);
            throw new RuntimeException("规则文件语法异常");
        }

        InternalKnowledgeBase base = KnowledgeBaseFactory.newKnowledgeBase();
        base.addPackages(builder.getKnowledgePackages());
        return base;
    }

}
