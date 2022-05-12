package com.example.dmaker.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 이넘에서 message 를 받아오기 위해서 Getter
// 이넘 안에 메세지를 넣어주기 위해서 AllArgsConstructor
@Getter
@AllArgsConstructor
public enum DMakerErrorCode {
    NO_DEVELOPER("해당되는 개발자가 없습니다"),
    DUPLICATED_MEMBER_ID("MemberId가 중복되는 개발자가 있습니다"),
    LEVEL_EXPERIENCE_YEARS_NOT_MATCHED("개발자 레벨과 연차가 맞지 않습니다"),

    // 예외의 예외 같은 느낌 진짜 알 수 없는 예외가 발생했을 때 사용할 것
    INTERNAL_SERVER_ERROR("서버에 오류가 발생했습니다"),
    INVALID_REQUEST("잘못된 요청입니다");
    private final String message;
}
