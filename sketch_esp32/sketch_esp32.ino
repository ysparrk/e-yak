#include <DHT.h>
#include "BluetoothSerial.h"
#include "time.h"

// power & bluetooth LED
#define POWERLED 19
#define BTLED 18
// alarm related LED & BTN pins
#define ALARMLED 5
#define ALARMBTN 17
#define BUZPIN 12
// temperature & density pin
#define DHTPIN 13
// hall sensor
#define HALLPIN 27
// inner LED pins
#define LED1 23
#define LED2 22
#define LED3 21
#define LED4 32
#define LED5 33
#define LED6 25
#define LED7 26

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

#if !defined(CONFIG_BT_SPP_ENABLED)
#error Serial Bluetooth not available or not enabled. It is only available for the ESP32 chip.
#endif

//=== variables setup
// multi core
TaskHandle_t btnTask;

// bluetooth setups
String device_name = "ESP32-BT-TEST_2";
BluetoothSerial SerialBT;
bool btConnectFlag = false;
int btData[11];

// device open/close, alarm
bool deviceOpenFlag = false;
bool alarmFlag = false;

// buzzer setups
int freq = 5000;
int channel = 0;
int resolution = 8;





//=== callback & task functions
// temp: manual serial input.
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

// callback on bluetooth connection events.
void callback(esp_spp_cb_event_t event, esp_spp_cb_param_t* param) {
  if (event == ESP_SPP_SRV_OPEN_EVT) {
    Serial.println("Client Connected");
    btConnectFlag = true;
  } else if (event == ESP_SPP_SRV_STOP_EVT) {
    Serial.println("Client Disconnected");
    btConnectFlag = false;
  } else if (event == ESP_SPP_CLOSE_EVT) {
    Serial.println("Client Disconnected 2");
    btConnectFlag = false;
  } else if (event == ESP_SPP_START_EVT) {
    Serial.println("Server Started");
  } else if (event == ESP_SPP_INIT_EVT) {
    Serial.println("SPP Init");
  }
  else {
    Serial.println("Else");
  }
}

// task function for button push.
void btnPushTask(void *param) {
  Serial.print("btn task running...");
  Serial.println(xPortGetCoreID());
  while(1) {
    int state = digitalRead(ALARMBTN);
    if (btConnectFlag == true) {
      if (state == 0) {
        alarmFlag = false;
        //SerialBT.write(107);
      }
    }
  }
}

// task function for box open
void boxOpenTask(void *param) {
  Serial.print("alarm task running...");
  Serial.println(xPortGetCoreID());
  while(1) {
    // hall sensor read
    int sense = 0;
    sense = analogRead(HALLPIN);
    if (sense > 1900 || sense < 1800) { //"inled"
      deviceClose();
      //Serial.println(sense);
    } else {
      deviceOpen();
    }
  }
}



//=== operation functions.
// alarm on
void alarmOn() {
  digitalWrite(ALARMLED, HIGH);
  ledcWriteNote(channel, NOTE_D, 4);
  delay(500);
  ledcWriteNote(channel, NOTE_F, 4);
  delay(500);
  ledcWriteNote(channel, NOTE_A, 4);
  delay(500);
  ledcWriteTone(channel, 0);
  digitalWrite(ALARMLED, LOW);
  while(alarmFlag) {
    digitalWrite(ALARMLED, HIGH);
    delay(500);
    digitalWrite(ALARMLED, LOW);
    delay(500);
  }
}
// on device open
void deviceOpen() {
  digitalWrite(POWERLED, HIGH);
  if (btConnectFlag == true) {
    digitalWrite(BTLED, HIGH);
  } else {
    digitalWrite(BTLED, LOW);
  }
  digitalWrite(LED1, HIGH);
  digitalWrite(LED2, HIGH);
  digitalWrite(LED3, HIGH);
  digitalWrite(LED4, HIGH);
  digitalWrite(LED5, HIGH);
  digitalWrite(LED6, HIGH);
  digitalWrite(LED7, HIGH);
}
// on device close
void deviceClose() {
  digitalWrite(POWERLED, LOW);
  digitalWrite(BTLED, LOW);
  digitalWrite(LED1, LOW);
  digitalWrite(LED2, LOW);
  digitalWrite(LED3, LOW);
  digitalWrite(LED4, LOW);
  digitalWrite(LED5, LOW);
  digitalWrite(LED6, LOW);
  digitalWrite(LED7, LOW);
}





//=== setup
void setup() {
  // serial setup
  Serial.begin(115200);

  // bluetooth setup
  SerialBT.begin(device_name); //Bluetooth device name
  SerialBT.register_callback(callback);
  Serial.printf("The device with name \"%s\" is started.\nPair it with Bluetooth!\n", device_name.c_str());
  #ifdef USE_PIN
    SerialBT.setPin(pin);
    Serial.println("Using PIN");
  #endif

  // buzzer setup
  ledcSetup(channel, freq, resolution);
  ledcAttachPin(BUZPIN, channel);
  // btn setup
  pinMode(ALARMBTN, INPUT_PULLUP);
  // hall sensor setup
  pinMode(HALLPIN, INPUT);
  // out led setup
  pinMode(POWERLED, OUTPUT);
  pinMode(BTLED, OUTPUT);
  pinMode(ALARMLED, OUTPUT);
  pinMode(LED1, OUTPUT);
  pinMode(LED2, OUTPUT);
  pinMode(LED3, OUTPUT);
  pinMode(LED4, OUTPUT);
  pinMode(LED5, OUTPUT);
  pinMode(LED6, OUTPUT);
  pinMode(LED7, OUTPUT);

  // multi-core task setup
  //xTaskCreatePinnedToCore (btnPush, "btnTask", 10000, NULL, 1, &btnTask, 0);
  xTaskCreate(btnPushTask, "btnTask", 4096, NULL, tskIDLE_PRIORITY, NULL);
  xTaskCreate(boxOpenTask, "boxTask", 4096, NULL, tskIDLE_PRIORITY, NULL);
}



//=== loop
void loop() {

  // bluetooth signal out
  String data;
  if (Serial.available()) {
    int d = Serial.read();
    SerialBT.write(d);
  }

  // bluetooth signal in
  if (SerialBT.available()) {
    // bluetooth signal read
    data = readSerial();
    Serial.println(data);
    if (data == "alarm") {
      alarmFlag = true;
      alarmOn();
    }
  }

  delay(100);
}