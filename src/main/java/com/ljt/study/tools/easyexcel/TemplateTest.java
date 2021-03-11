package com.ljt.study.tools.easyexcel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.alibaba.excel.EasyExcelFactory.write;
import static com.alibaba.excel.EasyExcelFactory.writerSheet;
import static com.ljt.study.Constant.DESKTOP;
import static com.ljt.study.Constant.XLSX;

/**
 * 导出
 *
 * @author LiJingTang
 * @date 2020-10-19 15:00
 */
@Slf4j
public class TemplateTest {

    @Test
    public void testSimpleFill() throws IOException {
        ClassPathResource resource = getResource("SimpleFill");
        String templatePath = resource.getURI().getRawPath();
        String path = DESKTOP + resource.getFilename();
        FillData fillData = new FillData("李斯", 5.4);

        // 这里会填充到第一个sheet， 然后文件流会自动关闭
        write(path).withTemplate(templatePath).sheet().doFill(fillData);

        String mapPath = DESKTOP + "Map-" + resource.getFilename();
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
        map.put("name", "张三");
        map.put("number", 5.3);
        write(mapPath).withTemplate(templatePath).sheet().doFill(map);
    }

    /**
     * 填充list 的时候还要注意 模板中{.} 多了个点 表示list
     */
    @Test
    public void testListFill() throws IOException {
        ClassPathResource resource = getResource("ListFill");
        String templatePath = resource.getURI().getRawPath();
        String path = DESKTOP + resource.getFilename();
        List<FillData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new FillData("李白", 6)).collect(Collectors.toList());

        write(path).withTemplate(templatePath).sheet().doFill(data);

        // 分多次 填充 会使用文件缓存（省内存）
        String multiPath = DESKTOP + "Multi-" + resource.getFilename();
        ExcelWriter excelWriter = write(multiPath).withTemplate(templatePath).build();
        WriteSheet writeSheet = writerSheet().build();
        excelWriter.fill(data, writeSheet);
        excelWriter.fill(data, writeSheet);
        // 千万别忘记关闭流
        excelWriter.finish();
    }

    @Test
    public void testComplexFill() throws IOException {
        ClassPathResource resource = getResource("ComplexFill");
        String templatePath = resource.getURI().getRawPath();
        String path = DESKTOP + resource.getFilename();
        List<FillData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new FillData("杜甫", 6)).collect(Collectors.toList());

        ExcelWriter excelWriter = write(path).withTemplate(templatePath).build();
        WriteSheet writeSheet = writerSheet().build();
        // 这里注意 入参用了forceNewRow 代表在写入list的时候不管list下面有没有空行 都会创建一行，然后下面的数据往后移动。默认 是false，会直接使用下一行，如果没有则创建。
        // forceNewRow 如果设置了true,有个缺点 就是他会把所有的数据都放到内存了，所以慎用
        // 简单的说 如果你的模板有list,且list不是最后一行，下面还有数据需要填充 就必须设置 forceNewRow=true 但是这个就会把所有数据放到内存 会很耗内存
        // 如果数据量大 list不是最后一行 参照下一个
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        excelWriter.fill(data, fillConfig, writeSheet);
        excelWriter.fill(data, fillConfig, writeSheet);
        Map<String, Object> map = ImmutableMap.of("date", "2020年10月20日11:42:30", "total", 1000);
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
    }

    /**
     * 数据量大的复杂填充
     * <p>
     * 这里的解决方案是 确保模板list为最后一行，然后再拼接table.还有03版没救，只能刚正面加内存。
     */
    @Test
    public void testComplexFillWithTable() throws IOException {
        ClassPathResource resource = getResource("ComplexFillWithTable");
        String templatePath = resource.getURI().getRawPath();
        String path = DESKTOP + resource.getFilename();
        List<FillData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new FillData("罗隐", 7)).collect(Collectors.toList());

        ExcelWriter excelWriter = write(path).withTemplate(templatePath).build();
        WriteSheet writeSheet = writerSheet().build();
        // 直接写入数据
        excelWriter.fill(data, writeSheet).fill(data, writeSheet);

        // 写入list之前的数据
        Map<String, Object> map = ImmutableMap.of("date", "2020年10月20日12:00:00");
        excelWriter.fill(map, writeSheet);

        // list 后面还有个统计 想办法手动写入 这里偷懒直接用list 也可以用对象
        List<List<String>> totalListList = new ArrayList<>();
        totalListList.add(Lists.newArrayList(null, null, null, "统计:1010"));
        // 这里是write方法 不是fill
        excelWriter.write(totalListList, writeSheet).finish();
    }

    /**
     * 横向填充
     */
    @Test
    public void testHorizontalFill() throws IOException {
        ClassPathResource resource = getResource("HorizontalFill");
        String templatePath = resource.getURI().getRawPath();
        String path = DESKTOP + resource.getFilename();
        List<FillData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new FillData("刘郎", 7)).collect(Collectors.toList());

        ExcelWriter excelWriter = write(path).withTemplate(templatePath).build();
        WriteSheet writeSheet = writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        excelWriter.fill(data, fillConfig, writeSheet).fill(data, fillConfig, writeSheet);

        Map<String, Object> map = ImmutableMap.of("date", "2020年10月20日14:00:00");
        excelWriter.fill(map, writeSheet).finish();
    }

    @Test
    public void testCompositeFill() throws IOException {
        ClassPathResource resource = getResource("CompositeFill");
        String templatePath = resource.getURI().getRawPath();
        String path = DESKTOP + resource.getFilename();
        List<FillData> data = IntStream.rangeClosed(1, 10).mapToObj(i -> new FillData("白乐天", 7)).collect(Collectors.toList());

        ExcelWriter excelWriter = write(path).withTemplate(templatePath).build();
        WriteSheet writeSheet = writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        // 如果有多个list 模板上必须有{前缀.} 这里的前缀就是 data1，然后多个list必须用 FillWrapper包裹
        excelWriter.fill(new FillWrapper("data1", data), fillConfig, writeSheet);
        excelWriter.fill(new FillWrapper("data1", data), fillConfig, writeSheet);
        excelWriter.fill(new FillWrapper("data2", data), writeSheet);
        excelWriter.fill(new FillWrapper("data2", data), writeSheet);
        excelWriter.fill(new FillWrapper("data3", data), writeSheet);
        excelWriter.fill(new FillWrapper("data3", data), writeSheet);

        Map<String, Object> map = ImmutableMap.of("date", "2010年10月20日14:28:28");
        excelWriter.fill(map, writeSheet).finish();
    }


    private static ClassPathResource getResource(String fileName) {
        return new ClassPathResource("/tools/easyexcel/" + fileName + XLSX);
    }

    @Data
    @AllArgsConstructor
    static class FillData {
        private String name;
        private double number;
    }

}
