package com.ljt.study.dingtalk.exception;

/**
 * @author LiJingTang
 * @date 2022-07-25 13:50
 */
public class DingTalkException extends RuntimeException {

    private static final long serialVersionUID = -3407241558946293622L;

    public DingTalkException(String message) {
        super(message);
    }

}
