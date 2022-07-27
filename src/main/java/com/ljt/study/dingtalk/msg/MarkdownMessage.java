package com.ljt.study.dingtalk.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author LiJingTang
 * @date 2022-07-25 9:49
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MarkdownMessage extends DingTalkMessage {

    public MarkdownMessage() {
        super(MsgType.MARKDOWN.getType());
    }

    private Markdown markdown;

    @Data
    public static class Markdown {

        private String title;
        private String text;

    }

}
