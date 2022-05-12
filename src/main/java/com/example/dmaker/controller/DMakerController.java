package com.example.dmaker.controller;

import com.example.dmaker.dto.CreateDeveloper;
import com.example.dmaker.dto.DeveloperDetailDto;
import com.example.dmaker.dto.DeveloperDto;
import com.example.dmaker.service.DMakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

// 실제로 스프링에서 동작할 Bean 으로 등록
@RestController
// Controller(Component 와 거의 동일. Bean 등록) + ResponseBody(return 할 때, JSON 으로 응답을 내려준다)
@Slf4j
@RequiredArgsConstructor
public class DMakerController {
    private final DMakerService dMakerService;

    // API 응답으로 Entity (Developer) 를 그대로 내려주는 것은 안티 패턴
    // DTO 를 통해서 Entity 와 응답 내려주는 것을 서로 분리 해주는 것이 매우 좋은 방식이 된다.
    @GetMapping("/developers")
    public List<DeveloperDto> getAllDevelopers() {
        log.info("GET /developers HTTP/1.1");

        return dMakerService.getAllDevelopers();
    }

    @GetMapping("/developers/{memberId}")
    public DeveloperDetailDto getDeveloperDetail(
            @PathVariable String memberId
    ) {
        log.info("GET /developers HTTP/1.1");

        return dMakerService.getDeveloperDetail(memberId);

    }

    @PostMapping("/create-developer")
    public CreateDeveloper.Response createAllDeveloper(
            @Valid @RequestBody CreateDeveloper.Request request
    ) {

        log.info("request : {}", request);

        return dMakerService.createDeveloper(request);
    }
}
