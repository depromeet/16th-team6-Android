# 앗차!


디프만. 16기 6십계치킨 팀의 **앗차** 서비스 입니다.

[![Facebook](https://img.shields.io/badge/PlayStore-414141?style=flat-square&logo=googleplay&logoColor=white&link=https://play.google.com/store/apps/details?id=com.depromeet.team6&hl=ko)](https://play.google.com/store/apps/details?id=com.depromeet.team6&hl=ko)
[![instagram](https://img.shields.io/badge/instagram-E4405F?style=flat-square&logo=Instagram&logoColor=white&link=https://www.instagram.com/atcha_official/)](https://www.instagram.com/atcha_official/)

# OverView
> 막차를 자주 놓치는 경기도인들을 위한 막차알림 서비스  

![image](https://github.com/user-attachments/assets/f875c469-b3dc-4790-b35c-2833da2df21e)


- 개발 기간 : 2025.02.12 ~ (진행중)
- Android 개발자 : <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=Android&logoColor=white"> <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white">

|<img width="200" height="200" src="https://avatars.githubusercontent.com/u/72616557?v=4"/>|<img width="200" height="200" src="https://avatars.githubusercontent.com/u/122257945?v=4"/>|<img width="200" height="200" src="https://avatars.githubusercontent.com/u/75196460?v=4"/>|<img width="200" height="200" src="https://avatars.githubusercontent.com/u/70833219?v=4"/>|
|:------:|:---:|:---:|:---:|
|[정현석](https://github.com/hyuns66)|[신민석](https://github.com/t1nm1ksun)|[이지은](https://github.com/jieeeunnn) | [조윤진](https://github.com/cyjadela) |


# 핵심 기능
- 막차 정보 검색
- 집 설정 및 출발지 검색
- 홈화면 막차 도착 & 출발시간 알림
- 막차정보 제공
  - 버스 시간
  - 버스 운행정보
  - 경유 정거장
  - 막차 경로
  - 현재위치 출발시간 & 막차 탑승시간 정보
  - 실시간 버스 남은 도착시간
- 막차정보 변경 & 막차 알림설정 푸시알림
- 출발해야 하는 시간에 잠금화면 소리알람

<br/>  

# 기술 스택
- Jetpack Compose
- CleanArchitecture & MVI Pattern
  - Repository Pattern
  - Event
  - SideEffect
  - UiState
- AAC
  - NavigationGraph
  - DataStore
  - Flow
  - ViewModel
  - Coroutine
- Kakao Login
- TMap SDK
- Hilt
- FirebaseMessagingService
- OKHttp & Retrofit
- BackGroundService
- BroadCastReceiver
- Amplitude
- R8 & Proguard

<br>

# 아키텍처
![image](https://github.com/user-attachments/assets/18a90ddd-6fa2-42bd-baf8-bcef0c7b001b)

- UI -> Domain -> Data 계층간의 단방향 데이터 흐름구조를 설계 및 구현했습니다.
- 각 데이터의 흐름은 Event 로써 수집되고 ViewModel로 전달되며, 해당 과정에 Amplitude를 삽입하여 유저데이터를 수집하고 서비스 개선에 활용하였습니다.
- Data 계층의 추상화를 통해 각 Domain 계층과의 결합도를 낮췄습니다.
