package now.eyak.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date; // 설문 해당 날짜(생성시간 X)
    @OneToMany(mappedBy = "survey")
    private List<SurveyContent> surveyContents;
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder
    public Survey(List<SurveyContent> surveyContents, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.surveyContents = surveyContents;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
