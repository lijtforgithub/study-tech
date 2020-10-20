package com.ljt.study.tools.easyexcel;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Map;

import static com.alibaba.excel.EasyExcelFactory.write;

/**
 * 导出
 *
 * @author LiJingTang
 * @date 2020-10-19 15:00
 */
@Slf4j
public class TemplateTest {

    private static final String DESKTOP = "C:/Users/Administrator/Desktop/";

    @Test
    public void testSimpleFill() throws IOException {
        ClassPathResource resource = new ClassPathResource("/tools/easyexcel/SimpleFill.xlsx");
        String templatePath = resource.getURI().getRawPath();
        String path = DESKTOP + resource.getFilename();
        String mapPath = DESKTOP + "Map-" + resource.getFilename();

        FillData fillData = new FillData();
        fillData.setName("李斯");
        fillData.setNumber(5.2);
        write(path).withTemplate(templatePath).sheet().doFill(fillData);

        Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
        map.put("name", "张三");
        map.put("number", 5.2);
        write(mapPath).withTemplate(templatePath).sheet().doFill(map);
    }

    @After
    public void after() {
        log.info("方法执行结束");
    }

    @Data
    public static class FillData {

        private String name;
        private double number;

    }

}
