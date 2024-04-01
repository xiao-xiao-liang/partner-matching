package com.liang.usercenter.exception;

import com.liang.usercenter.common.ErrorCode;

/**
 * 自定义业务异常类
 */
public class BusinessException extends RuntimeException {
    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = message;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
