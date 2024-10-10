package com.ljt.study.tools.poitl;

import com.deepoove.poi.XWPFTemplate;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author LiJingTang
 * @date 2024-08-22 11:17
 */
public class PoiTlTest {

    @Test
    void testRender() throws IOException {
        ClassPathResource resource = new ClassPathResource("/tools/poitl/模版文件.docx");
        XWPFDocument doc = new XWPFDocument(resource.getInputStream());

        XWPFTemplate template = XWPFTemplate.compile(doc).render(
                new HashMap<String, Object>(){{
                    put("title", "Hi, poi-tl Word模板引擎");
                }});
        template.writeAndClose(new FileOutputStream("/Users/lijingtang/Downloads/模板引擎.docx"));

    }

}
