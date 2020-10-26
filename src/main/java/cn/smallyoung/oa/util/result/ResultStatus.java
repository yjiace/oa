package cn.smallyoung.oa.util.result;

import lombok.Getter;

/**
 * @author smallyoung
 */

@Getter
public enum ResultStatus {

    /**
     * 请求成功
     */
    SUCCESS(200, "OK"),

    /**
     *
     */
    BAD_REQUEST(400, "Bad Request"),

    /**
     * 系统错误
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");


    /** 业务异常码 */
    private final Integer code;
    /** 业务异常信息描述 */
    private final String message;

    ResultStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
