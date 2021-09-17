```azure
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.guowy.cloud.common.enums.StatusEnum;
import com.guowy.cloud.common.exception.BusinessException;
import com.guowy.cloud.common.util.DateUtils;
import com.guowy.cloud.common.util.EnumUtils;
import com.guowy.cloud.common.util.JsonResult;
import com.guowy.cloud.crud.enums.FieldSuffix;
import com.guowy.cloud.security.context.UserContextHolder;
import com.guowy.finance.enums.PayTypeEnum;
import com.guowy.order.enums.OrderStatusEnum;
import com.guowy.report.webapp.model.PurchaseOrder;
import com.guowy.report.webapp.model.PurchaseOrderSum;
import com.guowy.report.webapp.service.PurchaseOrderService;
import com.guowy.report.webapp.vo.PurchaseOrderQueryDTO;
import com.guowy.report.webapp.vo.PurchaseOrderSumVO;
import com.guowy.report.webapp.vo.PurchaseOrderVO;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.alibaba.excel.EasyExcelFactory.*;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * @author LiJingTang
 * @date 2021-09-08 16:00
 */
@Slf4j
@RefreshScope
@Service
public class PurchaseOrderBiz {

    /**
     * 每次最多查2000
     */
    private static final int BATCH_QUERY = 2000;
    private static final List<Integer> ORDER_STATUS = Lists.newArrayList(OrderStatusEnum.PRE_SEND.getValue(),
            OrderStatusEnum.PRE_RECV.getValue(), OrderStatusEnum.COMPLETED.getValue());
    private static final ClassPathResource TEMPLATE_RESOURCE = new ClassPathResource("/excel/商户采购结算报表.xlsx");
    private static final String REMARK = "公式说明：\n" +
            "小计=实付金额-退单总额-赔付金额-退回货款+需补货款\n" +
            "注：退回货款：支付金额多于实收货款金额\n" +
            "       需补货款：支付金额少于实收货款金额\n" +
            "       赔付金额：赔付金额是另外转账至钱包中";
    private static final WriteTable WRITE_TABLE;
    private static final List<List<String>> LAST_2ROW;

    static {
        WriteCellStyle cellStyle = new WriteCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        WriteFont font = new WriteFont();
        font.setColor(IndexedColors.RED.getIndex());
        font.setFontHeightInPoints((short) 11);
        cellStyle.setWriteFont(font);
        HorizontalCellStyleStrategy styleStrategy = new HorizontalCellStyleStrategy(null, cellStyle);

        WRITE_TABLE = writerTable()
                // 内容样式
                .registerWriteHandler(styleStrategy)
                // 行高
                .registerWriteHandler(new RowHeightStrategy())
                // 合并
                .registerWriteHandler(new MergeStrategy()).build();

        // 末尾两行 空行+说明
        LAST_2ROW = new ArrayList<>(2);
        LAST_2ROW.add(Lists.newArrayList(""));
        LAST_2ROW.add(Lists.newArrayList(null, REMARK));
    }


    @Value("${guowy.purchaseOrder.excel.count:10000}")
    private int excelCount;
    @Value("${guowy.purchaseOrder.excel.tempPath:/temp/report/purchaseOrder/}")
    private String excelTempPath;
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostConstruct
    public void init() {
        try {
            File folder = new File(excelTempPath);
            if (!folder.exists()) {
                Assert.isTrue(folder.mkdirs(), "创建临时目录失败");
            }

            File file = new File(excelTempPath + "empty.xlsx");
            write(file).withTemplate(TEMPLATE_RESOURCE.getInputStream()).build().finish();
        } catch (IOException e) {
            log.error("init", e);
        }
    }

    public JsonResult<PurchaseOrderSumVO> sum(PurchaseOrderQueryDTO queryDTO) {
        PurchaseOrder paramOrder = getQueryParam(queryDTO);
        List<PurchaseOrderSum> sumList = purchaseOrderService.sumOrder(paramOrder);
        PurchaseOrderSumVO vo = new PurchaseOrderSumVO();
        vo.setPaymentAmount(BigDecimal.ZERO);
        vo.setRefundAmount(BigDecimal.ZERO);
        vo.setCompensateAmount(BigDecimal.ZERO);
        vo.setReturnAmount(BigDecimal.ZERO);
        vo.setRepairAmount(BigDecimal.ZERO);

        for (PurchaseOrderSum sum : sumList) {
            vo.setPaymentAmount(vo.getPaymentAmount().add(new BigDecimal(sum.getRealAmount())));
            vo.setRefundAmount(vo.getRefundAmount().add(new BigDecimal(sum.getRejectRefund())));
            vo.setCompensateAmount(vo.getCompensateAmount().add(new BigDecimal(sum.getCompensateAmountPlatform())).add(new BigDecimal(sum.getCompensateAmountNode())));
            // 大于0 退款
            if (1 == sum.getSignType()) {
                vo.setReturnAmount(vo.getReturnAmount().add(new BigDecimal(sum.getFillOutSubtotal())));
            }
            // 小于0 补款
            if (-1 == sum.getSignType()) {
                vo.setRepairAmount(vo.getRepairAmount().add(new BigDecimal(sum.getFillOutSubtotal()).abs()));
            }
        }

        return new JsonResult<>(StatusEnum.OK.getValue(), null, vo);
    }

    public JsonResult<PageInfo<PurchaseOrderVO>> findByPage(int pageNum, int pageSize, PurchaseOrderQueryDTO queryDTO) {
        PurchaseOrder paramOrder = getQueryParam(queryDTO);
        PageInfo<PurchaseOrder> page = purchaseOrderService.findByPage(pageNum, pageSize, paramOrder);
        PageInfo<PurchaseOrderVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(page, pageInfo);

        if (!CollectionUtils.isEmpty(page.getList())) {
            Map<Integer, String> orderStatusMap = getOrderStatusMap();
            Map<Integer, String> payWayMap = EnumUtils.valueDesc(PayTypeEnum.values());
            pageInfo.setList(page.getList().stream().map(o -> getOrderVO(o, orderStatusMap, payWayMap)).collect(Collectors.toList()));
        }

        return new JsonResult<>(StatusEnum.OK.getValue(), null, pageInfo);
    }

    @SneakyThrows
    @Transactional(readOnly = true)
    public void export(PurchaseOrderQueryDTO queryDTO, HttpServletRequest request, HttpServletResponse response) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("准备工作");
        PurchaseOrder paramOrder = getQueryParam(queryDTO);
        Long total = purchaseOrderService.count(paramOrder);
        Assert.isTrue(total > 0, "没有符合条件的数据");

        // 创建目录和临时Excel文件
        List<File> files = createTempFile(total);
        final List<File> subList = files.subList(1, files.size());
        stopWatch.stop();

        try {
            stopWatch.start("创建Excel输出对象");
            // 生成Excel输出包装对象
            List<ExcelWriterHolder> holderList = getHolders(queryDTO, subList);
            stopWatch.stop();

            stopWatch.start("填充Excel数据");
            // 写Excel文件
            writeExcel(holderList, paramOrder, total);
            stopWatch.stop();

            stopWatch.start("zip压缩");
            handleHttp(request, response);
            // ZIP压缩 0号位置为目录 排除
            exportZip(subList, response.getOutputStream());
            stopWatch.stop();
            log.info(stopWatch.prettyPrint());
        } catch (IOException e) {
            throw new BusinessException(StatusEnum.FAIL.getCode(), e);
        } finally {
            // 0下标为目录
            FileUtils.deleteQuietly(files.get(0));
        }
    }

    /**
     * 创建临时文件
     */
    private List<File> createTempFile(long total) {
        int pages = (int) (total / excelCount + ((total % excelCount == 0) ? 0 : 1));
        List<File> files = new ArrayList<>(pages + 1);
        File folder = new File(excelTempPath + System.nanoTime() + '/');
        if (!folder.exists()) {
            Assert.isTrue(folder.mkdirs(), "创建临时目录失败");
        }

        files.add(folder);

        for (int i = 1; i <= pages; i++) {
            File file = new File(folder, getFileName() + (pages > 1 ? ("_" + i) : "") + ".xlsx");
            files.add(file);
        }
        return files;
    }

    private void handleHttp(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        response.reset();
        response.setContentType(APPLICATION_OCTET_STREAM.getMimeType());
        String fileName = getFileName() + ".zip";
        String agent = request.getHeader(USER_AGENT).toUpperCase();
        // IE浏览器和Edge浏览器
        if (agent.contains("MSIE") || (agent.contains("GECKO") && agent.contains("RV:11"))) {
            fileName = URLEncoder.encode(fileName, UTF_8.name());
        } else {
            fileName = new String(fileName.getBytes(UTF_8), ISO_8859_1);
        }
        response.setHeader(CONTENT_DISPOSITION, "attachment; filename=" + fileName);
    }

    /**
     * 写Excel文件
     */
    private void writeExcel(List<ExcelWriterHolder> holderList, PurchaseOrder paramOrder, long total) throws IOException {
        Map<Integer, String> orderStatusMap = getOrderStatusMap();
        Map<Integer, String> payWayMap = EnumUtils.valueDesc(PayTypeEnum.values());

        try (Page<?> page = new Page<>()) {
            page.setPageSize(BATCH_QUERY);
            page.setTotal(total);

            for (int i = 1; i <= page.getPages(); i++) {
                page.setPageNum(i);
                // 再次设置 为了计算startRow endRow
                page.pageSize(page.getPageSize());
                List<PurchaseOrderVO> list = purchaseOrderService.findList(page.getStartRow(), page.getPageSize(), paramOrder)
                        .stream().map(o -> getOrderVO(o, orderStatusMap, payWayMap)).collect(Collectors.toList());

                int startIndex = page.getStartRow() / excelCount;
                int endIndex = (page.getStartRow() + list.size() - 1) / excelCount;

                for (int index = startIndex; index <= endIndex; index++) {
                    List<PurchaseOrderVO> subList = startIndex == endIndex ? list :
                            list.subList(index * excelCount, Math.min(list.size(), (index + 1) * excelCount));
                    ExcelWriterHolder holder = holderList.get(index);
                    // 填充明细数据
                    holder.fillData(subList);
                    // 要么当前Excel写满 要么是最后一页
                    if (holder.getSum().getCount() == excelCount || i == page.getPages()) {
                        // 填充合计信息 + 写入空行+说明
                        holder.fillSum();
                    }
                }
            }
        }
    }

    /**
     * 获取Excel写包装对象
     */
    private List<ExcelWriterHolder> getHolders(PurchaseOrderQueryDTO queryDTO, List<File> files) throws IOException {
        String startDate = DateUtils.format(queryDTO.getOrderStartTime(), "yyyy/M/d");
        String endDate = DateUtils.format(queryDTO.getOrderEndTime(), "yyyy/M/d");
        // 生成写出对象
        List<ExcelWriterHolder> holderList = new ArrayList<>(files.size());
        for (File file : files) {
            ExcelWriter excelWriter = write(file).withTemplate(TEMPLATE_RESOURCE.getInputStream()).build();
            holderList.add(new ExcelWriterHolder(excelWriter, startDate, endDate));
        }
        return holderList;
    }

    /**
     * 把Excel文件压缩成Zip
     */
    private void exportZip(List<File> files, OutputStream outputStream) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            for (File file : files) {
                zipOut.putNextEntry(new ZipEntry(file.getName()));
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    IOUtils.copyLarge(inputStream, zipOut);
                }
            }
            zipOut.flush();
            outputStream.flush();
        }
    }

    /**
     * 转换采购订单对象
     */
    private PurchaseOrderVO getOrderVO(PurchaseOrder order, Map<Integer, String> orderStatusMap, Map<Integer, String> payWayMap) {
        PurchaseOrderVO vo = new PurchaseOrderVO();
        vo.setId(order.getId());
        vo.setOrderCode(order.getOrderCode());
        vo.setOrderTime(DateUtils.formatDateTime(order.getCreateTime()));
        vo.setBuyer(order.getPayAccountName());
        vo.setSeller(order.getSellerName());
        vo.setStatusDesc(orderStatusMap.get(order.getStatus()));
        vo.setPayTypeDesc(payWayMap.get(order.getPayWay()));
        vo.setOrderAmount(new BigDecimal(order.getOrderAmount()));
        vo.setTransferFee(new BigDecimal(order.getTradeFee()));
        vo.setPaymentAmount(new BigDecimal(order.getRealAmount()));
        vo.setChargebackAmount(new BigDecimal(order.getRejectRefund()));
        vo.setReturnFee(new BigDecimal(order.getRefundableTradeFee()));
        vo.setRefundAmount(vo.getChargebackAmount().add(vo.getReturnFee()));

        vo.setReturnAmount(BigDecimal.ZERO);
        vo.setRepairAmount(BigDecimal.ZERO);
        BigDecimal fillOut = new BigDecimal(order.getFillOutSubtotal());
        if (fillOut.compareTo(BigDecimal.ZERO) > 0) {
            vo.setReturnAmount(fillOut);
        } else if (fillOut.compareTo(BigDecimal.ZERO) < 0) {
            vo.setRepairAmount(fillOut.abs());
        }
        // 平台赔付+仓储赔付
        vo.setCompensateAmount(new BigDecimal(order.getCompensateAmountPlatform()).add(new BigDecimal(order.getCompensateAmountNode())));
        // 实付金额-退单总额-赔付金额-退回货款+需补货款
        vo.setSubTotal(vo.getPaymentAmount().add(vo.getRepairAmount())
                .subtract(vo.getRefundAmount()).subtract(vo.getCompensateAmount()).subtract(vo.getReturnAmount()));

        return vo;
    }

    /**
     * 校验查询条件并转换
     */
    private PurchaseOrder getQueryParam(PurchaseOrderQueryDTO queryDTO) {
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderCode(StringUtils.trimToNull(queryDTO.getOrderCode()));
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(7);
        map.put("statusList", ORDER_STATUS);
        map.put("memberId", UserContextHolder.get().getMemberId());
        map.put(FieldSuffix.LIKE.getName("sellerName"), StringUtils.trimToNull(queryDTO.getSellerName()));
        map.put(FieldSuffix.LIKE.getName("payAccountName"), StringUtils.trimToNull(queryDTO.getBuyer()));
        LocalDateTime endTime = LocalDateTime.ofInstant(Instant
                .ofEpochMilli(Math.min(queryDTO.getOrderEndTime(), System.currentTimeMillis())), ZoneId.systemDefault());
        long lastMonthDay = endTime.minusMonths(1).with(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Assert.isTrue(queryDTO.getOrderStartTime() >= lastMonthDay, "下单时间范围最多一个月");

        map.put(FieldSuffix.GTE.getName("createTime"), queryDTO.getOrderStartTime());
        map.put(FieldSuffix.LTE.getName("createTime"), queryDTO.getOrderEndTime());
        order.setWhereExt(map);
        return order;
    }

    private Map<Integer, String> getOrderStatusMap() {
        Map<Integer, String> map = EnumUtils.valueDesc(OrderStatusEnum.values());
        map.put(OrderStatusEnum.COMPLETED.getValue(), "已完成");
        return map;
    }

    private String getFileName() {
        return "商户采购结算报表" + DateUtils.format(System.currentTimeMillis(), "yyyyMMdd");
    }


    private static class MergeStrategy extends AbstractMergeStrategy {

        @Override
        protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
            if (1 == relativeRowIndex && 1 == cell.getColumnIndex()) {
                // 说明行合并 内容自动换行
                sheet.addMergedRegion(new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex(), 1, 16));
                cell.getCellStyle().setWrapText(true);
            }
        }
    }

    private static class RowHeightStrategy extends AbstractRowHeightStyleStrategy {

        @Override
        protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void setContentColumnHeight(Row row, int relativeRowIndex) {
            switch (relativeRowIndex) {
                case 0:
                    // 空行高度
                    row.setHeight((short) 450);
                    break;
                case 1:
                    // 说明行高度
                    row.setHeight((short) 1800);
                    break;
                default:
                    break;
            }
        }
    }

    @Getter
    private static class ExcelWriterHolder {

        private final ExcelWriter excelWriter;
        private final WriteSheet writeSheet;
        private final PurchaseOrderSumVO sum;

        public ExcelWriterHolder(ExcelWriter excelWriter, String startDate, String endDate) {
            this.excelWriter = excelWriter;
            this.writeSheet = writerSheet().build();
            this.sum = initSum(startDate, endDate);
        }

        /**
         * 初始化汇总对象
         */
        private PurchaseOrderSumVO initSum(String startDate, String endDate) {
            PurchaseOrderSumVO sumVO = new PurchaseOrderSumVO();
            sumVO.setMemberName(UserContextHolder.get().getMemberName());
            sumVO.setStartDate(startDate);
            sumVO.setEndDate(endDate);
            sumVO.setCount(0);
            sumVO.setPaymentAmount(BigDecimal.ZERO);
            sumVO.setRefundAmount(BigDecimal.ZERO);
            sumVO.setReturnAmount(BigDecimal.ZERO);
            sumVO.setRepairAmount(BigDecimal.ZERO);
            sumVO.setCompensateAmount(BigDecimal.ZERO);
            return sumVO;
        }

        public void fillData(List<PurchaseOrderVO> list) {
            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            excelWriter.fill(list, writeSheet);

            // 累加合计信息
            for (PurchaseOrderVO item : list) {
                sum.setPaymentAmount(sum.getPaymentAmount().add(item.getPaymentAmount()));
                sum.setRefundAmount(sum.getRefundAmount().add(item.getRefundAmount()));
                sum.setReturnAmount(sum.getReturnAmount().add(item.getReturnAmount()));
                sum.setRepairAmount(sum.getRepairAmount().add(item.getRepairAmount()));
                sum.setCompensateAmount(item.getCompensateAmount().add(item.getCompensateAmount()));
            }

            sum.setCount(sum.getCount() + list.size());
        }

        public void fillSum() {
            excelWriter.fill(sum, writeSheet);
            excelWriter.write(LAST_2ROW, writeSheet, WRITE_TABLE).finish();
        }
    }

}

```