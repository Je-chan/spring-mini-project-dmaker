package com.example.dmaker.service;

import com.example.dmaker.dto.CreateDeveloper;
import com.example.dmaker.entity.Developer;
import com.example.dmaker.exception.DMakerErrorCode;
import com.example.dmaker.exception.DMakerException;
import com.example.dmaker.repository.DeveloperRepository;
import com.example.dmaker.type.DeveloperLevel;
import com.example.dmaker.type.DeveloperSkillType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;

import java.util.Optional;

import static com.example.dmaker.exception.DMakerErrorCode.DUPLICATED_MEMBER_ID;
import static com.example.dmaker.exception.DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED;

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
    public void createDeveloper(CreateDeveloper.Request request) {

        validateCreateDeveloperRequest(request);

        Developer developer = Developer.builder()
                .developerLevel(DeveloperLevel.JUNIOR)
                .developerSkillType(DeveloperSkillType.FRONT_END)
                .experienceYear(1)
                .name("Je")
                .age(20)
                .build();

        developerRepository.save(developer);
    }

    private void validateCreateDeveloperRequest(CreateDeveloper.Request request) {
        // buisness validation 을 수행하

        DeveloperLevel developerLevel = request.getDeveloperLevel();
        Integer experienceYears = request.getExperienceYears();

        if(developerLevel == DeveloperLevel.SENIOR
            && experienceYears < 10) {

            // 예외를 던질 때는 다양한 Exception 들을 날릴 수 있지만, 이렇게 커스텀 Exception 날려주는 게 좋다.
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }

        if(developerLevel == DeveloperLevel.JUNGNIOR
            && (experienceYears < 4 || experienceYears > 10)) {
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }

        if(developerLevel == DeveloperLevel.JUNIOR && experienceYears > 4) {
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }

        // 원래라면 아래와 같이 하나하나 코드를 작업해줘야 했지만 자바 8 이후부터는 주석 해제된 코드처럼 사용할 수 있다.
//        Optional<Developer> developer = developerRepository.findByMemberId(request.getMemberId());
//        if(developer.isPresent()) throw new DMakerException(DUPLICATED_MEMBER_ID);

        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMBER_ID);
                }));
    }


    // 위의 트랜잭션은 사실 아래의 코드와 같이 작동한다.
    // em 은 추상화된 db라고 생각하면 된다
    private final EntityManager em;

    @Transactional
    public void createDeveloperTransaction () {
        EntityTransaction transaction = em.getTransaction();

        try{
            // 트랜잭션의 시작 지점
            transaction.begin();

            // [1] 비즈니스 로직 시작점
            Developer developer = Developer.builder()
                    .developerLevel(DeveloperLevel.JUNIOR)
                    .developerSkillType(DeveloperSkillType.FRONT_END)
                    .experienceYear(1)
                    .name("Je")
                    .age(20)
                    .build();

            //  db와 관련된 일련의 작업을 수행한다.

            /* A -> B 1만원 송금 이라고 가정*/

            // A 계좌에서 1만원을 줄임
            developerRepository.save(developer);
            // B 계좌에서 1만원을 늘림
            developerRepository.save(developer);

            // [2] 비즈니스 로직 끝나는 점
            // 커밋을 한다는 건 작업 하나를 마친다는 의미
            transaction.commit();
        } catch(Exception e) {
            // 만약 작업 하나라도 실패한 경우 전체를 rollback 한다
            transaction.rollback();
            throw e;
        }

        // 현재 트랜잭션은 공통된 구현부가 존재한다
        // [1] 시작점 [2] 종료점 [3] 에러
        // 이런 것들을 AOP 로 구현 => 그게 Transactional 어노테이션.
    }
}
