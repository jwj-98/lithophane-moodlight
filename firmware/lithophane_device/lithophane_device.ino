#include <ESPAsyncWebServer.h>
#include <WiFi.h>
#include <FS.h>
#include <HTTPClient.h>
#include <PubSubClient.h>

//led strip on/off tirg
boolean ledTrig = false;

// pin var
#define ledR 21
#define ledG 22
#define ledB 26
#define btn 16
bool preBtn = false;
unsigned long times;

//btn lock trig
bool btnLock = true;

//rgb strip set
int R = 0;
int G = 0;
int B = 0;

//접속할 ap 정보, 등록할 유저 정보 변수
String acSsid_str = "";
String acPw_str = "";
String type;
String mac;
boolean trig = false;
int count= 1;
String username = "";
String userpw;
bool apTrig = true;

//기기등록 시 생성할 ap 정보 변수
const char* APSsid = "Lithophane_device";
const char* APPw = "YOUR_AP_PASSWORD";
AsyncWebServer server(80);


//client가 잘못된 주소로 접속시 표시합니다.
void notFound(AsyncWebServerRequest *request) {
    request->send(404, "text/plain", "Not found");
}

//ap웹 서버에서 보여줄 html입니다. WiFi스캔을 위해 header와 footer를 구분합니다. (header + 'WiFi스캔' + footer)
String htmlHeader = R"qoooo(<!DOCTYPE html><html lang=\"en\"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0" ><style>div, input {padding: 5px;font-size: 1em;}input{width: 95%;}select {padding: 5px;font-size: 1em;width: 100%;}body {text-align: center;font-family: verdana;}button {border: 0;border-radius: 0.3rem;background-color: #1fa3ec;color: #fff;line-height: 2.4rem;font-size: 1.2rem;width: 100%}h3 {border: 0;border-radius: 0.3rem;background-color: #3b3b3b;color: #fff;line-height: 2.4rem;font-size: 1.2rem;width: 100%}</style></head><body><div style="text-align:center;display:inline-block;min-width:260px;"><h3>IOT 기기등록</h3>공유기 정보를 입력하고<BR>기기등록을 완료해주세요.<BR><br/><form method='post' action='save'><div><input name='username' type='text' placeholder='사이트 아이디'></div><div> <input name='userpw' type='password' placeholder='사이트 비밀번호'></div><div><select name="ssid"><option selected>SSID 선택...</option>)qoooo";
String htmlFooter = R"qoooo(</select></div><div><input name='pw' type='password' placeholder='비밀번호'></div><br/><button type='submit'>완&nbsp;료</button></form><p>&copy; 2020-team_3D print</p><br/></div></body></html>)qoooo";
String viewHtml;
String htmlSave = R"hellohaha(<!DOCTYPE html><html lang=\"en\"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0" ><style>div, input {padding: 5px;font-size: 1em;}input {width: 95%;}select {padding: 5px;font-size: 1em;width: 100%;}body {text-align: center;font-family: verdana;}button {border: 0;border-radius: 0.3rem;background-color: #1fa3ec;color: #fff;line-height: 2.4rem;font-size: 1.2rem;width: 100%}h3 {border: 0;border-radius: 0.3rem;background-color: #3b3b3b;color: #fff;line-height: 2.4rem;font-size: 1.2rem;width: 100%}.w {border: 0;border-radius: 0.3rem;background-color: rgba(255, 0, 0, 0.78);color: #fff;line-height: 1.8rem;width: 100%}</style></head><body><div style="text-align:center;display:inline-block;min-width:260px;"><h3>IOT 기기등록</h3>기기등록 버튼의 불빛을 확인해주세요!<BR>기기등록이 완료되면 불이 꺼집니다.<BR><BR><div class="w">10초이상기기등록이진행되지않을경우<BR>메뉴얼2.부터다시진행해주세요.</div><br/><p>&copy;2020-team_3D print</p><br/></div></body></html>)hellohaha";

//mqtt 정보
const char* mqtt_server = "YOUR_SERVER_IP";   // MQTT 브로커 주소
WiFiClient espClient;
PubSubClient client(espClient);
unsigned long lastMsg = 0;
#define MSG_BUFFER_SIZE  (50)
char msg[MSG_BUFFER_SIZE];
int value = 0;

void setup() {
    Serial.begin(115200);
    pinMode(btn, INPUT_PULLUP);
    ledcSetup(0, 5000, 8);
    ledcAttachPin(ledR, 0);
    ledcSetup(1, 5000, 8);
    ledcAttachPin(ledG, 1);
    ledcSetup(2, 5000, 8);
    ledcAttachPin(ledB, 2);
    ledcWrite(0, 255);
    ledcWrite(1, 255);
    ledcWrite(2, 255);
    ledTrig = false;
}


void loop() {
  //버튼 스캔
  bool val = !digitalRead(btn);
  String scanSsid;
  int scanCount;
  //Serial.println(val);

  //전등&기기등록 버튼 동작
  if(btnLock){
    if(val){
      delay(40);
        if(!preBtn){
            times = millis();
            preBtn = true;
            
        }
        //버튼을 2초이상 누를 경우 기기 등록 실행
        if ( (millis() - times > 2000) ){
          apTrig = false;
          preBtn = false;
          ledcWrite(0, 255);
          ledcWrite(1, 255);
          ledcWrite(2, 255);
          delay(300);
          ledcWrite(0, 0);
          delay(300);
          ledcWrite(0, 255);
          delay(300);
          ledcWrite(0, 0);
          delay(300);
          ledcWrite(0, 255);
          delay(300);
          ledcWrite(0, 0);
          delay(300);
          ledcWrite(0, 255);
          delay(300);
          ledcWrite(0, 0);
          delay(300);
          ledcWrite(0, 255);
          delay(300);
          ledcWrite(0, 0);
          ledTrig = true;
          trig = true;
          acSsid_str = "";
          acPw_str= "";
          WiFi.disconnect();
        }
    }else if(!val){
      delay(40);
        if(preBtn){
            changeRlySt(1);
            sendRlyCon(1);
            preBtn = false;
        }
    }
  }


  //기기등록 이벤트 처리
  while (trig) { //현재 ap에 접속돼있지 않다면 반복 10번까지 접속 시도
    btnLock = false;
    delay(500);
    WiFi.softAPdisconnect(true);

    //형변환 [ wifi.begin(char[],char[]) ]
//    char* acSsid = (char*)acSsid_str.c_str();
//    char* acPw = (char*)acPw_str.c_str();
    char* acSsid = "YOUR_WIFI_SSID";   // 테스트용 고정값
    char* acPw = "YOUR_WIFI_PASSWORD";
    
    WiFi.begin(acSsid, acPw);
    Serial.print(acSsid_str);
    Serial.print(acPw_str);
    Serial.print("AP에 접속 시도...(");
    Serial.print(count);
    Serial.println(")");
    if(WiFi.status() != WL_CONNECTED){
      count++;
    }
    if(WiFi.status() == WL_CONNECTED){ //AP접속 성공시 실행, 웹 요청
      Serial.println("!...AP에 접속 성공");
      int httpCount = 0;
      boolean httpTrig = true;

      while(httpTrig){
        mac = WiFi.macAddress();
        String response;
        String post = "&username=" + username + "&password=" + userpw + "&mac=" + mac + "&type=lithophane_1.0";

        Serial.print("send post: ");
        Serial.println(post);

        delay(1000);
        HTTPClient http;

        http.begin("http://YOUR_SERVER_IP:8090/jsp_server/registDevice.jsp");
        http.addHeader("Content-Type", "application/x-www-form-urlencoded");

        int httpResponseCode = http.POST(post);

        if(httpResponseCode>0){
         response = http.getString();

         Serial.println(httpResponseCode);
         Serial.println(response);
        }else{
         Serial.print("Error on sending POST: ");
         Serial.println(httpResponseCode);
        }
        http.end();  //Free resources

        if(httpResponseCode == 200){
          trig = false;
          httpTrig = false;
          httpCount = 0;
          ledcWrite(0, 255);
          ledTrig = false;
          btnLock = true;
          apTrig = true;
        }

        if(httpCount > 5) {
          WiFi.disconnect();
          acSsid_str = "";
          acPw_str = "";
          httpTrig = false;
        }
        httpCount++;
      }
    }


    if(count > 20){// 접속시도 10회초과로 ap모드를 활성화해 접속할ap 정보를 받아옵니다.
      WiFi.disconnect();
      trig = false;//post정보를 받을때까지 trig=false; ap접속을 시도하지 않습니다.
      apTrig = false;
      IPAddress ip(192, 168, 4, 1);
      IPAddress gateway(192, 168, 4, 2);
      IPAddress subnet(255, 255, 255, 0);
      WiFi.softAPConfig(ip, gateway, subnet);
      WiFi.softAP(APSsid, APPw);//ap생성

      Serial.print("접속실패...AP모드를 활성화합니다. AP IP Address: ");
      Serial.println(WiFi.softAPIP());

      // softAPIP로 get 요청시 실행
      server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
          String ssids; //스캔된 WiFi의 ssid
          int netcount = WiFi.scanNetworks();
          viewHtml = htmlHeader; //스캔된 ap ssid를 html코드 사이에 추가시킵니다.
          for(int i= 0; i< netcount; i++){
            ssids= WiFi.SSID(i);
            viewHtml += "<option value=" + ssids + ">";
            viewHtml += ssids;
            viewHtml += "</option>";
          }
          viewHtml += htmlFooter;
          request->send(200, "text/html", viewHtml);
      });
      // /save로 post 요청시 실행
      server.on("/save", HTTP_POST, [](AsyncWebServerRequest *request){
          if (request->hasParam("username", true)) {
              username = request->getParam("username", true)->value();
          }
          if (request->hasParam("userpw", true)) {
              userpw = request->getParam("userpw", true)->value();
          }
          if (request->hasParam("ssid", true)) {
              acSsid_str = request->getParam("ssid", true)->value();
          }
          if (request->hasParam("pw", true)) {
              acPw_str = request->getParam("pw", true)->value();
          }
          apTrig = true;
          trig = true;
          count = 1;

          request->send(200, "text/html", htmlSave);

      });
      server.onNotFound(notFound);
      server.begin();
    }

  }

  if(apTrig){
    if(WiFi.status() != WL_CONNECTED){
       //WiFi.softAPdisconnect(true);

      //형변환 [ wifi.begin(char[],char[]) ]
      char* acSsid = (char*)acSsid_str.c_str();
      char* acPw = (char*)acPw_str.c_str();
  
      WiFi.begin(acSsid, acPw);
      Serial.println("AP에 접속 시도...");
    }
    //mqtt에 접속 후 구독합니다.
    else if(WiFi.status() == WL_CONNECTED){
      mac = WiFi.macAddress();
      if (!client.connected()) {
        reconnect();
      }
      client.loop();
    }
  }
  //mqtt brocker에서 보낸 메세지가 있다면 헤더를 확인하고 릴레이를 제어합니다.


}// end loop
//***********************************************************************************
//***********************************************************************************

//함수.led 상태 변경 [on/off]
void changeRlySt(int num){
  if(!ledTrig){
    ledTrig = true;
    ledcWrite(0, R);
    ledcWrite(1, G);
    ledcWrite(2, B);
  }else if( ledTrig ){
    ledTrig = false;
    ledcWrite(0, 255);
    ledcWrite(1, 255);
    ledcWrite(2, 255);
  }
}

//함수.기기 수동제어 전송
void sendRlyCon(int num){

}

//함수.mqtt통신 후 실행
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
  String payMac = "";
    for (int i = 0; i < 17; i++){
    payMac = payMac + (char)payload[i];
  }
  int payNum = payload[17] - 48;
  char c_payR[] = {payload[18], payload[19], payload[20]};
  int payR = atoi(c_payR);
  char c_payG[] = {payload[21], payload[22], payload[23]};
  int payG = atoi(c_payG);
  char c_payB[] = {payload[24], payload[25], payload[26]};
  int payB = atoi(c_payB);
  
   if(mac.equals(payMac)) {
      if(payNum == 0){
        changeRlySt(payNum);
      }else if(payNum == 1){
        ledTrig = true;
        R = 255 - payR;
        G = 255 - payG;
        B = 255 - payB;
        ledcWrite(0, R);
        ledcWrite(1, G);
        ledcWrite(2, B);
      }
    }
}

//함수.mqtt 구독
void reconnect() {
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
  // Loop until we're reconnected
  int count = 0;
  bool trig = true;
  while (trig) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
      // ... and resubscribe
      char* userid = (char*)username.c_str();
      client.subscribe("test");
      trig = false;
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
      count ++;
      if(count > 5) trig = false;
    }
  }
}
