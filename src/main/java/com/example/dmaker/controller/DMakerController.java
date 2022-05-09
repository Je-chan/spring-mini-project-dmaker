package com.example.dmaker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

// 실제로 스프링에서 동작할 Bean 으로 등록
@RestController // Controller(Component 와 거의 동일. Bean 등록) + ResponseBody(return 할 때, JSON 으로 응답을 내려준다)
@Slf4j
public class DMakerController {

    @GetMapping("/developers")
    public List<String> getAllDevelopers() {
        log.info("GET /developers HTTP/1.1");

        return Arrays.asList("je", "Liebe", "Bono");
    }
}
