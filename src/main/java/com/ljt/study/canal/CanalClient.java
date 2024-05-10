package com.ljt.study.canal;

import cn.hutool.db.meta.JdbcType;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author LiJingTang
 * @date 2020-12-03 20:08
 */
@Slf4j
class CanalClient {

    private static volatile boolean run = true;

    public static void main(String[] args) throws InterruptedException {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("127.0.0.1", 11111), "standalone-zk", "root", "admin");
//        CanalConnector connector = CanalConnectors.newClusterConnector("127.0.0.1:2181", "cluster-zk", "root", "admin");
        connector.connect();
        // "study-canal\\.test1,study-canal\\.test2"
        connector.subscribe();

        try {
            int batchSize = 1000;
            connector.rollback();
            log.info("开始监听...");

            while (run) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(batchSize);
                long batchId = message.getId();
                List<Entry> entryList = message.getEntries();
                if (batchId == -1 || CollectionUtils.isEmpty(entryList)) {
                    TimeUnit.SECONDS.sleep(5);
                } else {
                    printEntry(entryList);
                }

                // 提交确认 如果需要回滚数据 connector.rollback(batchId)
                connector.ack(batchId);
            }
        } finally {
            connector.disconnect();
        }
    }

    private static void printEntry(List<Entry> entryList) {
        for (Entry entry : entryList) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChange;
            try {
                rowChange = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                log.error("转换数据异常, data:" + entry, e);
                return;
            }

            EventType eventType = rowChange.getEventType();
            Header header = entry.getHeader();
            log.info("数据库[{}] 表[{}] 操作[{}]", header.getSchemaName(), header.getTableName(), eventType);

            for (RowData rowData : rowChange.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                } else {
                    log.info("修改前");
                    printColumn(rowData.getBeforeColumnsList());
                    log.info("修改后");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private static void printColumn(List<Column> columns) {
        for (Column column : columns) {
            System.out.println(String.format("%s[%s => %s]%s => %s", column.getName(), column.getMysqlType(), JdbcType.valueOf(column.getSqlType()), column.getValue(), column.getUpdated()));
        }
    }

}
