#include <Arduino.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <string>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <stdio.h>
#include <stdint.h>

const int oneWireBus = 1; // GPIO1
OneWire oneWire(oneWireBus);
DallasTemperature sensors(&oneWire);

#define SERVICE_UUID "12345678-1234-1234-1234-1234567890ab"
#define CHARACTERISTIC_UUID "abcdefab-1234-5678-9abc-def123456789"
#define MEASUREMENT_CHAR_UUID "12345678-1234-5678-0001-56789abcdef1"
#define INPUT_CHAR_UUID "12345678-1234-5678-0002-56789abcdef2"
// #define ALARM_CHAR_UUID
// Megfelelő méret: 4 (timestamp) + 2 + 3×2 + 4×2 + 4×2 + 2 = 30 byte
const int PAYLOAD_SIZE = 30;
uint8_t payload[PAYLOAD_SIZE] = {0};
int offset = 0;
typedef struct
{
  int32_t timestamps[1440];
  int16_t temperature[1440];
  int16_t accelerometerx[1440];
  int16_t accelerometery[1440];
  int16_t accelerometerz[1440];
  uint16_t frequency1[1440];
  uint16_t frequency2[1440];
  uint16_t frequency3[1440];
  uint16_t frequency4[1440];
  uint16_t amplitude1[1440];
  uint16_t amplitude2[1440];
  uint16_t amplitude3[1440];
  uint16_t amplitude4[1440];
  uint16_t battery_level[1440];
} store_data;
store_data data = {0};
BLECharacteristic *pCharacteristic;
BLECharacteristic *pMeasurementChar;
BLECharacteristic *pInputChar;
BLECharacteristic *pAlarmChar;
void send_data(int i);
// void randomData();

void get_data(int *get_timestamp)
{
}
void send_data(int i)
{
  // Timestamp (int32_t)
  int32_t ts = data.timestamps[i];
  payload[offset++] = (ts >> 24) & 0xFF;
  payload[offset++] = (ts >> 16) & 0xFF;
  payload[offset++] = (ts >> 8) & 0xFF;
  payload[offset++] = ts & 0xFF;

  // Temperature
  int16_t temp = data.temperature[i];
  payload[offset++] = (temp >> 8) & 0xFF;
  payload[offset++] = temp & 0xFF;
  // Accelerometer X/Y/Z
  int16_t ax = data.accelerometerx[i];
  int16_t ay = data.accelerometery[i];
  int16_t az = data.accelerometerz[i];
  payload[offset++] = (ax >> 8) & 0xFF;
  payload[offset++] = ax & 0xFF;
  payload[offset++] = (ay >> 8) & 0xFF;
  payload[offset++] = ay & 0xFF;
  payload[offset++] = (az >> 8) & 0xFF;
  payload[offset++] = az & 0xFF;
  // Frequency 1–4
  uint16_t f[4] = {
      data.frequency1[i], data.frequency2[i],
      data.frequency3[i], data.frequency4[i]};
  for (int j = 0; j < 4; j++)
  {
    payload[offset++] = (f[j] >> 8) & 0xFF;
    payload[offset++] = f[j] & 0xFF;
  }
  // Amplitude 1–4
  uint16_t a[4] = {
      data.amplitude1[i], data.amplitude2[i],
      data.amplitude3[i], data.amplitude4[i]};
  for (int k = 0; k < 4; k++)
  {
    payload[offset++] = (a[k] >> 8) & 0xFF;
    payload[offset++] = a[k] & 0xFF;
  }
  // Battery level
  uint16_t bat = data.battery_level[i];
  payload[offset++] = (bat >> 8) & 0xFF;
  payload[offset++] = bat & 0xFF;
}
void setup()
{
  Serial.begin(115200);
  sensors.begin();
  delay(1000);
  Serial.println("BLE Payload küldés és ellenőrzés indul...");
  BLEDevice::init("Agrosentinels_Payload");
  BLEServer *pServer = BLEDevice::createServer();
  BLEDevice::setMTU(247); // max 247
  BLEService *pService = pServer->createService(SERVICE_UUID);
  pCharacteristic = pService->createCharacteristic(
      CHARACTERISTIC_UUID,
      BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY);
  pCharacteristic->addDescriptor(new BLE2902());
  pMeasurementChar = pService->createCharacteristic(
      MEASUREMENT_CHAR_UUID,
      BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY);
  pMeasurementChar->addDescriptor(new BLE2902());
  pInputChar = pService->createCharacteristic(
      INPUT_CHAR_UUID,
      BLECharacteristic::PROPERTY_WRITE);
  pService->start();
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->start();
  Serial.println("BLE szolgáltatás elindult.");
  delay(1000);

  // randomData();

  // send_data();

  // int m=0;
  // delay(20000);
  // int l=0;
  // for(l=0 ;l<8; l++){
  // send_data(m);
  // m++;
  // }
  // pCharacteristic->setValue(payload, PAYLOAD_SIZE);
  // delay(500);
  // pCharacteristic->notify();
  // offset = 0;

  // memset(payload, 0, PAYLOAD_SIZE);
}
//}

// void randomData()
// {
//   for(int i=0; i<1440 ; i++){
//     data.timestamps[i] =0;
//     data.temperature[i] =0;
//     data.accelerometerx[i] = 0;
//     data.accelerometery[i] =0;
//     data.accelerometerz[i] = 0;
//     data.frequency1[i] = 0;
//     data.frequency2[i] = 0;
//     data.frequency3[i] = 0;
//     data.frequency4[i] = 0;
//     data.amplitude1[i] = 0;
//     data.amplitude2[i] = 0;
//     data.amplitude3[i] = 0;
//     data.amplitude4[i] = 0;
//     data.battery_level[i] = 0;
//   }
// }

void loop()
{
  sensors.requestTemperatures();
  delay(1000);
  float temp = sensors.getTempCByIndex(0);
  int temptiz = int(10 * temp);

  // int h=0;
  // if(h=1439){
  // h=0;
  //}
  // else{
  data.temperature[0] = temptiz;
  data.timestamps[0] = millis() / 1000 + 1751459000;
  // h++;
  send_data(0);
  pCharacteristic->setValue(payload, PAYLOAD_SIZE);
  pCharacteristic->notify();
  offset = 0;
}
