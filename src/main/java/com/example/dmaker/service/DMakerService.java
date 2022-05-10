package com.example.dmaker.service;

import com.example.dmaker.entity.Developer;
import com.example.dmaker.repository.DeveloperRepository;
import com.example.dmaker.type.DeveloperLevel;
import com.example.dmaker.type.DeveloperSkillType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class DMakerService {

    // RequiredArgsConstuctor 를 사용하면 DeveloperRepository interface 를 자동으로 injection 해준다
    /**
     * @Autowired
     * @Inject
     *
     * 서비스 코드가 어노테이션에 종속적으로 만들어지는 문제가 있음.
     * 그 이후에 만들어진 방식이 생성자에 주입을 만드는 방식이 테스트에 용이하다고 만들었다
     * 생성자를 계속 만드는 것이 굉장히 고통스러움
     * 그래서 final 로 인젝션 받는 값의 property 를 설정하고 @RequiredArgsConstructor 를 사용하면 자동 Injection
     */

    private final DeveloperRepository developerRepository;

    @Transactional
    public void createDeveloper () {
        Developer developer = Developer.builder()
                .developerLevel(DeveloperLevel.JUNIOR)
                .developerSkillType(DeveloperSkillType.FRONT_END)
                .experienceYear(1)
                .name("Je")
                .age(20)
                .build();

        developerRepository.save(developer);
    }
}
