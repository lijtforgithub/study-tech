package com.ljt.study;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author LiJingTang
 * @date 2020-01-03 20:47
 */
@Slf4j
public abstract class AbstractTest {

    private static final String SPRING_BEAN = "org.springframework";
    private static final String PCK_PREFIX = "com.ljt.study";

    protected ApplicationContext applicationContext;

    protected void printBeanDefinition() {
        Objects.requireNonNull(applicationContext, "IoC容器为空");

        log.info("打印IoC容器里自定义的BeanDefinitionName开始");

        Stream.of(applicationContext.getBeanDefinitionNames())
                .filter(name -> !name.startsWith(SPRING_BEAN))
                .forEach(System.out::println);

        log.info("打印IoC容器里自定义的BeanDefinitionName结束");
    }

    private static final String SUFFIX = ".xml";

    protected void setApplicationContext(String fileName) {
        String pckSuffix = this.getClass().getPackage().getName().substring(PCK_PREFIX.length());
        String configLocations = pckSuffix.replaceAll("\\.", "/") + "/" + fileName + SUFFIX;
        applicationContext = new ClassPathXmlApplicationContext(configLocations);

        log.info("【{}】IoC容器初始化完成", configLocations);
    }

}
