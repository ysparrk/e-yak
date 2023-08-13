package now.eyak.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import now.eyak.member.domain.Member;
import now.eyak.survey.enumeration.ChoiceStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private Member member;
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();
    @OneToMany(mappedBy = "contentStatusResult", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ContentStatusResultChoiceStatusEntity> selectedStatusChoices = new ArrayList<>();

    @Builder
    public ContentStatusResult(Long id, SurveyContent surveyContent, Member member, LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
        this.id = id;
        this.surveyContent = surveyContent;
        this.member = member;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void addChoiceStatusEntity(ContentStatusResultChoiceStatusEntity contentStatusResultChoiceStatusEntity) {
        selectedStatusChoices.add(contentStatusResultChoiceStatusEntity);
    }

    // update
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentStatusResult that = (ContentStatusResult) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getSurveyContent(), that.getSurveyContent()) && Objects.equals(getMember(), that.getMember()) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getUpdatedAt(), that.getUpdatedAt()) && Objects.equals(getSelectedStatusChoices(), that.getSelectedStatusChoices());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSurveyContent(), getMember(), getCreatedAt(), getUpdatedAt(), getSelectedStatusChoices());
    }
}
