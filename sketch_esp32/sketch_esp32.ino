#include <DHT.h>
#include "BluetoothSerial.h"

// power & bluetooth LED
#define POWERLED 19
#define BTLED 18
// alarm related LED & BTN pins
#define ALARMLED 5
#define ALARMBTN 17
#define BUZPIN 12
#define VIBPIN 14
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

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

#if !defined(CONFIG_BT_SPP_ENABLED)
#error Serial Bluetooth not available or not enabled. It is only available for the ESP32 chip.
#endif



//=== variables setup
// bt data
String r_data;
String s_data;

// multi core
// TaskHandle_t btnTask;

// bluetooth setups
String device_name = "ESP32-BT-TEST_2";
BluetoothSerial SerialBT;

// flags
bool btConnectFlag = false;
bool deviceOpenFlag = false;
bool alarmFlag = false;

// buzzer setups
int freq = 5000;
int channel = 0;
int resolution = 8;

// dht
DHT dht(DHTPIN, DHT11);




//=== serial & callback & task functions
// bt serial input read.
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
    btConnectFlag = true;
  } else if (event == ESP_SPP_SRV_STOP_EVT) {
    btConnectFlag = false;
  } else if (event == ESP_SPP_CLOSE_EVT) {
    btConnectFlag = false;
  }
}

// task function for button push.
void btnPushTask(void *param) {
  Serial.print("btn task running...");
  Serial.println(xPortGetCoreID());
  while(1) {
    int state = digitalRead(ALARMBTN);
    if (btConnectFlag == true) {
      if (state == 0 && alarmFlag == true) {
        alarmFlag = false;
        SerialBT.write(107); // k
      }
    }
  }
}

// task function for box open
void boxOpenTask(void *param) {
  Serial.print("box task running...");
  Serial.println(xPortGetCoreID());
  while(1) {
    // hall sensor read
    int sense = 0;
    sense = analogRead(HALLPIN);
    if (sense > 1900 || sense < 1800) {
      deviceClose();
      //Serial.println(sense);
    } else {
      deviceOpen();
    }
  }
}

// task function for dht measure
void dhtTask(void *param) {
  Serial.print("dht task running...");
  Serial.println(xPortGetCoreID());
  while(1) {
    int humid = dht.readHumidity();
    float temp = dht.readTemperature();
    if (humid >= 0 && humid <= 100) {
      Serial.print("ok ");
      
    }
    Serial.print(humid);
    Serial.print(" ");
    Serial.println(temp);
    delay(10000);
  }
}



//=== operation functions.
// alarm on
void alarmOn() {
  // first alarm
  if (r_data[1] == '1') {
    digitalWrite(ALARMLED, HIGH);
  }
  if (r_data[2] == '1' || r_data[3] == '1') {
    for(int i=0; i<3; i++) {
      if (r_data[2] == '1') ledcWriteNote(channel, NOTE_F, 4);
      if (r_data[3] == '1') digitalWrite(VIBPIN, HIGH);
      delay(300);
      ledcWriteTone(channel, 0);
      digitalWrite(VIBPIN, LOW);
      delay(100);
      if (r_data[2] == '1') ledcWriteNote(channel, NOTE_F, 4);
      if (r_data[3] == '1') digitalWrite(VIBPIN, HIGH);
      delay(500);
      ledcWriteTone(channel, 0);
      digitalWrite(VIBPIN, LOW);
      delay(300);
    }
  }
  ledcWriteTone(channel, 0);
  digitalWrite(ALARMLED, LOW);
  // repeating alarm (LED)
  int tm = 0;
  while(alarmFlag && tm <= 10) {   // timer set.
    if (r_data[1] == '1') digitalWrite(ALARMLED, HIGH);
    delay(500);
    digitalWrite(ALARMLED, LOW);
    delay(500);
    tm++;
  }
  // alarm end
  if (alarmFlag == false) {
    digitalWrite(ALARMLED, LOW);
    ledcWriteTone(channel, 0);
    digitalWrite(LED1, LOW);
    digitalWrite(LED2, LOW);
    digitalWrite(LED3, LOW);
    digitalWrite(LED4, LOW);
    digitalWrite(LED5, LOW);
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
  if (alarmFlag) {
    int leds[5] = {LED1, LED2, LED3, LED4, LED5};
    for(int i=0; i<5; i++) {
      if (r_data[4+i] == '1')
        digitalWrite(leds[i], HIGH);
    }
  }
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

  // buzzer & vib setup
  ledcSetup(channel, freq, resolution);
  ledcAttachPin(BUZPIN, channel);
  pinMode(VIBPIN, OUTPUT);
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

  // multi-core task setup
  xTaskCreate(btnPushTask, "btnTask", 4096, NULL, tskIDLE_PRIORITY, NULL);
  xTaskCreate(boxOpenTask, "boxTask", 4096, NULL, tskIDLE_PRIORITY, NULL);
  xTaskCreate(dhtTask, "dhtTask", 4096, NULL, tskIDLE_PRIORITY, NULL);
}



//=== loop
void loop() {

  // bluetooth signal out
  // if (Serial.available()) {
  //   int d = Serial.read();
  //   SerialBT.write(d);

  //   String s = "hello\n";
  //   uint8_t buf[s.length()+1];
  //   memcpy(buf, s.c_str(), s.length()+1);
  //   SerialBT.write(buf, s.length()+1);
  // }

  // bluetooth signal in
  if (SerialBT.available()) {
    // bluetooth signal read
    r_data = readSerial();
    Serial.println(r_data);
    if (r_data[0] == '0') { // 0: alarm, 123: LED/sound/buzz
      alarmFlag = true;
      alarmOn();
    } else if (r_data[0] == '1') { // 1: alarm off.
      alarmFlag = false;
    }
  }

  delay(200);
}