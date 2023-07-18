package now.eyak.member.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import now.eyak.dailycondition.domain.Survey;
import now.eyak.prescription.domain.Prescription;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nickname;
//    private String phoneNumber; // TODO: 수집 정보 결정
//    private String baseAddress;
//    private String detailAddress;
    private LocalTime wakeTime;
    private LocalTime breakfastTime;
    private LocalTime lunchTime;
    private LocalTime dinnerTime;
    private LocalTime bedTime;
    private LocalTime eatingDuration;
//    private String specifics; // 특이사항 줄 글로
//    private String supplements; // 영양제등을 줄 글로
    @OneToMany(mappedBy = "member")
    private List<Prescription> prescriptions = new ArrayList<>();
    @OneToMany(mappedBy = "member")
    private List<Survey> surveys = new ArrayList<>();
}
