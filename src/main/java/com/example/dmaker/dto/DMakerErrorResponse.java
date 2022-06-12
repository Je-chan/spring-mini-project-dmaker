package com.example.dmaker.dto;

import com.example.dmaker.exception.DMakerErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DMakerErrorResponse {

    // API 가 실패하는 경우에는 공통의 DTO 를 만들어서 사용한다
    private DMakerErrorCode errorCode;
    private String errorMessage;
}
