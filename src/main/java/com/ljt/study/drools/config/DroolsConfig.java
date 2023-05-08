package com.ljt.study.drools.config;

import org.kie.api.KieServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LiJingTang
 * @date 2023-05-06 17:30
 */
@Configuration
class DroolsConfig {

    private static final String RULES_PATH = "drools/";

    @Bean
    KieServices getKieServices() {
        return KieServices.Factory.get();
    }

//    @Bean
//    KieFileSystem kieFileSystem() throws IOException {
//        KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
//        for (Resource file : getRuleFiles()) {
//            kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH + file.getFilename(), "UTF-8"));
//        }
//        return kieFileSystem;
//    }
//
//    Resource[] getRuleFiles() throws IOException {
//        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
//        return resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "**/*.*");
//    }
//
//    @Bean
//    KieContainer kieContainer() throws IOException {
//        KieRepository kieRepository = getKieServices().getRepository();
//        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);
//
//        KieBuilder kieBuilder = getKieServices().newKieBuilder(kieFileSystem());
//        kieBuilder.buildAll();
//
//        return getKieServices().newKieContainer(kieRepository.getDefaultReleaseId());
//    }
//
//    @Bean
//    KieBase kieBase() throws IOException {
//        return kieContainer().getKieBase();
//    }

}
