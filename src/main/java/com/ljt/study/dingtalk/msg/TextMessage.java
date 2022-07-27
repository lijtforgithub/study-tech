package com.ljt.study.dingtalk.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author LiJingTang
 * @date 2022-07-25 9:30
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TextMessage extends DingTalkMessage {

    public TextMessage() {
        super(MsgType.TEXT.getType());
    }

    private Text text;

    @Data
    public static class Text {

        private String content;

    }

}
