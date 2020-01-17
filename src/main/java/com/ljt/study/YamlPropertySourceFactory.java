package com.ljt.study;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author LiJingTang
 * @date 2020-01-16 14:10
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String s, EncodedResource encodedResource) throws IOException {
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
                .load(encodedResource.getResource().getFilename(), encodedResource.getResource());
        return sources.get(0);
    }

}
