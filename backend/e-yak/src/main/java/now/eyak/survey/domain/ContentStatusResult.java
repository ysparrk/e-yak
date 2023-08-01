package now.eyak.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;
import now.eyak.survey.enumeration.ChoiceEmotion;
import now.eyak.survey.enumeration.ChoiceStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContentStatusResult {
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

    @ElementCollection(targetClass = ChoiceStatus.class)
    @Enumerated(EnumType.STRING)
    private List<ChoiceStatus> selectedStatusChoices = new ArrayList<>();

    @Builder
    public ContentStatusResult(SurveyContent surveyContent, Member member, LocalDateTime createdAt, LocalDateTime updatedAt, List<ChoiceStatus> selectedStatusChoices) {
        this.surveyContent = surveyContent;
        this.member = member;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.selectedStatusChoices = selectedStatusChoices;
    }
}
