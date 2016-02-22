#include <Wire.h>
#include <SPI.h>
#include <Adafruit_PN532.h>
#define PN532_SCK  (7)
#define PN532_MOSI (5)
#define PN532_SS   (4)
#define PN532_MISO (6)
#define PN532_IRQ   (2)
#define PN532_RESET (3) 
#define led (13) 
Adafruit_PN532 nfc(PN532_SCK, PN532_MISO, PN532_MOSI, PN532_SS);
int mode=3;
void setup(void) {
  Serial.begin(9600);
  nfc.begin();
  uint32_t versiondata = nfc.getFirmwareVersion();
  if (! versiondata) {
    Serial.print("Didn't find PN53x board");
    while (1); // halt
  }
  Serial.print("Found chip PN5"); Serial.println((versiondata>>24) & 0xFF, HEX); 
  Serial.print("Firmware ver. "); Serial.print((versiondata>>16) & 0xFF, DEC); 
  Serial.print('.'); Serial.println((versiondata>>8) & 0xFF, DEC);
  Serial.println("Hello! Now start NFC reader.");
  nfc.setPassiveActivationRetries(0xFF);
  nfc.SAMConfig();
}

void loop(void) {
  uint8_t success;
  uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
  uint8_t uidLength;
  uint8_t dataLength;
     digitalWrite(led,LOW);
     Serial.flush();
     Serial.println("Now ready to read data,plaese put Mifare Ultralight Tag on the reader!");
     success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
  if (success) {
    digitalWrite(led,HIGH);
    Serial.println("Found an ISO14443A card");
    Serial.print("  UID Length: ");Serial.print(uidLength, DEC);Serial.println(" bytes");
    Serial.print("  UID Value: ");
    nfc.PrintHex(uid, uidLength);
    Serial.println("");    
    if (uidLength == 7)
    {
      uint8_t data[32];
      Serial.println("Got Mifare Ultralight Tag (7 byte UID)!");   
      for (uint8_t i = 7; i < 40; i++) 
      {
        success = nfc.ntag2xx_ReadPage(i, data);
        if (success) 
        {
          nfc.PrintHexChar(data, 4);}
      }      
    }
    else
    {Serial.println("This doesn't Mifare Ultralight Tag");
    delay(1000);
    return;
    }
  Serial.println("Now ready to update Mifare Ultralight Tag data and press any key to continue!");
  while (!Serial.available());
  while (Serial.available()) 
  Serial.read();
  Serial.flush();
  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
  if (success) 
  {
      uint8_t data[32];
      memset(data, 0, 4);
      success = nfc.ntag2xx_ReadPage(3, data);
      if (!success)
      {
        Serial.println("Unable to read the Capability Container (page 3)");
        return;
      }
      else
      {
        if (!((data[0] == 0xE1) && (data[1] == 0x11)))
        {
          Serial.println("This doesn't seem to be an NDEF formatted tag.");
          Serial.println("Page 3 should start with 0xE1 0x10.");
        }
        else
        {
          dataLength = data[2]*8;
          Serial.flush();
          Serial.println("Input string:");
          while (!Serial.available());
          while (Serial.available())
    {
          String str;
          str = Serial.readStringUntil('\n');
          char * url="help!";
          str.toCharArray(url,str.length() + 1);
          Serial.print("Erasing previous data area ");
          for (uint8_t i = 6; i < 34; i++) 
          {
            memset(data, 0, 4);
            success = nfc.ntag2xx_WritePage(i, data);
            Serial.print(".");
            if (!success)
            {
              Serial.println(" ERROR!");
              delay(1000);
              return;
            }
          }
          Serial.println(" DONE!");         
          Serial.print("Writing:");
          Serial.print(url);
          Serial.print(" ...");
          uint8_t ndefprefix = NDEF_URIPREFIX_NONE;
          success = nfc.ntag2xx_WriteNDEFURI(ndefprefix, url, dataLength);
          if (success)
          {
            Serial.println("DONE!");
            Serial.println("Put out Mifare Ultralight Tag and wait a second!");
            digitalWrite(led,LOW);
            delay(5000);
            
          }
          else
          {
            Serial.println("ERROR!");
            digitalWrite(led,LOW);
            delay(1000);
            return;
          }
         }                       
        } 
      } 
    } 
  }
  }
