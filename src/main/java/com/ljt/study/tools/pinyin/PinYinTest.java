package com.ljt.study.tools.pinyin;

import com.github.promeg.pinyinhelper.Pinyin;
import com.hankcs.hanlp.HanLP;
import org.junit.jupiter.api.Test;

/**
 * @author jtli3
 * @date 2022-01-14 15:15
 */
class PinYinTest {

    @Test
    void test() throws InterruptedException {
        String text = "科大讯飞 绿 女 訊飛醫療 盛饭 茂盛 几重天 重几斤  陹砼嘦嫑燊棽顕両屃  (三人行 银行) （倔强 小强） 口腔及咽喉分泌物";
        System.out.println(hanLP(text));
        System.out.println(pinyin(text));
//        TimeUnit.MINUTES.sleep(5);

//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 1000; i++) {
//            hanLP(text);
//        }
//        System.out.println(System.currentTimeMillis() - start);
    }

    private String hanLP(String text) {
        return HanLP.convertToPinyinString(text, "", false);
    }

    private String pinyin(String text) {
        return Pinyin.toPinyin(text, " ").toLowerCase();
    }

}
