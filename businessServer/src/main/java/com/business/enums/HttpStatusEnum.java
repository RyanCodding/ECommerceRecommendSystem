package com.business.enums;

import lombok.Getter;

@Getter
public enum HttpStatusEnum {
    /**
     * http状态码枚举所有状态码注解
     */

    USER_TAKEN(512, "用户名已经被注册"),

    SUCCESS(200, "SUCCESS");

    private int code;
    private String msg;

    HttpStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }

}
