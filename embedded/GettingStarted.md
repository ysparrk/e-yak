# 0. 시작

> ESP32 보드 설정

설정 > 추가적인 보드 매니저
https://dl.espressif.com/dl/package_esp32_index.json

보드매니저 > esp32 espressif systems 설치

> 포트 검색이 되지 않을 때

드라이버 설치

https://www.silabs.com/developers/usb-to-uart-bridge-vcp-drivers?tab=downloads

# 1. 블루투스 연결
1. Classic
    * 1:1 전송, 연결확인
    * 대용량 데이터 전송 가능
    * 최대 3MB/sec
    * 안정적
2. Blue tooth Low Energy mode
    * 1:1 및 1:다 broadcasting 
    * 적은 데이터 전송
    * 저전력
    * 최대 2MB/sec
    * 불안정

[각종 튜토리얼 있는 사이트](https://randomnerdtutorials.com/esp32-bluetooth-low-energy-ble-arduino-ide/)

### 1. 블루투스 연결 테스트 1 (Classic)

[가이드](https://www.bneware.com/blogPost/esp32_arduino_bluetooth)

SerialToSerialBT 예제 기반
```arduino
#include "BluetoothSerial.h"

const char *pin = "1234";

String device_name = "ESP32-BT-TEST";

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

#if !defined(CONFIG_BT_SPP_ENABLED)
#error Serial Bluetooth not available or not enabled. It is only available for the ESP32 chip.
#endif

BluetoothSerial SerialBT;

String readSerial(){
  String str = "";
  char ch;

  while(SerialBT.available()){
    ch = SerialBT.read();
    str.concat(ch);
    delay(10);
  }
  return str;
}
void setup() {
  Serial.begin(115200);
  SerialBT.begin(device_name);
  Serial.printf("The device with name \"%s\" is started.\nNow you can pair it with Bluetooth!\n", device_name.c_str());
  #ifdef USE_PIN
    SerialBT.setPin(pin);
    Serial.println("Using PIN");
  #endif
}

void loop() {
  String data;

  if (Serial.available()) {
    SerialBT.write(Serial.read());
  }
  if (SerialBT.available()) {
    data = readSerial();
    Serial.println(data);
  }
  delay(100);
}

```
arduino bluetooth controller 어플로 통신 확인

### 2. 블루투스 연결 테스트 2 (BLE)

[가이드](https://blog.naver.com/chandong83/222032757410)

BLE_uart 예제 기반
```arduino
/*
    Video: https://www.youtube.com/watch?v=oCMOYS71NIU
    Based on Neil Kolban example for IDF: https://github.com/nkolban/esp32-snippets/blob/master/cpp_utils/tests/BLE%20Tests/SampleNotify.cpp
    Ported to Arduino ESP32 by Evandro Copercini

   Create a BLE server that, once we receive a connection, will send periodic notifications.
   The service advertises itself as: 6E400001-B5A3-F393-E0A9-E50E24DCCA9E
   Has a characteristic of: 6E400002-B5A3-F393-E0A9-E50E24DCCA9E - used for receiving data with "WRITE" 
   Has a characteristic of: 6E400003-B5A3-F393-E0A9-E50E24DCCA9E - used to send data with  "NOTIFY"

   The design of creating the BLE server is:
   1. Create a BLE Server
   2. Create a BLE Service
   3. Create a BLE Characteristic on the Service
   4. Create a BLE Descriptor on the characteristic
   5. Start the service.
   6. Start advertising.

   In this example rxValue is the data received (only accessible inside that function).
   And txValue is the data to be sent, in this example just a byte incremented every second. 
*/
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

BLEServer *pServer = NULL;
BLECharacteristic * pTxCharacteristic;
bool deviceConnected = false;
bool oldDeviceConnected = false;
uint8_t txValue = 0;

// See the following for generating UUIDs:
// https://www.uuidgenerator.net/

#define SERVICE_UUID           "6E400001-B5A3-F393-E0A9-E50E24DCCA9E" // UART service UUID
#define CHARACTERISTIC_UUID_RX "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
#define CHARACTERISTIC_UUID_TX "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"


class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      deviceConnected = true;
    };

    void onDisconnect(BLEServer* pServer) {
      deviceConnected = false;
    }
};

class MyCallbacks: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
      std::string rxValue = pCharacteristic->getValue();

      if (rxValue.length() > 0) {
        Serial.println("*********");
        Serial.print("Received Value: ");
        for (int i = 0; i < rxValue.length(); i++)
          Serial.print(rxValue[i]);

        Serial.println();
        Serial.println("*********");
      }
    }
};


void setup() {
  Serial.begin(115200);

  // Create the BLE Device
  BLEDevice::init("ESP32-BLE-TEST");

  // Create the BLE Server
  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  // Create the BLE Service
  BLEService *pService = pServer->createService(SERVICE_UUID);

  // Create a BLE Characteristic
  pTxCharacteristic = pService->createCharacteristic(
										CHARACTERISTIC_UUID_TX,
										BLECharacteristic::PROPERTY_NOTIFY
									);
                      
  pTxCharacteristic->addDescriptor(new BLE2902());

  BLECharacteristic * pRxCharacteristic = pService->createCharacteristic(
											 CHARACTERISTIC_UUID_RX,
											BLECharacteristic::PROPERTY_WRITE
										);

  pRxCharacteristic->setCallbacks(new MyCallbacks());

  // Start the service
  pService->start();

  // Start advertising
  pServer->getAdvertising()->start();
  Serial.println("Waiting a client connection to notify...");
}

void loop() {
  char buffer[10];

    if (deviceConnected) {
        sprintf(buffer, "%d초 \n", txValue);
        pTxCharacteristic->setValue((uint8_t*)buffer, strlen(buffer));
        pTxCharacteristic->notify();
        txValue++;
	    	delay(1000); // bluetooth stack will go into congestion, if too many packets are sent
	  }

    // disconnecting
    if (!deviceConnected && oldDeviceConnected) {
        delay(500); // give the bluetooth stack the chance to get things ready
        pServer->startAdvertising(); // restart advertising
        Serial.println("start advertising");
        oldDeviceConnected = deviceConnected;
    }
    // connecting
    if (deviceConnected && !oldDeviceConnected) {
		// do stuff here on connecting
        oldDeviceConnected = deviceConnected;
    }
}

```
nRF toolbox 어플(코드 공개되어 있음)과 통신 확인
# 2. 센서 연결 테스트
### 1. 온습도 센서
스케치 > 라이브러리 포함 > 라이브러리 관리

DHT sensor library by Adafruit 설치

```arduino
#include <DHT.h>
#define DHTPIN 13

DHT dht(DHTPIN, DHT11);

void setup() {
  Serial.begin(115200);
}

void loop() {
  int humid = dht.readHumidity();
  int temp = dht.readTemperature();

  Serial.print("humid: ");
  Serial.println(humid);
  Serial.print("temp: ");
  Serial.println(temp);

  delay(1000);
}
```
humid: 158<br>
temp: 12<br>
humid: 2147483647<br>
temp: 214748364<br>

-> 결과 불안정. 정상 작동은 한다.
### 2. 진동모터
```arduino
#define VIBPIN 21

void setup() {
  pinMode(VIBPIN, OUTPUT);
}

void loop() {
  digitalWrite(VIBPIN, HIGH);
  delay(2000);
  digitalWrite(VIBPIN, LOW);
  delay(2000);
}
```
```arduino
#define VIBPIN 21

void setup() {
  Serial.begin(115200);
  pinMode(VIBPIN, OUTPUT);
}

void loop() {
  for (int i=0;i<=255;i++){
    analogWrite(VIBPIN, i);
    Serial.println(i);
    delay(500);
  }
}
```
analogWrite 했을 때 약 100부터 느껴진다
### 3. LED
진동모터와 동일
### 4. 버튼
```arduino
#define BTNPIN 17

void setup() {
  Serial.begin(115200);
  pinMode(BTNPIN, INPUT_PULLUP);
}

void loop() {
  int state = digitalRead(BTNPIN);
  Serial.println(state);

  delay(20);
}
```
눌렸을 때 0 출력
### 5. 홀 센서
내장 홀 센서
```arduino
void setup() {
  Serial.begin(115200);
}

void loop() {
  int measure = 0;
  measure = hallRead();
  Serial.println(measure);
  delay(500);
}
```
WSH135
```arduino
#define HALLPIN 27

void setup() {
  Serial.begin(115200);
  pinMode(HALLPIN, INPUT);
}

void loop() {
  int sense = 0;
  sense = analogRead( HALLPIN );
  Serial.println(sense);
  delay(100);
}
```
센서 돌출 된 부분을 앞면으로 봤을 때<br>
왼쪽부터 3.3V GND 27번핀에 연결해서 테스트
### 6. 부저
```arduino
#define BUZPIN 12

int freq = 5000;
int channel = 0;
int resolution = 8;

void setup() {
  Serial.begin(115200);
  ledcSetup(channel, freq, resolution);
  ledcAttachPin(BUZPIN, channel);
}

void loop() {
  ledcWriteNote(channel,NOTE_D, 4);
  delay(200);
  ledcWriteTone(channel, 0);
  delay(2000);
}
```
`uint32_t ledcWriteTone(uint8_t chan, uint32_t freq);`

chan : select LEDC channel<br>
freq : select frequency of pwm signal

`uint32_t ledcWriteNote(uint8_t chan, note_t note, uint8_t octave);`

note : select note to be set<br>
octave : select octave for note
