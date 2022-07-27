package com.ljt.study.dingtalk.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户响应消息
 *
 * @author LiJingTang
 * @date 2022-07-22 11:21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DepartListResponseDTO extends BaseResponseDTO {

    private List<Depart> result;


    @Data
    public static class Depart {

        /**
         * 部门ID
         */
        private String dept_id;
        /**
         * 部门名称
         */
        private String name;
        /**
         * 父部门ID
         */
        private String parent_id;

    }

}

