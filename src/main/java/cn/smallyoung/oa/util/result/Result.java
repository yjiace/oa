package cn.smallyoung.oa.util.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author smallyoung
 */
@Getter
@ApiModel("通用接口返回对象")
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -3937755350175676600L;

    @ApiModelProperty(required = true, notes = "状态码", example = "200")
    private Integer code;

    @ApiModelProperty(required = true, notes = "说明", example = "success")
    private String message;

    @ApiModelProperty(required = true, notes = "返回的业务数据")
    private T data;

    private Result() { }

    private Result(ResultStatus resultStatus, T data) {
        this.code = resultStatus.getCode();
        this.message = resultStatus.getMessage();
        this.data = data;
    }

    public static <T> Result<T> result(ResultStatus resultStatus, T data) {
        return new Result<T>(resultStatus, data);
    }

    public static Result<Void> success() {
        return result(ResultStatus.SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        return result(ResultStatus.SUCCESS, data);
    }

    public static Result<Void> failure() {
        return result(ResultStatus.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> Result<T> failure(T data) {
        return result(ResultStatus.INTERNAL_SERVER_ERROR, data);
    }


}
