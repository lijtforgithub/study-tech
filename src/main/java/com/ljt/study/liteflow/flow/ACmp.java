package com.ljt.study.liteflow.flow;

import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author LiJingTang
 * @date 2023-05-12 15:27
 */
@Slf4j
@Component("a")
public class ACmp extends NodeComponent {

    @Override
    public void process() throws Exception {
        log.info("Aa");
    }

}
