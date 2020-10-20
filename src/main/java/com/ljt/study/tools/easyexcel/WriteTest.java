package com.ljt.study.tools.easyexcel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.alibaba.excel.EasyExcelFactory.write;
import static com.alibaba.excel.EasyExcelFactory.writerSheet;
import static com.ljt.study.Constant.DESKTOP;
import static com.ljt.study.Constant.XLSX;

/**
 * @author LiJingTang
 * @date 2020-10-20 14:45
 */
public class WriteTest {

    @Test
    public void testSimpleWrite() {
        String path = DESKTOP + "SimpleWrite" + XLSX;
        String path2 = DESKTOP + "SimpleWrite2" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("李白", new Date(), 6.1, "Ignore")).collect(Collectors.toList());
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭 如果这里想使用03 则传入excelType参数即可
        write(path, DemoData.class).sheet("模板").doWrite(data);

        ExcelWriter excelWriter = null;
        try {
            excelWriter = write(path2, DemoData.class).build();
            WriteSheet writeSheet = writerSheet("模板").build();
            excelWriter.write(data, writeSheet);
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.finish();
            }
        }
    }

    @Test
    public void testExcludeOrIncludeWrite() {
        String path = DESKTOP + "ExcludeWrite" + XLSX;
        String path2 = DESKTOP + "IncludeWrite" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("李贺", new Date(), 6.1, "Ignore")).collect(Collectors.toList());

        // 根据用户传入字段 假设我们要忽略 date
        Set<String> excludeFiledNames = Sets.newHashSet("date");
        write(path, DemoData.class).excludeColumnFiledNames(excludeFiledNames).sheet("模板").doWrite(data);

        // 根据用户传入字段 假设我们只要导出 string
        Set<String> includeFiledNames = Sets.newHashSet("string");
        write(path2, DemoData.class).includeColumnFiledNames(includeFiledNames).sheet("模板").doWrite(data);
    }

    @Test
    public void testIndexWrite() {
        String path = DESKTOP + "IndexWrite" + XLSX;
        List<IndexData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new IndexData("赵祯", new Date(), 6.1)).collect(Collectors.toList());

        write(path, IndexData.class).sheet("模板").doWrite(data);
    }

    @Test
    public void testComplexHeadWrite() {
        String path = DESKTOP + "ComplexHeadWrite" + XLSX;
        List<ComplexHeadData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new ComplexHeadData("晏殊", new Date(), 6.1)).collect(Collectors.toList());
        write(path, ComplexHeadData.class).sheet("模板").doWrite(data);
    }

    @Test
    public void testRepeatedWrite() {
        String path = DESKTOP + "RepeatedWrite" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("李白", new Date(), 6.1, "Ignore")).collect(Collectors.toList());

        ExcelWriter excelWriter = null;
        try {
            // 这里 需要指定写用哪个class去写
            excelWriter = write(path, DemoData.class).build();
            // 这里注意 如果同一个sheet只要创建一次
            WriteSheet writeSheet = writerSheet("模板").build();
            // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来
            for (int i = 0; i < 5; i++) {
                excelWriter.write(data, writeSheet);
            }
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.finish();
            }
        }

        String path2 = DESKTOP + "RepeatedWrite2" + XLSX;
        try {
            excelWriter = write(path2, DemoData.class).build();
            // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来。这里最终会写到5个sheet里面
            for (int i = 0; i < 3; i++) {
                // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样
                WriteSheet writeSheet = writerSheet(i, "模板" + i).build();
                excelWriter.write(data, writeSheet);
            }
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.finish();
            }
        }

        String path3 = DESKTOP + "RepeatedWrite3" + XLSX;
        try {
            // 这里 指定文件
            excelWriter = write(path3).build();
            // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来。这里最终会写到5个sheet里面
            for (int i = 0; i < 2; i++) {
                // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样。这里注意DemoData.class 可以每次都变，我这里为了方便 所以用的同一个class 实际上可以一直变
                WriteSheet writeSheet = writerSheet(i, "模板" + i).head(DemoData.class).build();
                excelWriter.write(data, writeSheet);
            }
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.finish();
            }
        }
    }

    @Test
    public void testConverterWrite() {
        String path = DESKTOP + "ConverterWrite" + XLSX;
        List<ConverterData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new ConverterData("杜甫", new Date(), 6.1)).collect(Collectors.toList());

        write(path, ConverterData.class).sheet("模板").doWrite(data);
    }


    public static class CustomStringStringConverter implements Converter<String> {

        @Override
        public Class supportJavaTypeKey() {
            return String.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public String convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            return null;
        }

        @Override
        public CellData convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
            return new CellData("自定义：" + value);
        }
    }

    @Data
    @AllArgsConstructor
    static class ConverterData {
        /**
         * 我想所有的 字符串起前面加上"自定义："三个字
         */
        @ExcelProperty(value = "字符串标题", converter = CustomStringStringConverter.class)
        private String string;
        /**
         * 我想写到excel 用年月日的格式
         */
        @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
        @ExcelProperty("日期标题")
        private Date date;
        /**
         * 我想写到excel 用百分比表示
         */
        @NumberFormat("#.##%")
        @ExcelProperty(value = "数字标题")
        private Double doubleData;
    }


    @Data
    @AllArgsConstructor
    static class ComplexHeadData {
        @ExcelProperty({"主标题", "字符串标题"})
        private String string;
        @ExcelProperty({"主标题", "日期标题"})
        private Date date;
        @ExcelProperty({"主标题", "数字标题"})
        private Double doubleData;
    }

    @Data
    @AllArgsConstructor
    static class IndexData {
        @ExcelProperty(value = "字符串", index = 0)
        private String string;
        @ExcelProperty(value = "日期", index = 1)
        private Date date;
        /**
         * 这里设置3 会导致第二列空的
         */
        @ExcelProperty(value = "数字", index = 3)
        private Double doubleData;
    }

    @Data
    @AllArgsConstructor
    static class DemoData {
        @ExcelProperty("字符串")
        private String string;
        @ExcelProperty("日期")
        private Date date;
        @ExcelProperty("数字")
        private Double doubleData;
        /**
         * 忽略这个字段
         */
        @ExcelIgnore
        private String ignore;
    }

}
