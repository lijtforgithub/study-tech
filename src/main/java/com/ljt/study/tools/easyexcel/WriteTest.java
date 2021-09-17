package com.ljt.study.tools.easyexcel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.string.StringImageConverter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.FileUtils;
import com.alibaba.excel.write.handler.AbstractCellWriteHandler;
import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.merge.LoopMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.alibaba.excel.EasyExcelFactory.*;
import static com.ljt.study.Constant.DESKTOP;
import static com.ljt.study.Constant.XLSX;

/**
 * @author LiJingTang
 * @date 2020-10-20 14:45
 */
class WriteTest {

    @Test
    void simple() {
        String path = DESKTOP + "SimpleWrite" + XLSX;
        String path2 = DESKTOP + "SimpleWrite2" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("李白", new Date(), 6.1)).collect(Collectors.toList());
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
    void excludeOrInclude() {
        String path = DESKTOP + "ExcludeWrite" + XLSX;
        String path2 = DESKTOP + "IncludeWrite" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("李贺", new Date(), 6.1)).collect(Collectors.toList());

        // 根据用户传入字段 假设我们要忽略 date
        Set<String> excludeFiledNames = Sets.newHashSet("date");
        write(path, DemoData.class).excludeColumnFiledNames(excludeFiledNames).sheet("模板").doWrite(data);

        // 根据用户传入字段 假设我们只要导出 string
        Set<String> includeFiledNames = Sets.newHashSet("string");
        write(path2, DemoData.class).includeColumnFiledNames(includeFiledNames).sheet("模板").doWrite(data);
    }

    @Test
    void index() {
        String path = DESKTOP + "IndexWrite" + XLSX;
        List<IndexData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new IndexData("赵祯", new Date(), 6.1)).collect(Collectors.toList());

        write(path, IndexData.class).sheet("模板").doWrite(data);
    }

    @Test
    void complexHead() {
        String path = DESKTOP + "ComplexHeadWrite" + XLSX;
        List<ComplexHeadData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new ComplexHeadData("晏殊", new Date(), 6.1)).collect(Collectors.toList());
        write(path, ComplexHeadData.class).sheet("模板").doWrite(data);
    }

    @Test
    void repeated() {
        String path = DESKTOP + "RepeatedWrite" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("李白", new Date(), 6.1)).collect(Collectors.toList());

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
            excelWriter = write(path3).build();
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
    void converter() {
        String path = DESKTOP + "ConverterWrite" + XLSX;
        List<ConverterData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new ConverterData("杜甫", new Date(), 6.1)).collect(Collectors.toList());

        write(path, ConverterData.class).sheet("模板").doWrite(data);
    }

    @Test
    void image() throws IOException {
        String path = DESKTOP + "ImageWrite" + XLSX;
        InputStream inputStream = null;
        try {
            List<ImageData> list = new ArrayList<>();
            ImageData imageData = new ImageData();
            list.add(imageData);
            String imagePath = "C:/Users/Administrator/Pictures/Camera Roll/头像.png";
            // 放入五种类型的图片 实际使用只要选一种即可
            imageData.setString(imagePath);
            inputStream = FileUtils.openInputStream(new File(imagePath));
            imageData.setInputStream(inputStream);
            imageData.setByteArray(FileUtils.readFileToByteArray(new File(imagePath)));
            imageData.setFile(new File(imagePath));
            imageData.setUrl(new URL("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1603205219826&di=d580e9c4b6c3f49baa05bece6b80cd5a&imgtype=0&src=http%3A%2F%2Fcache.house.sina.com.cn%2Fbbshouse%2F2010%2F08%2F09%2F5%2F244a54418443714c6520bd.jpg"));
            write(path, ImageData.class).sheet().doWrite(list);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Test
    void template() {
        String path = DESKTOP + "TemplateWriteDemo" + XLSX;
        String templatePath = DESKTOP + "TemplateWrite" + XLSX;
        List<IndexData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new IndexData("赵构", new Date(), 6.1)).collect(Collectors.toList());

        write(path, IndexData.class).withTemplate(templatePath).sheet().doWrite(data);
    }

    @Test
    void widthAndHeight() {
        String path = DESKTOP + "WidthAndHeightWrite" + XLSX;
        List<WidthAndHeightData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new WidthAndHeightData("赵佶", new Date(), 6.1)).collect(Collectors.toList());

        write(path, WidthAndHeightData.class).sheet("模板").doWrite(data);
    }

    @Test
    void annotationStyle() {
        String path = DESKTOP + "AnnotationStyleWrite" + XLSX;
        List<DemoStyleData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoStyleData("赵恒", new Date(), 6.1)).collect(Collectors.toList());

        write(path, DemoStyleData.class).sheet("模板").doWrite(data);
    }

    @Test
    void style() {
        String path = DESKTOP + "StyleWrite" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("李白", new Date(), 6.1)).collect(Collectors.toList());
        // 头的策略
        WriteCellStyle headCellStyle = new WriteCellStyle();
        // 背景设置为红色
        headCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 20);
        headCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // 背景绿色
        contentCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 20);
        contentCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy styleStrategy = new HorizontalCellStyleStrategy(headCellStyle, contentCellStyle);

        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        write(path, DemoData.class).registerWriteHandler(styleStrategy).sheet("模板").doWrite(data);
    }

    @Test
    void merge() {
        String path = DESKTOP + "MergeWrite" + XLSX;
        List<DemoMergeData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoMergeData("李白", new Date(), 6.1)).collect(Collectors.toList());
        // 在DemoStyleData里面加上ContentLoopMerge注解
        write(path, DemoMergeData.class).sheet("模板").doWrite(data);

        String path2 = DESKTOP + "MergeWrite2" + XLSX;
        // 每隔2行会合并 把eachColumn 设置成 3 也就是我们数据的长度，所以就第一列会合并。当然其他合并策略也可以自己写
        LoopMergeStrategy loopMergeStrategy = new LoopMergeStrategy(2, 0);
        write(path2, DemoData.class).registerWriteHandler(loopMergeStrategy).sheet("模板").doWrite(data);
    }

    @Test
    void table() {
        String path = DESKTOP + "TableWrite" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("赵昚", new Date(), 6.1)).collect(Collectors.toList());

        // 这里直接写多个table的案例了，如果只有一个 也可以直一行代码搞定，参照其他案例
        ExcelWriter excelWriter = null;
        try {
            excelWriter = write(path, DemoData.class).build();
            // 把sheet设置为不需要头 不然会输出sheet的头 这样看起来第一个table 就有2个头了
            WriteSheet writeSheet = writerSheet("模板").needHead(Boolean.FALSE).build();
            // 这里必须指定需要头，table 会继承sheet的配置，sheet配置了不需要，table 默认也是不需要
            WriteTable writeTable0 = writerTable(0).needHead(Boolean.TRUE).build();
            WriteTable writeTable1 = writerTable(1).needHead(Boolean.TRUE).build();
            // 第一次写入会创建头
            excelWriter.write(data, writeSheet, writeTable0);
            // 第二次写如也会创建头，然后在第一次的后面写入数据
            excelWriter.write(data, writeSheet, writeTable1);
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.finish();
            }
        }
    }

    @Test
    void dynamicHead() {
        String path = DESKTOP + "DynamicHeadWrite" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("赵顼", new Date(), 6.1)).collect(Collectors.toList());

        List<List<String>> head = Lists.newArrayList(Lists.newArrayList("字符串"), Lists.newArrayList("日期"), Lists.newArrayList("数字", "第二行标题"));
        write(path).head(head).sheet("模板").doWrite(data);
    }

    @Test
    void longestMatchColumnWidth() {
        String path = DESKTOP + "LongestMatchColumnWidthWrite" + XLSX;
        List<LongestMatchColumnWidthData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new LongestMatchColumnWidthData("赵熙", new Date(),
                1000000000000.0)).collect(Collectors.toList());
        write(path, LongestMatchColumnWidthData.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet("模板").doWrite(data);
    }

    @Test
    void customHandler() {
        String path = DESKTOP + "CustomHandlerWrite" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("赵曙", new Date(), 6.1)).collect(Collectors.toList());

        write(path, DemoData.class)
                .registerWriteHandler(new CustomSheetWriteHandler())
                .registerWriteHandler(new CustomCellWriteHandler()).sheet("模板").doWrite(data);
    }


    @Test
    void comment() {
        String path = DESKTOP + "CommentWrite" + XLSX;
        List<DemoData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new DemoData("赵匡胤", new Date(), 6.1)).collect(Collectors.toList());

        // 这里要注意inMemory 要设置为true，才能支持批注。目前没有好的办法解决 不在内存处理批注。这个需要自己选择。
        write(path, DemoData.class).inMemory(Boolean.TRUE).registerWriteHandler(new CommentWriteHandler()).sheet("模板").doWrite(data);
    }

    @Test
    void noModel() {
        String path = DESKTOP + "NoModelWrite" + XLSX;
        List<List<String>> head = Lists.newArrayList(Lists.newArrayList("唐诗"), Lists.newArrayList("宋词"), Lists.newArrayList("元曲"));
        List<List<String>> data = Lists.newArrayList(Lists.newArrayList("落叶满长安", "封狼居胥"), Lists.newArrayList("封狼居胥"), Lists.newArrayList("多情自古", "此恨绵绵"));

        write(path).head(head).sheet("模板").doWrite(data);
    }

    @Test
    void formula() {
        String path = DESKTOP + "formula" + XLSX;
        List<FormulaDemo> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new FormulaDemo(i, i)).collect(Collectors.toList());
        write(path, FormulaDemo.class).sheet().registerWriteHandler(new FormulaCellWriteHandler()).doWrite(data);
    }

    static class FormulaCellWriteHandler extends AbstractCellWriteHandler {
        @Override
        public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
            if (Boolean.FALSE.equals(isHead) && cell.getColumnIndex() == 2) {
                System.out.println(head.getFieldName());
                int index = relativeRowIndex + 2;
                cell.setCellType(CellType.FORMULA);
                cell.setCellFormula(String.format("=(A%d * B%d)", index, index));
            }
        }
    }

    @Data
    static class FormulaDemo {
        @ExcelProperty("数量")
        private int num;
        @ExcelProperty("单价")
        private int price;
        @ExcelProperty("金额")
        private int money;

        public FormulaDemo(int num, int price) {
            this.num = num;
            this.price = price;
        }
    }


    static class CommentWriteHandler extends AbstractRowWriteHandler {

        @Override
        public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row,
                                    Integer relativeRowIndex, Boolean isHead) {
            if (Boolean.TRUE.equals(isHead)) {
                Sheet sheet = writeSheetHolder.getSheet();
                Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
                // 在第一行 第二列创建一个批注
                Comment comment = drawingPatriarch.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 1, 0, (short) 2, 1));
                // 输入批注信息
                comment.setString(new XSSFRichTextString("创建批注!"));
                // 将批注添加到单元格对象中
                sheet.getRow(0).getCell(1).setCellComment(comment);
            }
        }
    }

    /**
     * 自定义拦截器。对第一行第一列的头超链接到:https://github.com/alibaba/easyexcel
     */
    @Slf4j
    static class CustomCellWriteHandler implements CellWriteHandler {

        @Override
        public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row,
                                     Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {

        }

        @Override
        public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell,
                                    Head head, Integer relativeRowIndex, Boolean isHead) {

        }

        @Override
        public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, CellData cellData, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

        }

        @Override
        public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                     List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
            // 这里可以对cell进行任何操作
            log.info("第{}行，第{}列写入完成。", cell.getRowIndex(), cell.getColumnIndex());
            if (Boolean.TRUE.equals(isHead) && cell.getColumnIndex() == 0) {
                CreationHelper createHelper = writeSheetHolder.getSheet().getWorkbook().getCreationHelper();
                Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.URL);
                hyperlink.setAddress("https://github.com/alibaba/easyexcel");
                cell.setHyperlink(hyperlink);
            }
        }
    }

    /**
     * 自定义拦截器.对第一列第一行和第二行的数据新增下拉框，显示 测试1 测试2
     */
    @Slf4j
    static class CustomSheetWriteHandler implements SheetWriteHandler {

        @Override
        public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

        }

        @Override
        public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
            log.info("第{}个Sheet写入成功。", writeSheetHolder.getSheetNo());

            // 区间设置 第一列第一行和第二行的数据。由于第一行是头，所以第一、二行的数据实际上是第二三行
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 2, 0, 0);
            DataValidationHelper helper = writeSheetHolder.getSheet().getDataValidationHelper();
            DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[]{"测试1", "测试2"});
            DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);
            writeSheetHolder.getSheet().addValidationData(dataValidation);
        }
    }


    @Data
    @AllArgsConstructor
    static class LongestMatchColumnWidthData {
        @ExcelProperty("字符串标题")
        private String string;
        @ExcelProperty("日期标题很长日期标题很长日期标题很长很长")
        private Date date;
        @ExcelProperty("数字")
        private Double doubleData;
    }

    /**
     * 将第6-7行的2-3列合并成一个单元格
     */
    @Data
    @AllArgsConstructor
    @OnceAbsoluteMerge(firstRowIndex = 5, lastRowIndex = 6, firstColumnIndex = 1, lastColumnIndex = 2)
    static class DemoMergeData {
        // 这一列 每隔2行 合并单元格
        @ContentLoopMerge(eachRow = 2)
        @ExcelProperty("字符串标题")
        private String string;
        @ExcelProperty("日期标题")
        private Date date;
        @ExcelProperty("数字标题")
        private Double doubleData;
    }

    @Data
    @AllArgsConstructor
    // 头背景设置成红色 IndexedColors.RED.getIndex()
    @HeadStyle(fillPatternType = FillPatternType.SOLID_FOREGROUND, fillForegroundColor = 10)
    // 头字体设置成20
    @HeadFontStyle(fontHeightInPoints = 20)
    // 内容的背景设置成绿色 IndexedColors.GREEN.getIndex()
    @ContentStyle(fillPatternType = FillPatternType.SOLID_FOREGROUND, fillForegroundColor = 17)
    // 内容字体设置成20
    @ContentFontStyle(fontHeightInPoints = 20)
    static class DemoStyleData {
        // 字符串的头背景设置成粉红 IndexedColors.PINK.getIndex()
        @HeadStyle(fillPatternType = FillPatternType.SOLID_FOREGROUND, fillForegroundColor = 14)
        // 字符串的头字体设置成20
        @HeadFontStyle(fontHeightInPoints = 30)
        // 字符串的内容的背景设置成天蓝 IndexedColors.SKY_BLUE.getIndex()
        @ContentStyle(fillPatternType = FillPatternType.SOLID_FOREGROUND, fillForegroundColor = 40)
        // 字符串的内容字体设置成20
        @ContentFontStyle(fontHeightInPoints = 30)
        @ExcelProperty("字符串标题")
        private String string;
        @ExcelProperty("日期标题")
        private Date date;
        @ExcelProperty("数字标题")
        private Double doubleData;
    }

    @Data
    @AllArgsConstructor
    @ContentRowHeight(30)
    @HeadRowHeight(50)
    @ColumnWidth(25)
    static class WidthAndHeightData {
        @ExcelProperty("字符串标题")
        private String string;
        @ExcelProperty("日期标题")
        private Date date;
        /**
         * 宽度为50
         */
        @ColumnWidth(40)
        @ExcelProperty("数字标题")
        private Double doubleData;
    }

    @Data
    @ContentRowHeight(100)
    @ColumnWidth(100 / 8)
    static class ImageData {
        private File file;
        private InputStream inputStream;
        /**
         * 如果string类型 必须指定转换器，string默认转换成string
         */
        @ExcelProperty(converter = StringImageConverter.class)
        private String string;
        private byte[] byteArray;
        /**
         * 根据url导出
         *
         * @since 2.1.1
         */
        private URL url;
    }

    public static class CustomStringStringConverter implements Converter<String> {

        @Override
        public Class<?> supportJavaTypeKey() {
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
        public CellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
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

        public DemoData(String string, Date date, Double doubleData) {
            this(string, date, doubleData, "ignore");
        }
    }

}
