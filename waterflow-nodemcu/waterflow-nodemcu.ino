// library untuk esp8266
#include <ESP8266WiFi.h>

// library untuk firebase
#include <FirebaseArduino.h>

// library untuk keterangan waktu (bulan)
#include <NTPClient.h>
#include <WiFiUdp.h>

#define FIREBASE_HOST   "smartpdam-961a8-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH   "BbwxzHboCN7ULeHJUuRzpK8jhoBGtsDHdxxSOSI8"
#define WIFI_SSID       "UNAMA"
#define WIFI_PASSWORD   "unamaoke"
#define WF_SENSOR       D3

String USER_ID = "users/xI3UWpcMSJXeqP8fqfIlG0w3fPn1/"; // ubah userID

// define NTP Client to get time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

//Week Days
String weekDays[7] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};


//Month names
String months[12] = {
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
};

long curr_ms = 0, prev_ms = 0, interval = 1000, ml, total_ml;
float calibrationFactor = 4.5, flowRate = 0, flowLiter = 0, totalLiter = 0, air, biaya;
byte pulseCount = 0, pulsePerSec = 0;

void IRAM_ATTR pulseCounter() {
  pulseCount++;
}

void setup() {
  Serial.begin(9600);

  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());

  // Initialize a NTPClient to get time
  timeClient.begin();
  // Set offset time in seconds to adjust for your timezone, for example:
  // GMT +1 = 3600
  // GMT +8 = 28800
  // GMT -1 = -3600
  // GMT 0 = 0
  timeClient.setTimeOffset(3600 * 7); // GMT +7

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  pinMode(WF_SENSOR, INPUT_PULLUP);

  attachInterrupt(digitalPinToInterrupt(WF_SENSOR), pulseCounter, FALLING);

  air = getAir().toFloat();
  biaya = getBiaya().toFloat();
}

void loop() {
  curr_ms = millis();
  if (curr_ms - prev_ms > interval) {

    pulsePerSec = pulseCount;
    pulseCount = 0;
    flowRate = ((1000.0 / (millis() - prev_ms)) * pulsePerSec) / calibrationFactor;
    prev_ms = millis();

    ml = (flowRate / 60) * 1000;
    flowLiter = (flowRate / 60);
    total_ml += ml;

    totalLiter += flowLiter;
  }

  if (flowRate > 0) {
    air += totalLiter;
  }

  Serial.print("Update Data Air: ");
  Serial.print(air);
  Serial.print("\t");
  Serial.print(flowRate);
  Serial.print("\t");
  Serial.println(flowLiter);

  if (air <= 10000) {
    biaya = air * 4;
  }
  else if (air > 10000 && air <= 20000) {
    biaya = air * 5.5;
  }
  else {
    biaya = air * 6.5;
  }

  Serial.print("Update Data Biaya: Rp.");
  Serial.println(biaya);
  Serial.println("");

  updateAir(air);
  updateBiaya(biaya);
}

void updateAir(int value) {
  String air = String(value);
  String bulan = String(getBulan());

  // update data air
  String pathAir = USER_ID + "data/" + bulan + "/air";
  Firebase.setString(pathAir, air); checkError();

  // update chart
  String pathChart = USER_ID + "chart/" + bulan + "/y";
  Firebase.setString(pathChart, air); checkError();
  Serial.println("Berhasil Update Air");
}

void updateBiaya(int value) {
  String biaya = String(value);
  String bulan = String(getBulan());

  // update biaya
  String pathBiaya = USER_ID + "data/" + bulan + "/biaya";
  Firebase.setString(pathBiaya, biaya); checkError();
  Serial.println("Berhasil Update Biaya");
}

String getAir() {
  String bulan = String(getBulan());
  // get data air
  String pathAir = USER_ID + "data/" + bulan + "/air";
  String air = Firebase.getString(pathAir); checkError();
  //  Serial.print("air = ");
  //  Serial.println(getAir+" L");
  return air;
}

String getBiaya() {
  String bulan = String(getBulan());
  // get data biaya
  String pathBiaya = USER_ID + "data/" + bulan + "/biaya";
  String biaya = Firebase.getString(pathBiaya); checkError();
  //  Serial.print("biaya = Rp.");
  //  Serial.println(getBiaya);
  return biaya;
}

int getBulan() {
  timeClient.update();
  unsigned long epochTime = timeClient.getEpochTime();

  String formattedTime = timeClient.getFormattedTime();

  //Get a time structure
  struct tm *ptm = gmtime ((time_t *)&epochTime);

  int currentMonth = ptm->tm_mon + 1;

  //  String currentMonthName = months[currentMonth - 1];
  //  Serial.print("Month name: ");
  //  Serial.println(currentMonthName);
  //
  //  int currentYear = ptm->tm_year + 1900;
  //  Serial.print("Year: ");
  //  Serial.println(currentYear);
  //
  //  //Print complete date:
  //  String currentDate = String(currentYear) + "-" + String(currentMonth) + "-" + String(monthDay);
  //  Serial.print("Current date: ");
  //  Serial.println(currentDate);
  //
  //  Serial.println("");
  return currentMonth;
}

void checkError() {
  // handle error
  if (Firebase.failed()) {
    Serial.print("setting /message failed:");
    Serial.println(Firebase.error());
    return;
  }
}
