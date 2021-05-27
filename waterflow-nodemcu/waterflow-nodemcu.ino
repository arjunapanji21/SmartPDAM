#include <FirebaseArduino.h>
#include <Firebase.h>
#include <FirebaseHttpClient.h>
#include <FirebaseCloudMessaging.h>
#include <FirebaseError.h>
#include <FirebaseObject.h>

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

// Set these to run example.
#define FIREBASE_HOST   "https://smartpdam-961a8-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH   "BbwxzHboCN7ULeHJUuRzpK8jhoBGtsDHdxxSOSI8"
#define WIFI_SSID       "Dea Lova"
#define WIFI_PASSWORD   "de@lova197050"

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
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  
}

void loop() {
  Firebase.set("test", 100);
  delay(2000);
  float a = Firebase.getFloat("test");
  Serial.print("a = ");
  Serial.println(a);
  delay(2000);
}

void setChildFloat(String child, float value){
  // set value
  Firebase.setFloat(child, value);
  // handle error
  checkError();
}

void setChildString(String child, String value){
  // set string value
  Firebase.setString(child, value);
  // handle error
  checkError();
}

void setChildBool(String child, boolean value){
  // set bool value
  Firebase.setBool(child, value);
  // handle error
  checkError();
}

void getChild(String child){
  // get value 
  Serial.print(child+": ");
  Serial.println(Firebase.getFloat(child));
  delay(1000);
}

void deleteChild(String child){
  // remove value
  Firebase.remove(child);
  delay(1000);
}

void checkError(){
  // handle error
  if (Firebase.failed()) {
      Serial.print("setting /message failed:");
      Serial.println(Firebase.error());  
      return;
  }
  delay(1000);
}
