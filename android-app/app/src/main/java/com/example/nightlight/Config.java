package com.example.nightlight;

/**
 * 서버 접속 정보.
 * 실제 서버 주소는 저장소에 올리지 않습니다. 빌드 전에 아래 값을 본인 환경에 맞게 바꿔주세요.
 */
public final class Config {
    private Config() {}

    /** JSP API 서버 (Tomcat) */
    public static final String API_BASE = "http://YOUR_SERVER_IP:8090/jsp_server";

    /** MQTT 브로커 (Mosquitto 등) */
    public static final String MQTT_BROKER = "tcp://YOUR_SERVER_IP:1883";
}
