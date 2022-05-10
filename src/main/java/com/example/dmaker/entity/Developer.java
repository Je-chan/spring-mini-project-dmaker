package com.example.dmaker.entity;

import com.example.dmaker.type.DeveloperLevel;
import com.example.dmaker.type.DeveloperSkillType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// 아래의 코드가 있어야 Auditing 이 제대로 된다
@EntityListeners(AuditingEntityListener.class)
public class Developer {

    // @Entity 에 따른 규약에 맞춰서 property 를 만들어야 한다
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    // Enum 을 만드는 어노테이션, Enumerated
    @Enumerated(EnumType.STRING)
    private DeveloperLevel developerLevel;

    @Enumerated(EnumType.STRING)
    private DeveloperSkillType developerSkillType;

    private Integer experienceYear;
    private String memberId;
    private String name;
    private Integer age;

    // 자동으로 생성 시점과 수정 시점을 넣어주기 위해서는 main 에서 EnableJpaAuditing 을 넣어준다
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
