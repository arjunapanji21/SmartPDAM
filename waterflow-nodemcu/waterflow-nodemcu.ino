// library untuk esp8266
#include <ESP8266WiFi.h>

// library untuk firebase
#include <FirebaseArduino.h>

// library untuk keterangan waktu (bulan)
#include <NTPClient.h>
#include <WiFiUdp.h>

#define FIREBASE_HOST   "smartpdam-961a8-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH   "BbwxzHboCN7ULeHJUuRzpK8jhoBGtsDHdxxSOSI8"
#define WIFI_SSID       "Dea Lova"
#define WIFI_PASSWORD   "de@lova197050"

//#define USER_ID         "users/PO2NWeXFMPQaxDlU5c609kDSVJf2/"
String USER_ID = "users/PO2NWeXFMPQaxDlU5c609kDSVJf2/";

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
}

void loop() {
  float air = getAir().toFloat();
  float biaya = getBiaya().toFloat();
  air = air;
  if (air <= 10000) {
    biaya = air * 2.6;
  }
  else if (air <= 20000) {
    biaya = air * 4.6;
  }
  else if (air <= 30000) {
    biaya = air * 7.4;
  }
  else {
    biaya = air * 10.7;
  }
  updateAir(air);
  updateBiaya(biaya);
//  Serial.print("Update Data Air: ");
//  Serial.println(air);
//  Serial.print("Update Data Biaya: Rp.");
//  Serial.println(biaya);
//  Serial.println("");
  delay(5);
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
}

void updateBiaya(int value) {
  String biaya = String(value);
  String bulan = String(getBulan());
  
  // update biaya
  String pathBiaya = USER_ID + "data/" + bulan + "/biaya";
  Firebase.setString(pathBiaya, biaya); checkError();
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
