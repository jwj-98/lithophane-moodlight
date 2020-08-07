# Lithophane Mood Light

3D 프린터로 출력한 **리토페인(빛을 비추면 사진이 드러나는 판) 큐브 무드등**에 ESP32와 RGB LED를 넣고,
**안드로이드 앱에서 MQTT로 색상·전원·예약을 제어**하는 IoT 프로젝트입니다. (2020년 여름)

홈네트워크 수업에서 만든 [homenet-iot-switch](https://github.com/jwj-98/homenet-iot-switch)의 기기 등록 방식을 이어받아,
그때 완성하지 못한 **MQTT 원격 제어**를 실제로 구현했습니다.

## 시스템 구성

```
 ┌──────────────┐  HTTP (로그인/회원가입/기기목록)   ┌──────────────────┐
 │ Android 앱    │ ─────────────────────────────────▶ │ JSP API 서버      │
 │              │                                    │ (Tomcat :8090)    │
 │              │  MQTT publish                      ├──────────────────┤
 │              │ ─────────────────────────────────▶ │ MQTT 브로커 :1883 │
 └──────────────┘                                    └────────┬─────────┘
                                                              │ subscribe
 ┌──────────────┐  POST registDevice.jsp (최초 등록)          ▼
 │ ESP32 무드등  │ ◀───────────────────────────────────────────┘
 │ RGB LED + 버튼│
 └──────────────┘
```

## 기능

- **기기 등록**: 무드등이 AP 모드(`Lithophane_device`)로 바뀌면 스마트폰으로 접속해 공유기 정보와 계정을 입력 → 무드등이 서버에 MAC 주소로 등록
- **앱 로그인 / 회원가입**
- **내 기기 목록**: 계정에 등록된 무드등 목록 표시
- **전원 / 색상 제어**: 앱에서 RGB 값을 골라 MQTT로 전송 → 무드등이 즉시 반영
- **예약**: `AlarmManager` + 서비스로 지정 시간에 켜기/끄기 메시지 전송
- **물리 버튼 하나로 두 기능**: 짧게 누르면 on/off, 2초 이상 누르면 빨간색 깜빡임과 함께 기기 등록(AP) 모드 진입

## MQTT 메시지 형식

고정 길이 문자열을 씁니다. 무드등은 앞 17자리 MAC이 자기 주소와 같을 때만 반응합니다.

```
 AA:BB:CC:DD:EE:FF  1  255 128 000
 └──── MAC 17자 ──┘  │  └R─┘└G─┘└B─┘  (각 3자리, 0~255)
                     └ 명령: 0 = on/off 토글, 1 = 색상 지정
```

## 폴더 구조

```
android-app/                  안드로이드 앱 (Java)
  app/src/main/java/com/example/nightlight/
    act_login, act_signup     로그인 / 회원가입 화면
    Main, MainAdapter         내 기기 목록
    Con_lithophane            색상·전원 제어 화면
    TimePicker, AlarmService  예약 켜기/끄기
    MqttRegister              MQTT 발행 (Eclipse Paho)
    Config                    서버 주소 설정  ← 빌드 전에 수정
firmware/lithophane_device/   ESP32 펌웨어 (RGB PWM, AP 프로비저닝, MQTT 구독)
3d-print/gcode/               직접 슬라이싱한 출력 파일 (Cura, 0.2mm)
```

## 하드웨어

- ESP32 개발보드
- RGB LED 스트립 (R: GPIO 21, G: GPIO 22, B: GPIO 26, `ledcWrite` PWM)
- 버튼 1개 (GPIO 16)
- 3D 프린팅 큐브 케이스 + 리토페인 패널

## 실행

**앱**
1. `android-app/app/src/main/java/com/example/nightlight/Config.java`의 `YOUR_SERVER_IP`를 서버 주소로 바꿉니다.
2. Android Studio로 `android-app`을 열고 빌드합니다. (compileSdk 29, MQTT는 `org.eclipse.paho.client.mqttv3:1.2.4`)

**펌웨어** (Arduino IDE, ESP32 보드 매니저)
- 라이브러리: `ESPAsyncWebServer`, `AsyncTCP`, `PubSubClient`
- `YOUR_SERVER_IP`, `YOUR_AP_PASSWORD`를 바꿔서 업로드합니다.

## 서버 API (앱/펌웨어가 호출하는 명세)

서버 측 JSP 소스는 보관하지 못해 이 저장소에는 없습니다. 클라이언트 코드 기준 명세입니다.

| 경로 | 호출 | 파라미터 |
|---|---|---|
| `/jsp_server/login.jsp` | 앱 | userid, userpw |
| `/jsp_server/signup.jsp` | 앱 | userid, userpw |
| `/jsp_server/getDevice.jsp` | 앱 | userid → 등록된 기기 목록 |
| `/jsp_server/registDevice.jsp` | 펌웨어 | username, password, mac, type=`lithophane_1.0` |

## 알려진 한계 (당시 그대로 보존)

- 앱은 사용자 아이디를 MQTT 토픽으로 발행하는데, 펌웨어는 테스트용 토픽 `test`를 구독하고 있어 토픽을 맞춰야 합니다.
- 펌웨어의 공유기 접속 부분이 테스트용 고정 SSID로 바뀌어 있습니다. (원래 코드는 주석으로 남아 있음)
- `AndroidManifest.xml`에 `.con_lithophane`로 등록되어 있어 클래스명 `Con_lithophane`과 대소문자가 다릅니다.
- 통신이 모두 평문(HTTP, MQTT 인증 없음)입니다.

## 3D 모델 출처

케이스 모델은 Thingiverse의 **"Lithophane Light Cube" (제작자: Matt Porter, Creative Commons 라이선스)**를 사용했습니다.
원본 STL은 저장소에 포함하지 않았고, 이 프로젝트용으로 직접 슬라이싱한 G-code만 넣었습니다.

## 기술 스택

ESP32 (Arduino) · PubSubClient · MQTT · Android (Java) · Eclipse Paho · JSP/Tomcat · 3D 프린팅 (Cura)
