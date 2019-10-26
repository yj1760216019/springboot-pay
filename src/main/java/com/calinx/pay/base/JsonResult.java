package com.calinx.pay.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


@ApiModel(
        value = "JsonResult",
        description = "统一Json返回"
)
public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(
            value = "是否成功",
            required = true,
            example = "true"
    )
    private boolean success;
    @ApiModelProperty(
            value = "消息",
            required = true
    )
    private String msg;
    @ApiModelProperty(
            value = "数据",
            required = true
    )
    private T data;

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public JsonResult(boolean success, String msg, T data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public JsonResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public static <T> JsonResult<T> success(String msg, T t) {
        return new JsonResult(true, msg, t);
    }

    public static <T> JsonResult<T> success(String msg) {
        return new JsonResult(true, msg);
    }

    public static <T> JsonResult<T> success(T t) {
        return new JsonResult(true, (String)null, t);
    }

    public static <T> JsonResult<T> fail(String msg, T t) {
        return new JsonResult(false, msg, t);
    }

    public static <T> JsonResult<T> fail(String msg) {
        return new JsonResult(false, msg);
    }

    public static <T> JsonResult<T> fail(T t) {
        return new JsonResult(false, (String)null, t);
    }


}
