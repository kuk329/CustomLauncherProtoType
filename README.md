# CustomLauncherProtoType

Android TV / STB 기기에 최적화된 커스텀 Leanback 런처입니다. 
사용자 친화적인 카드 UI, 리모컨(RCU) 포커스 이동, 날씨 정보, 설정 패널 등을 제공합니다.

## 🎯 프로젝트 목표
- Android TV 및 셋톱박스(STB)에 맞는 Leanback 스타일 홈 런처 제공
- 상단 탭 기반 콘텐츠 전환 (Content / Setting / OTT)
- 날씨 정보, 시계, 앱 카드, 설정 등 실사용 환경 고려한 TV UX 구현

---

## 🖼️ 주요 UI 구성
| 영역 | 설명 |
|------|------|
| 상단 탭 | Content / Setting / OTT 버튼 (CustomTitleView) |
| 날씨 카드 | 현재 날씨 및 온도, 위치 정보 표시 (우측은 미니 대시보드) |
| 콘텐츠 영역 | 탭에 따라 Fragment 교체 (`ContentFragment`, `OttFragment`) |
| 사이드 설정 패널 | Setting 탭 클릭 시 슬라이드 등장 (`SettingsFragment`) |
| 미니 대시보드 | 볼륨, Wi-Fi 상태, 언어 표시 (`MiniSettingFragment`) |

---

## 📂 디렉토리 구조 (상위 요약)
```text
com/example/customerlauncher/
├── common/     # 공통 유틸리티 및 상수
├── custom/     # 커스텀 뷰 (타이틀 탭 등)
├── data/       # API, Repository, 데이터 계층
├── di/         # 의존성 주입 설정 (Koin)
├── domain/     # UseCase, Entity 등 비즈니스 로직
├── ui/         # Activity, Fragment, Presenter 등 화면 UI
```

---

## 🚀 실행 방법
```bash
adb install -r app-debug.apk
adb shell am start -n com.example.customerlauncher/.MainActivity
```

---

## ⚙️ 개발 환경
- Kotlin + AndroidX
- Android 12+ (STB 펌웨어 대응)
- Leanback Library
- MVVM + StateFlow
- Koin (DI)
- Glide / Lottie / Blurry 등 UI 라이브러리

---

## 📜 라이선스
이 프로젝트는 Apache License 2.0을 따릅니다. 

---

## 🙌 기여
- 버그 수정 및 기능 제안은 Pull Request 또는 이슈 등록 부탁드립니다.
- AIDL 연동관련 부분은 별도 branch로 진행 부탁드립니다. 
