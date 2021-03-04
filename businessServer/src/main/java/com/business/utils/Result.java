package com.business.utils;

import com.business.enums.HttpStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 * date: 2021/3/1 19:33
 *
 * @author hongjun
 */
@Data
public class Result<T> implements Serializable  {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回信息描述
     */
    private String msg;

    /**
     * 返回详细信息
     */
    private T data;

    public static <T> Result<T> success(String message,T data){
        Result result = new Result();
        result.code(HttpStatusEnum.SUCCESS.getCode()).msg(message).data(data);
        return result;
    }

    public static <T> Result<T> success(T data){
        return success(HttpStatusEnum.SUCCESS.getMsg(),data);
    }

    public static <T> Result<T> success(){
        return success(null);
    }

    public static <T> Result<T> error(int code,String msg,T data){
        Result result = new Result();
        result.code(code).msg(msg).data(data);
        return result;
    }

    public static <T> Result<T> error(int code,String msg){
        return error(code,msg,null);
    }

    public static <T> Result<T> error(HttpStatusEnum responseCodeEnum){
        return error(responseCodeEnum.getCode(), responseCodeEnum.getMsg());
    }

    public static <T> Result<T> error(HttpStatusEnum responseCodeEnum, T data){
        Result result = new Result();
        result.code(responseCodeEnum.getCode()).msg(responseCodeEnum.getMsg()).data(data);
        return result;
    }

    public Result code(int code) {
        this.code = code;
        return this;
    }

    public Result data(T data) {
        this.data = data;
        return this;
    }

    public Result msg(String msg) {
        this.msg = msg;
        return this;
    }

}
