package com.ljt.study.dingtalk.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author LiJingTang
 * @date 2022-07-22 16:55
 */
@Data
@Accessors(chain = true)
public class ChatUpdateRequestDTO {

    /**
     * 群名称，长度限制为1~20个字符
     */
    private String chatid;
    /**
     * 群成员列表，每次最多支持40人，群人数上限为1000。
     */
    private List<String> add_useridlist;

}
