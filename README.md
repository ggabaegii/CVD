# CVD
동의대학교 컴퓨터소프트웨어공학과 2024 1학기 캡스톤디자인 팀프로젝트  
**색각 이상자(적록 색약)를 위한 색 보정 프로그램 (Color Correction Program for Color Vision Deviate)**
***
## 1. 프로젝트 개요
CVD는 적록 색약 사용자가 사진 속 빨강/초록 계열 색상을 더 쉽게 구분할 수 있도록 돕는 Android 기반 색 보정 앱입니다.  
사용자는 카메라로 직접 촬영하거나 갤러리 이미지를 불러온 뒤, 자동 색 보정 또는 HSV 수동 조정을 적용할 수 있습니다.  
OpenCV를 활용해 이미지의 HSV 색상 공간을 변환하고, 보정 결과를 저장/공유할 수 있도록 구현했습니다.

---
## 2. 주요 기능
- 카메라 촬영 이미지 보정
- 갤러리 이미지 불러오기 및 보정
- 적록 색약자를 위한 자동 색 보정
- HSV 기반 수동 색상 조정
- Sobel 연산 기반 윤곽선 추출
- 보정 이미지 저장, 목록 관리, 공유

---
## 3. 개발 환경
- Language: Java
- IDE: Android Studio Hedgehog
- Gradle Plugin: com.android.application
- Compile SDK: 34
- Min SDK: 21
- Target SDK: 34
- OpenCV: 4.8.0
- 주요 라이브러리
  - AndroidX AppCompat 1.6.1
  - Material Components 1.11.0
  - ConstraintLayout 2.1.4
  - Glide 4.14.2
  - RecyclerView SwipeDecorator 1.4
---
## 4. 실행 방법
(1) Android Studio에서 프로젝트를 엽니다.  
(2) Gradle Sync를 실행합니다.  
(3) ndroid 14 또는 호환 가능한 Android 기기/에뮬레이터를 연결합니다.  
(4) `app` 모듈을 실행합니다.  
(5) 최초 실행 시 카메라 및 저장소 권한을 허용합니다.

---
## 5. 핵심 기능 및 주요 구현

### (1) 이미지 입력
- `MainActivity.gallery()`: 갤러리에서 이미지를 선택하고 편집 화면으로 전달합니다.
- `MainActivity.camera()`: 카메라 앱을 호출해 사진을 촬영하고 결과 URI를 편집 화면으로 전달합니다.
- `MainActivity.createImageFile()`: 촬영 이미지를 저장할 임시 파일과 FileProvider URI를 생성합니다.

### (2) 자동 색 보정
- `auto.autoCorrectImage()`: 원본 이미지를 복사한 뒤 색각 보정 로직을 적용하고 화면에 표시합니다.
- `auto.applyColorBlindCorrection(Mat imageMat)`: 이미지를 HSV 색상 공간으로 변환한 뒤 빨강/초록 계열의 Hue, Saturation, Value 값을 조정합니다. 흰색에 가까운 픽셀은 제외하여 배경 왜곡을 줄입니다.
- `auto.resetImage()`: 보정 결과를 원본 이미지로 되돌립니다.

### (3) HSV 수동 조정
- `HSV.setupHSVAdjustment()`: Hue, Saturation, Value 조정을 위한 SeekBar 이벤트를 설정합니다.
- `HSV.adjustHSV(int hue, int saturation, int value)`: OpenCV의 HSV 채널 분리/병합을 이용해 사용자가 지정한 색상, 채도, 명도 값을 이미지에 반영합니다.
- `HSV.saveBitmapToFile(Bitmap bitmap, String title)`: 보정 결과 이미지를 PNG 파일로 저장합니다.

### (4) 윤곽선 추출
- `outline.loadImage(Uri imageUri)`: 선택된 이미지를 Bitmap으로 로드합니다.
- `outline.processImage()`: 이미지를 Grayscale로 변환한 뒤 Sobel 연산자를 적용해 윤곽선을 추출합니다.
- `outline.saveBitmapToFile(Bitmap bitmap, String title)`: 윤곽선 처리 결과를 파일로 저장합니다.

### (5) 사진첩 관리
- `PhotoBookDB.addPhoto(String title, String uri)`: 저장한 이미지의 제목과 URI를 SQLite에 저장합니다.
- `PhotoBookDB.readAllData()`: 저장된 사진 목록을 조회합니다.
- `PhotoBookDB.readAllDataTitle()`: 제목 기준으로 정렬된 사진 목록을 조회합니다.
- `PhotoBookDB.deleteData(String title)`: 선택한 사진 기록을 삭제합니다.
- `PhotoBookDB.updateData(String oldTitle, String newTitle)`: 저장된 사진 제목을 수정합니다.
---
## 6. 결과 화면
- HSV 수동 조정  
  ![Image](https://github.com/user-attachments/assets/c2cf1e0a-511a-464b-8d1d-e0d3f1ab34bf)  
사용자가 Hue, Saturation, Value를 직접 조절하여 색상 대비를 강화합니다.  

- 자동 보정 (전/후)  
  <img width="391" height="378" alt="Image" src="https://github.com/user-attachments/assets/8823bdbd-0288-49f5-ad67-dad15470b45c" />  
빨강/초록 계열 색상을 HSV 기준으로 변환하여 적록 색약 사용자의 색상 구분을 돕습니다.
---
## 7. 발표자료
- [프로젝트 발표자료](docs/cvd_presentation.pdf)
---
## 8. License
- 소스 코드는 MIT License를 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참고해 주세요.
- 발표자료, 폰트, 디자인 요소 등 외부 저작물이 포함된 자료는 각 제공자의 라이선스를 따릅니다.
