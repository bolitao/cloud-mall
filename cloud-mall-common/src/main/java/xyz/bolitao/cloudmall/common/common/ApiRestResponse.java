package xyz.bolitao.cloudmall.common.common;

import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;

/**
 * common REST response
 *
 * @author boli.tao
 */
public class ApiRestResponse<T> {
    private Integer status;
    private String msg;
    private T data;

    private static final int OK_CODE = 10000;
    private static final String OK_MSG = "SUCCESS";

    public ApiRestResponse(Integer status) {
        this.status = status;
    }

    public ApiRestResponse(String msg) {
        this.msg = msg;
    }

    public ApiRestResponse(T data) {
        this.data = data;
    }

    public ApiRestResponse(Integer status, T data) {
        this.status = status;
        this.data = data;
    }

    public ApiRestResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ApiRestResponse(String msg, T data) {
        this.msg = msg;
        this.data = data;
    }

    public ApiRestResponse(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public ApiRestResponse() {
        this(OK_CODE, OK_MSG);
    }

    public static <T> ApiRestResponse<T> success() {
        return new ApiRestResponse<>();
    }

    public static <T> ApiRestResponse<T> success(T result) {
        return new ApiRestResponse<>(OK_CODE, OK_MSG, result);
    }

    public static <T> ApiRestResponse<T> error(Integer code, String msg) {
        return new ApiRestResponse<>(code, msg);
    }

    public static <T> ApiRestResponse<T> error(ImoocMallExceptionEnum exceptionEnum) {
        return new ApiRestResponse<>(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static int getOkCode() {
        return OK_CODE;
    }

    public static String getOkMsg() {
        return OK_MSG;
    }
}
