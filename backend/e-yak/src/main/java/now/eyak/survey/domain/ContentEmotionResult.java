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
import java.util.Objects;

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

    // update
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentEmotionResult that = (ContentEmotionResult) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getSurveyContent(), that.getSurveyContent()) && Objects.equals(getMember(), that.getMember()) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getUpdatedAt(), that.getUpdatedAt()) && getChoiceEmotion() == that.getChoiceEmotion();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSurveyContent(), getMember(), getCreatedAt(), getUpdatedAt(), getChoiceEmotion());
    }
}
