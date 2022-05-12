package com.example.dmaker.exception;

import lombok.Getter;

// 에러 코드를 작성할 때 기본 근간이 되는 클래스를 Runtime Exception 으로 둔다.
@Getter
public class DMakerException extends RuntimeException {

    private DMakerErrorCode dMakerErrorCode;
    private String detailMessage;

    // 에러 코드를 하나만 받아줄 때, 일반적인 케이스라 하면 기본 코드와 메시지를 만든다.
    public DMakerException(DMakerErrorCode errorCode) {
        super(errorCode.getMessage());
        this.dMakerErrorCode = errorCode;
        this.detailMessage = errorCode.getMessage();
    }

    public DMakerException(DMakerErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.dMakerErrorCode = errorCode;
        this.detailMessage = detailMessage;
    }
}
