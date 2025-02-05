# ImageUploader

### 개요
```Android에서 SOOP 웹사이트의 이미지를 로딩하고 RecyclerView를 활용해 화면에 표시하는 앱입니다. 사용자가 버튼을 클릭하면 홈페이지에 http 요청으로 API를 직접 가져온 후 이미지를 리스트로 출력합니다.```

### 실행 방법
```
1. 레포지토리 클론 : git clone https://github.com/kinggunnn/ImageUploader.git
2. Android Studio에서 프로젝트 열기
3. 필요한 라이브러리 동기화
4. 실행
```

### 사용한 기술 스택
```
개발환경
언어 : Kotlin
IDE : Android Studio

라이브러리 및 기술
네트워크 요청: OkHttp
이미지 로딩 및 캐싱: Coil 3
비동기 처리: 코루틴 (Coroutines)
UI: RecyclerView, Material Components
JSON 파싱: org.json.JSONObject

```

### 구현 기능
```
 이미지 로딩 및 표시
 URL에서 이미지 목록 가져오기 (API 호출)
 RecyclerView를 사용한 리스트 UI 구현
 Grid / List 형식으로 동적 레이아웃 변경

 성능 최적화
 Coil 3를 활용한 메모리/디스크 캐싱
 네트워크 요청 최적화 (OkHttp 사용)
 비동기 처리 (코루틴 활용)

 기타 기능
 버튼 클릭 시 이미지 목록 새로고침
 로딩 중 예외 처리 및 오류 로그 출력
```
