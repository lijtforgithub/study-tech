package com.ljt.study.dingtalk.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author LiJingTang
 * @date 2022-07-22 14:14
 */
@Data
@Accessors(chain = true)
public class DepartUserRequestDTO {

    /**
     * 部门ID
     */
    private String dept_id;
    /**
     * 分页查询的游标，最开始传0，后续传返回参数中的next_cursor值
     */
    private Integer cursor;

    /**
     * 分页大小
     */
    private Integer size;

    /**
     * 是否返回访问受限的员工
     */
    private Boolean contain_access_limit;


}
