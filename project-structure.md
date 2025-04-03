# 📁 Project Structure

이 문서는 `CustomLauncherProtoType`의 전체 패키지 구조와 각 디렉토리의 역할을 설명합니다.

---

## 📦 `com.example.customerlauncher`

| 디렉토리 | 설명 |
|----------|------|
| `common/` | 공통 상수, 확장 함수, 유틸리티 클래스 (예: FocusUtils, DateFormatter) |
| `custom/` | 커스텀 뷰 클래스 (예: `CustomTitleView`, 포커스 애니메이션 탭) |
| `data/` | Repository, API 통신, DataSource 등 데이터 처리 계층 |
| `di/` | 의존성 주입(DI) 모듈 (Koin 설정) |
| `domain/` | Entity, UseCase 등 비즈니스 로직 처리 영역 |
| `ui/` | 화면 구성 관련 클래스 (Activity, Fragment, Presenter 등) |

---

## 📁 주요 디렉토리 상세

### `ui/`
| 파일/디렉토리 | 설명 |
|----------------|------|
| `MainActivity.kt` | 앱의 진입점, Fragment 교체 처리 담당 |
| `MainFragment.kt` | 상단 탭, 날씨, 콘텐츠 영역 등 전체 UI 제어 |
| `ContentFragment.kt` | 영상 카드 UI (`BrowseSupportFragment` 기반) |
| `OttFragment.kt` | OTT 앱 리스트 UI (AppCardPresenter 사용) |
| `MiniSettingFragment.kt` | 날씨 옆 미니 설정 위젯 (볼륨, Wi-Fi 등) |
| `SettingsFragment.kt` | Leanback 설정 패널, 사이드 슬라이드 방식 |

### `data/`
| 디렉토리/파일 | 설명 |
|----------------|------|
| `repository/` | WeatherRepository 등 데이터 소스 연결 |
| `network/` | Retrofit API 정의, DTO 클래스 등 포함 |

### `domain/`
| 구성 요소 | 설명 |
|--------------|------|
| `UseCase` | 날씨 정보 요청, 앱 목록 가져오기 등 도메인 액션 |
| `Entity` | 날씨 정보, 앱 정보 도메인 모델 |

### `custom/`
| 구성 요소 | 설명 |
|--------------|------|
| `CustomTitleView.kt` | 상단 탭 버튼 UI 및 포커스 이동 커스텀 뷰 |

---

## 🧱 아키텍처 구조 (요약)
```
 UI ─────────────┐
                 │
 domain <────── data <────── network / local
```
- MVVM + Clean Architecture 일부 차용
- Fragment 단위로 분리된 구조 + Leanback Presenter 패턴 적용

---
