package now.eyak.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;
import now.eyak.survey.enumeration.ChoiceEmotion;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContentEmotionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SurveyContent surveyContent;
    @ManyToOne
    private Member member;
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ChoiceEmotion choiceEmotion;

    @Builder
    public ContentEmotionResult(SurveyContent surveyContent, Member member, LocalDateTime createdAt, LocalDateTime updatedAt, ChoiceEmotion choiceEmotion) {
        this.surveyContent = surveyContent;
        this.member = member;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.choiceEmotion = choiceEmotion;
    }
}
