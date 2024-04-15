## 💊 지금이:약
올바른 약 복용을 위해 보호자와 함께 관리하는 안드로이드 어플리케이션 및 스마트 약통 서비스


### Overview

> 💡 올바른 약 복용은 “처방된 용량대로, 정확한 복용법을 준수하여, 지시된 처방기간을 제대로 지키는 것” 입니다. <ins>**_지금이:약_** 은 이를 보조하기 위해 스마트 약통과 어플을 통해 약물 복용 관리를 간편하고 효과적으로 지원하는 서비스입니다.</ins> 사용자는 정해진 약 복용 시간에 약물을 복용할 수 있도록 어플 알림 뿐만 아니라 IoT기기에서 알림을 받습니다. 또한 약물 정보, 복용 기록, 건강설문을 통한 종합적인 건강 관리가 가능합니다.



### Project Info

🗓️ 2023. 07. 04 ~ 2023. 08. 18. (총 7주)

👨‍👩‍👧‍👧 BE/Infra (BE 2명 + FE 2명 + HW 2명, 총 6명)

🏆 프로젝트 우수상

---
</br>

## ⚙️ 시스템 설계

### A. System Architecture
![architecture (1).png](<src/architecture_(1).png>)
</br>


### B. ER-Diagram
![ERD.png](src/ERD.png)
</br>

### C. Entity Diagram

![jpa-entity-diagram.png](src/jpa-entity-diagram.png)


### 📄 [D. API Document](https://www.notion.so/API-Document-46f2ea74d51e4434b07b8e1be62182a9?pvs=21)

</br>

## 🛠️ Skills

#### Server

<p>
  <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=SpringBoot&logoColor=white"/> 
  <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Conda-Forge&logoColor=white" /> 
  <img src="https://img.shields.io/badge/jpa-006600?style=flat&logo=jpa&logoColor=white"> 
  <img src="https://img.shields.io/badge/QueryDSL-0769AD?style=flate&logo=querydsl&logoColor=white"> 
  <img src="https://img.shields.io/badge/gradle-02303A?style=flat&logo=gradle&logoColor=white"> 
  <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=springsecurity&logoColor=white"/> 
  <img src="https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white"/>
</p>


#### Database
<p>
  <img src="https://img.shields.io/badge/MariaDB-003545?style=flat&logo=MariaDB&logoColor=white"> 
  <img src="https://img.shields.io/badge/redis-DC382D?style=flat&logo=redis&logoColor=white">
</p>


#### Infra
<p>
  <img src="https://img.shields.io/badge/AmazonEC2-FF9900?style=flat&logo=AmazonEC2&logoColor=white"/> 
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=Docker&logoColor=white"/> 
  <img src="https://img.shields.io/badge/Jenkins-D24939?style=flat&logo=Jenkins&logoColor=white"/> 
  <img src="https://img.shields.io/badge/Nginx-009639?style=flat&logo=Nginx&logoColor=white"/> 
  <img src="https://img.shields.io/badge/linux-FCC624?style=flat&logo=linux&logoColor=black">
</p>

#### Android
<p>
  <img src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat&logo=Android Studio&logoColor=white"/> 
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?&style=flat&logo=kotlin&logoColor=white"/>
</p>

#### Embedded
<p>
  <img src="https://img.shields.io/badge/Arduino-00878F?&style=flat&logo=arduino&logoColor=white"/> 
  <img src="https://img.shields.io/badge/Bluetooth-0082FC?&style=flat&logo=bluetooth&logoColor=white"/>
</p>

#### Collaboration
<p>
  <img src="https://img.shields.io/badge/github-181717?style=flat&logo=github&logoColor=white"> 
  <img src="https://img.shields.io/badge/jira-0052CC?style=flat&logo=jira&logoColor=white"/> 
  <img src="https://img.shields.io/badge/swagger-85EA2D?style=flat&logo=Swagger&logoColor=white"/> 
  <img src="https://img.shields.io/badge/notion-000000?style=flate&logo=notion&logoColor=white"/> 
  <img src="https://img.shields.io/badge/mattermost-0058CC?style=flat&logo=mattermost&logoColor=white"/>
</p>
</br>


## ⭐ Issue

📌 **복용 약 알림 조회 기능 에러 해결**

- 문제: 복용 약 알림 조회 시 시작, 종료 날짜에 불필요한 알림 생성
- 원인:
  1. 약 등록 시 시작 시간을 기준으로 알림 정보를 생성하는 것이 아닌, N일치에 대한 알림을 생성 → 시작 날짜에 처방시점 이전의 알림 생성

  2. 스케줄러를 활용하여 daily로 복용해야 하는 약 리스트 생성 → 하지만 종료 일자에 약 복용이 끝난 이후의 알림 생성
- 해결:
  1. 스케줄러의 활용이 아닌 약 등록 시 등록 시점을 기준으로 복용 체크 column을 복약 시점 별로 모두 생성

  2. 이후 복용 체크 column 생성을 기준으로 복용 약 알림 조회
- 비고: 서버 시간에 대한 문제 발견 → AWS 서버 시간을 국내로 변경하여 해결

</br>

📌 **카테시안 곱(Cartesian Product) 문제 해결**

- 문제: 사용자가 입력한 기간에 대한 약 복용 정보와 데일리 설문에 대한 결과를 pdf로 조회하는 과정에서 중복되는 데이터가 들어감
- 원인: 해당 날짜에 대한 1) 약 복용 데이터, 2) 건강설문을 쿼리 한번으로 조회하려고 해 카테시안 곱 발생
- 해결: 1) 약 복용 데이터, 2) 건강설문을 각각 조회 후 dto에 담아서 해결

</br>

📌 **QueryDSL 사용 이유**

- 아침, 점심, 저녁 등 8가지 시간 상태에 따라 복용하는 약을 조회하는데 이 조건만 변경되게끔 조회했기 때문에 동적쿼리가 필요하다고 생각
- 정규화된 DB로 인해 복잡한 쿼리 작성 시 JPQL보다 가독성이 높다고 생각

</br>

📌 **CI/CD를 통한 효율적인 개발 프로세스 경험**

- 문제: Docker 컨테이너 내에서 SpringBoot 애플리케이션이 빌드되지 않는 문제 발생
- 원인: Docker in Docker(DinD) 방식
- 해결:
    
    1) Docker Out of Docker (DooD) 방식 사용
    
    2) 젠킨스 도커 이미지 내에 도커를 설치하는 것이 아닌 서버 내 설치되어 있는 도커를 이용해 도커를 실행
    
- 비고:
    
    1) Nginx에 HTTPS 인증서를 설치하여 보안성 높임
    
    2) JUnit 테스트를 통해 빌드 과정에서 예상치 못한 에러를 사전에 발견하여 수정


</br>

### ⭐ Link

📌 [Notion](https://www.notion.so/10bd0fb43ca041dba207806a7fe842ed?pvs=21)

📌 [Youtube](https://www.youtube.com/watch?v=f0LNya78P78)

📌 [Presentation](https://www.miricanvas.com/v/12bdetn)

---


## 🎬 주요 기능

### 1. Member

| 안드로이드 사용자가 모두 가지고 있는 구글로그인을 통해 회원가입이 가능합니다. 회원 정보 등록시 사용자의 6가지 루틴(일어나는 시간, 아침 식사 시간, 점심 식사 시간, 저녁 식사 시간, 잠자는 시간, 식사 시간)에 대한 정보를 입력 받고, 앞으로의 복용 알림에 사용됩니다.
| 회원가입 | 회원수정 | 회원탈퇴 |
|:---:|:---:|:---:|
|<img src="src/1%20%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85.gif" width="130%" height="130%"/> | <img src="src/7-%ED%9A%8C%EC%9B%90%EC%A0%95%EB%B3%B4-%EC%88%98%EC%A0%95.gif" width="35%" height="35%"/> | <img src="src/8%20%ED%9A%8C%EC%9B%90%ED%83%88%ED%87%B4.gif" width="130%" height="130%"/> |

### 2. Prescription

| 처방전 단위로 약의 정보를 등록가능합니다. 약의 이름은 사용자가 알아보기 쉬운 이름으로 등록할 수 있습니다. 약을 등록할 때는, 8가지 루틴(일어나는 시간, 아침식사 전/후, 점심식사 전/후, 저녁식사 전/후, 잠자기전)에 해당하는 시간 설정을 하게 됩니다.
| 복약추가 |복약정보수정|복약삭제|
|:---:|:---:|:---:|
|<img src="src/2-1-1%20%EB%B3%B5%EC%95%BD%20%EC%B6%94%EA%B0%80.gif" width="65%" height="65%"/> | <img src="src/2-1-2%20%EB%B3%B5%EC%95%BD%EC%A0%95%EB%B3%B4%EC%88%98%EC%A0%95.gif" width="65%" height="65%"/> | <img src="src/2-1-3%20%EB%B3%B5%EC%95%BD%20%EC%82%AD%EC%A0%9C.gif" width="65%" height="65%"/> |

### 3. Alarm

| 사용자의 일상 루틴에 따라 약을 복용해야 하는 시간을 자동으로 계산하여 알림을 주게 됩니다.
|알림 탭 들어가서 확인|복용 알림 및 복용 확인 버튼 누르기|
|:---:|:---:|
|<img src="src/2-3-%EC%95%8C%EB%A6%BC%ED%83%AD-%EB%93%A4%EC%96%B4%EA%B0%80%EC%84%9C-%ED%99%95%EC%9D%B8.gif" width="45%" height="45%"/> | <img src="src/2-4-%EB%B3%B5%EC%9A%A9-%EC%95%8C%EB%9E%8C-%EC%9A%B8%EB%A6%AC%EA%B3%A0-%EB%B3%B5%EC%9A%A9-%EB%B2%84%ED%8A%BC-%EB%88%84%EB%A5%B4%EA%B8%B0.gif" width="45%" height="45%"/> |

### 4. Survey

| 매일 오늘의 컨디션, 보유 증상, 기타 특이사항에 대한 정보를 입력할 수 있습니다.

<p align="center"><img src="src/3-1-%EB%8D%B0%EC%9D%BC%EB%A6%AC-%EC%84%A4%EB%AC%B8.gif" width="21%" height="21%"/></p>

### 5. PDF

| 사용자가 입력한 기간의 복약 정보 및 건강 설문 정보를 조회할 수 있으며, pdf로 저장해 병원에서의 정기 진료 시 의료진에게 간편하게 보여줄 수 있습니다. 의료진은 환자의 상태에 대해 추적관찰이 가능하며 앞으로의 치료에 대한 팔로우업이 용이해집니다.
| pdf 저장 |pdf 결과|
|:---:|:---:|
|<img src="src/4-1-pdf-%EC%A0%80%EC%9E%A5.gif" width="45%" height="45%"/>| <img src="src/4-2%20%EC%A0%80%EC%9E%A5%EB%90%9C%20pdf.jpg" width="50%" height="50%"/>

### 6. Follow

| 사용자는 나의 복약 정보와 건강설문을 열람할 수 있는 가족을 등록할 수 있습니다. 가족을 추가할 때, 공개범위(전체공개, 달력만 공개) 및 내가 지정할 이름을 설정할 수 있습니다. 요청을 받은 가족 또한 공개범위, 상대방의 이름을 설정할 수 있습니다. 전체공개로 설정된 가족은 세부적인 복약 정보와 건강 설문 내용을 조회할 수 있지만, 달력 공개의 경우 복약을 어느정도 했는지 여부만 열람할 수 있습니다.
|가족등록요청|요청수락|연결끊기|
|:---:|:---:|:---:|
|<img src="src/5-1%20%EA%B0%80%EC%A1%B1%EC%8B%A0%EC%B2%AD.gif" width="65%" height="65%"/>| <img src="src/5-2%20%EC%8B%A0%EC%B2%AD%20%EB%8F%84%EC%B0%A9%20%EB%B0%8F%20%EC%8B%A0%EC%B2%AD%20%EC%88%98%EB%9D%BD.gif" width="65%" height="65%"/>| <img src="src/5-5%20%ED%8C%94%EB%A1%9C%EC%9A%B0%20%EB%81%8A%EA%B8%B0.gif" width="65%" height="65%"/> |

|                                                                     가족조회(전체공개)                                                                      |                                                               가족조회(달력공개)                                                                |
| :---------------------------------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------: |
| <img src="src/5-4-2%20%EA%B0%80%EC%A1%B1%20%EB%B3%B5%EC%95%BD%20%EC%A1%B0%ED%9A%8C(%EC%A0%84%EC%B2%B4%20%EA%B3%B5%EA%B0%9C).gif" width="45%" height="45%"/> | <img src="src/5-4-1%20%EA%B0%80%EC%A1%B1%20%EB%B3%B5%EC%95%BD%20%EC%A1%B0%ED%9A%8C(%EB%8B%AC%EB%A0%A5%EB%A7%8C).gif" width="45%" height="45%"/> |

### 7. Bluetooth

| 블루투스로 약통 등록 후 원하는 위치에 약을 보관합니다. 알람이 울릴 때, 해당하는 칸에 LED 불빛이 들어옵니다.

<p align="center"><img src="src/6-2-%EC%95%BD%ED%86%B5-%EC%84%A4%EC%A0%95.gif" width="21%" height="21%"/></p>

---

📌 [약통 디바이스 사용 설명서](https://www.notion.so/cbd8cce51bf24509b498a86e1346f098?pvs=21)

📌 [App 사용 설명서](https://www.notion.so/App-2be66b3378284d1c9259550ec60bb3d9?pvs=21)

---


### 8. 명세서

📌 [요구사항 명세서](https://www.notion.so/87b545bdcbc54d6badf6d1feee768668?pvs=21)

📌 [기능 명세서](https://www.notion.so/1d15166fe4c44dfda384e8b8e7b4edd7?pvs=21)

📌 [기술 명세서](https://www.notion.so/208c6126fcbd4d3cae42e521a640499f?pvs=21)

---

## Member

|                  김용우                   |                조현기                 |                    고범수                     |                박영서                 |                         백서영                          |                   손명주                    |
| :---------------------------------------: | :-----------------------------------: | :-------------------------------------------: | :-----------------------------------: | :-----------------------------------------------------: | :-----------------------------------------: |
|                 Front-End                 |               Front-End               |                   Back-End                    |            Back-End/Infra             |                        Embedded                         |                  Embedded                   |
| [soybean33](https://github.com/soybean33) | [chk7082](https://github.com/chk7082) | [rhqjatn2398](https://github.com/rhqjatn2398) | [ysparrk](https://github.com/ysparrk) | [rainbow00unicorn](https://github.com/rainbow00unicorn) | [sonmyungju](https://github.com/sonmyungju) |

---
