package com.ljt.study.tools.easyexcel;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.context.xlsx.DefaultXlsxReadContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.alibaba.excel.EasyExcelFactory.*;
import static com.ljt.study.Constant.XLSX;

/**
 * @author LiJingTang
 * @date 2020-10-22 11:02
 */
@Slf4j
public class PeiPei {

    private static final String PATH = "D:/培培/";
    private static final String ZERO = "0";
    private static final BigDecimal ONE_DAY = BigDecimal.valueOf(7);
    private static final Pattern NUM_REG = Pattern.compile("\\d{1,2}\\.\\d{1,2}|\\d{1,2}");


    public static void main(String[] args) {
        List<RowData> data = new ArrayList<>();
        data.addAll(readKaoQin("百色百东分院", 1));
        data.addAll(readKaoQin("苍山小院", 1));
        data.addAll(readKaoQin("富政储出", 1));
        data.addAll(readKaoQin("中天金融中心", 1));
        data.addAll(readKaoQin("丽江束河秘境", 1));
        data.addAll(readKaoQin("修文农商银行", 1));
        data.addAll(readKaoQin("深圳技术大学", 1));
        data.addAll(readKaoQin("深圳万丽酒店", 2));
//        data.addAll(readKaoQin("成都", 1));
//        data.addAll(readKaoQin("仁怀家装", 1));
//        data.addAll(readKaoQin("茅台医院", 2));
//        data.addAll(readKaoQin("泰州金融城", 1));
//        data.addAll(readKaoQin("乌当支行", 1));
//        data.addAll(readKaoQin("会展城家装", 1));
//        data.addAll(readKaoQin("新世界家装", 2));

        data.sort(Comparator.comparing(RowData::getName).thenComparing(RowData::getMonth).thenComparing(RowData::getProjectName));

        String templatePath = PATH + "汇总模板" + XLSX;
        write(PATH + "汇总" + XLSX).withTemplate(templatePath).sheet().doFill(data);
    }


    private static List<RowData> readKaoQin(String projectName, int count) {
        List<RowData> list = new ArrayList<>();
        Set<String> set = new HashSet<>();

        ExcelReader excelReader = null;
        try {
            excelReader = read(PATH + projectName + "/合计" + XLSX, RowData.class, new AnalysisEventListener<RowData>() {
                @Override
                public void invoke(RowData data, AnalysisContext context) {
                    DefaultXlsxReadContext readContext = (DefaultXlsxReadContext) context;
                    String sheetName = readContext.getCurrentSheet().getSheetName();
                    String key = data.getName() + sheetName;
                    Assert.isTrue(!set.contains(key), "key重复：" + projectName + key);
                    set.add(key);

                    data.setMonth(sheetName);
                    data.setProjectName(projectName);
                    list.add(data);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    DefaultXlsxReadContext readContext = (DefaultXlsxReadContext) context;
                    log.info("{}【{}】月数据读取完毕", projectName, readContext.getCurrentSheet().getSheetName());
                }
            }).build();

            ExcelReader finalExcelReader = excelReader;
            IntStream.rangeClosed(0, count - 1).forEach(i -> {
                ReadSheet readSheet = readSheet(i).build();
                finalExcelReader.read(readSheet);
            });

        } finally {
            if (Objects.nonNull(excelReader)) {
                excelReader.finish();
            }
        }

        return list;
    }


    @Data
    public static class RowData {
        @ExcelProperty(index = 0)
        private String name;
        @ExcelProperty("合计")
        private String sum = ZERO;
        @ExcelProperty("加班")
        private String work = ZERO;
        private String total = ZERO;
        private String month;
        private String projectName;

        public void setSum(String sum) {
            this.sum = getNum(sum);
        }

        public void setWork(String work) {
            String temp = getNum(work);
            if (!ZERO.equals(temp)) {
                this.work = new BigDecimal(temp).divide(ONE_DAY, 2, RoundingMode.HALF_UP).toString();
            }
        }

        public String getTotal() {
            return new BigDecimal(sum).add(new BigDecimal(work)).toString();
        }

        private static String getNum(String num) {
            final Matcher matcher = NUM_REG.matcher(num);
            return matcher.find() ? matcher.group() : ZERO;
        }
    }

}
