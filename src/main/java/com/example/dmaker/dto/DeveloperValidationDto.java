package com.example.dmaker.dto;


import com.example.dmaker.entity.Developer;
import com.example.dmaker.exception.DMakerErrorCode;
import com.example.dmaker.type.DeveloperLevel;
import com.example.dmaker.type.DeveloperSkillType;
import lombok.*;

// 응답으로 내려줄 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeveloperValidationDto {

    private DMakerErrorCode errorCode;
    private String errorMsg;

}
