package com.example.dmaker.service;

import com.example.dmaker.code.StatusCode;
import com.example.dmaker.dto.*;
import com.example.dmaker.entity.Developer;
import com.example.dmaker.entity.RetiredDeveloper;
import com.example.dmaker.exception.DMakerException;
import com.example.dmaker.repository.DeveloperRepository;
import com.example.dmaker.repository.RetiredDeveloperRepository;
import com.example.dmaker.type.DeveloperLevel;
import com.example.dmaker.type.DeveloperSkillType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.dmaker.exception.DMakerErrorCode.*;

@Slf4j
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
    private final RetiredDeveloperRepository retiredDeveloperRepository;

    @Transactional
    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request) {

        validateCreateDeveloperRequest(request);

        Developer developer = Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYear(request.getExperienceYears())
                .memberId(request.getMemberId())
                .statusCode(StatusCode.EMPLOYED)
                .name(request.getName())
                .age(request.getAge())
                .build();

        developerRepository.save(developer);

        // Response DTO 를 만들 때는 developer 를 생성한 직후에 그 developer 의 entity 로 만들어주기에 강한 결합을 하게 된다
        // 그럴 때는 developer 받아서 return 해주는 static 메소드를 만들어주는 것이 현명한 방법이 된다
//        return CreateDeveloper.Response();
        return CreateDeveloper.Response.fromEntity(developer);

    }

    public DeveloperValidationDto validateCreateDeveloperRequest(CreateDeveloper.Request request) {

        DeveloperValidationDto developerValidationDto = null;

        DeveloperLevel developerLevel = request.getDeveloperLevel();
        Integer experienceYears = request.getExperienceYears();

        validateDeveloperLevel(developerLevel, experienceYears);

        // 원래라면 아래와 같이 하나하나 코드를 작업해줘야 했지만 자바 8 이후부터는 주석 해제된 코드처럼 사용할 수 있다.
//        Optional<Developer> developer = developerRepository.findByMemberId(request.getMemberId());
//        if(developer.isPresent()) throw new DMakerException(DUPLICATED_MEMBER_ID);

        try{
            if(developerRepository.findByMemberId(request.getMemberId()).isPresent()) {
                developerValidationDto = new DeveloperValidationDto(
                        DUPLICATED_MEMBER_ID,
                        DUPLICATED_MEMBER_ID.getMessage()
                );
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            developerValidationDto = new DeveloperValidationDto(
                    INTERNAL_SERVER_ERROR,
                    INTERNAL_SERVER_ERROR.getMessage()
            );
        }

//                .ifPresent((developer -> {
//                    throw new DMakerException(DUPLICATED_MEMBER_ID);
//                }));

        // Internal Servcer Error 를 확인해보기 위한 코드
        // throw new ArrayIndexOutOfBoundsException();

        return developerValidationDto;
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

    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .map(DeveloperDetailDto::fromEntity)
                // orElseThrow 는 null 값 나왔을 때의 예외 처리
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));

    }

    @Transactional
    public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {
        validateEditDeveloperRequest(request, memberId);

        Developer developer = developerRepository.findByMemberId(memberId).orElseThrow(
                () -> new DMakerException(NO_DEVELOPER)
        );

        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYear(request.getExperienceYears());

        return DeveloperDetailDto.fromEntity(developer);
    }

    private void validateEditDeveloperRequest(EditDeveloper.Request request, String memberId) {

        DeveloperLevel developerLevel = request.getDeveloperLevel();
        Integer experienceYears = request.getExperienceYears();

        validateDeveloperLevel(developerLevel, experienceYears);


    }

    private DeveloperValidationDto validateDeveloperLevel(DeveloperLevel developerLevel, Integer experienceYears) {
        if(developerLevel == DeveloperLevel.SENIOR
                && experienceYears < 10) {
            return new DeveloperValidationDto(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED,
                    LEVEL_EXPERIENCE_YEARS_NOT_MATCHED.getMessage());
            // 예외를 던질 때는 다양한 Exception 들을 날릴 수 있지만, 이렇게 커스텀 Exception 날려주는 게 좋다.
//            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }

//        if(developerLevel == DeveloperLevel.JUNGNIOR
//                && (experienceYears < 4 || experienceYears > 10)) {
//            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
//        }
//        if(developerLevel == DeveloperLevel.JUNIOR && experienceYears > 4) {
//            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
//        }
    }

    @Transactional
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        // EMPLOYED -> RETIRED
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
        developer.setStatusCode(StatusCode.RETIRED);

        // save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(memberId)
                .name(developer.getName())
                .build();

        retiredDeveloperRepository.save(retiredDeveloper);

        return DeveloperDetailDto.fromEntity(developer);
    }
}
