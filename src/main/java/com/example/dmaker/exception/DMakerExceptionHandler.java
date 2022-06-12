package com.example.dmaker.exception;

import com.example.dmaker.dto.DMakerErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static com.example.dmaker.exception.DMakerErrorCode.INTERNAL_SERVER_ERROR;
import static com.example.dmaker.exception.DMakerErrorCode.INVALID_REQUEST;

// Bean 으로 등록할 수 있는 어노테이션
// DMaker 뿐만 아니라 다른 곳에서도 동일한 Exception 으로 처리해줄 수 있다
// 이것으로 도저히 처리할 수 없는 경우에는 Controller 에서 따로 처리해줘야 한다. 마치 HttpRequestMethodNotSupportException 을 사용
@Slf4j
@RestControllerAdvice
public class DMakerExceptionHandler {

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(DMakerException.class)
    public DMakerErrorResponse handleException(DMakerException e,
                                               HttpServletRequest request) {
        log.error("errorCode: {}, url: {}, message: {}", e.getDMakerErrorCode(), request.getRequestURI(), e.getDetailMessage());

        return DMakerErrorResponse.builder()
                .errorCode(e.getDMakerErrorCode())
                .errorMessage(e.getDetailMessage())
                .build();

    }

    @ExceptionHandler(value = {
            HttpRequestMethodNotSupportedException.class,
            MethodArgumentNotValidException.class
            // 상정하지 못한 에러가 있는 경우가 있으므로 그 때는 Exception.class 로 날릴 수는 있지만 추적해서 상세하게 내용을 담아주는 것이 좋다
    })
    public DMakerErrorResponse handleBadRequest(
            Exception e, HttpServletRequest request
    ) {

        log.error("url: {}, message: {}", request.getRequestURI(), e.getMessage());

        return DMakerErrorResponse.builder()
                .errorCode(INVALID_REQUEST)
                .errorMessage(INVALID_REQUEST.getMessage())
                .build();

    }


    @ExceptionHandler(value = {
            // 상정하지 못한 에러가 있는 경우가 있으므로 그 때는 Exception.class 로 날릴 수는 있지만 추적해서 상세하게 내용을 담아주는 것이 좋다
            Exception.class
    })
    public DMakerErrorResponse handleException(
            Exception e, HttpServletRequest request
    ) {

        log.error("url: {}, message: {}", request.getRequestURI(), e.getMessage());

        return DMakerErrorResponse.builder()
                .errorCode(INTERNAL_SERVER_ERROR)
                .errorMessage(INTERNAL_SERVER_ERROR.getMessage())
                .build();

    }
}
