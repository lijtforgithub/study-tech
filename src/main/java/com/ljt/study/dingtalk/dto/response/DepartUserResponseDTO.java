package com.ljt.study.dingtalk.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author LiJingTang
 * @date 2022-07-22 11:23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DepartUserResponseDTO extends BaseResponseDTO {

    /**
     * 成员列表
     */
    private Result result;


    @Data
    public static class Result {

        /**
         * 是否还有更多的数据
         */
        private Boolean hasMore;
        /**
         * 下一次分页的游标
         */
        private Integer next_cursor;

        private List<User> list;

    }


    @Data
    public static class User {

        /**
         * 用户的userId
         */
        private String userid;
        /**
         * 用户姓名
         */
        private String name;
        /**
         * 手机号码
         */
        private String mobile;

    }

}
