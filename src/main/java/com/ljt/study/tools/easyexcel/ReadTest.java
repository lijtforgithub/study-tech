package com.ljt.study.tools.easyexcel;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.alibaba.excel.EasyExcelFactory.read;
import static com.alibaba.excel.EasyExcelFactory.readSheet;
import static com.ljt.study.Constant.DESKTOP;
import static com.ljt.study.Constant.XLSX;

/**
 * @author LiJingTang
 * @date 2020-10-22 09:09
 */
@Slf4j
public class ReadTest {

    @Test
    public void testSimpleRead() {
        String path = DESKTOP + "SimpleRead" + XLSX;
        // 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        read(path, DemoData.class, new DemoDataListener()).sheet().doRead();

        ExcelReader excelReader = null;
        try {
            excelReader = read(path, DemoData.class, new DemoDataListener()).build();
            ReadSheet readSheet = readSheet(0).build();
            excelReader.read(readSheet);
        } finally {
            if (Objects.nonNull(excelReader)) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
    }


    @Test
    public void testIndexOrNameRead() {
        String path = DESKTOP + "SimpleRead" + XLSX;
        // 这里默认读取第一个sheet
        read(path, IndexOrNameData.class, new AnalysisEventListener<IndexOrNameData>() {

            @Override
            public void invoke(IndexOrNameData data, AnalysisContext context) {
                log.info("读取数据：{}", JSON.toJSONString(data));
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("读取完毕");
            }
        }).sheet().doRead();
    }

    @Data
    public static class IndexOrNameData {
        /**
         * 强制读取第三个 这里不建议 index 和 name 同时用，要么一个对象只用index，要么一个对象只用name去匹配
         */
        @ExcelProperty(index = 2)
        private Double doubleData;
        /**
         * 用名字去匹配，这里需要注意，如果名字重复，会导致只有一个字段读取到数据
         */
        @ExcelProperty("字符串")
        private String string;
        @ExcelProperty("日期")
        private Date date;
    }

    @Slf4j
    static class DemoDataListener extends AnalysisEventListener<DemoData> {
        /**
         * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
         */
        private static final int BATCH_COUNT = 5;
        List<DemoData> list = new ArrayList<>();

        @Override
        public void invoke(DemoData data, AnalysisContext context) {
            log.info("解析到一条数据:{}", JSON.toJSONString(data));
            list.add(data);
            // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
            if (list.size() >= BATCH_COUNT) {
                saveData();
                // 存储完成清理 list
                list.clear();
            }
        }
        /**
         * 所有数据解析完成了 都会来调用
         */
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 这里也要保存数据，确保最后遗留的数据也存储到数据库
            saveData();
            log.info("所有数据解析完成！");
        }

        /**
         * 加上存储数据库
         */
        private void saveData() {
            if (!list.isEmpty()) {
                log.info("{}条数据，开始存储数据库！", list.size());
                log.info("{}保存到数据库", JSON.toJSONString(list));
                log.info("存储数据库成功！");
            }
        }
    }

    @Data
    public static class DemoData {
        private String string;
        private Date date;
        private Double doubleData;
    }

}
