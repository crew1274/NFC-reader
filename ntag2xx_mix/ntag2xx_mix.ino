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
  nfc.setPassiveActivationRetries(0xFF);
  nfc.SAMConfig();
  Serial.println("Waiting for an ISO14443A card");
}

void loop(void) {
  uint8_t success;
  uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };	// Buffer to store the returned UID
  uint8_t uidLength;
  uint8_t dataLength;
  digitalWrite(led,LOW);
  Serial.println("Plaese choose mode(1=Read Serial Number,2=Read Tag,3=Update Tag Data):");
   Serial.flush();
    while (!Serial.available());
    while (Serial.available()) {
    mode=Serial.parseInt();
  switch (mode) {
    case 1:
      Serial.println("Now ready to read serial number!");
       success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, &uid[0], &uidLength);
  if (success) {
    digitalWrite(led,HIGH);
    Serial.println("Found a card!");
    Serial.print("UID Length: ");Serial.print(uidLength, DEC);Serial.println(" bytes");
    Serial.print("UID Value: ");
    for (uint8_t i=0; i < uidLength; i++) 
    {
      Serial.print(" 0x");Serial.print(uid[i], HEX); 
    }
    Serial.println("Waiting for a card...");
    delay(1000);
  }
      break;
    case 2:
     Serial.println("Now ready to read tag data");
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
      Serial.println("Seems to be an NTAG2xx tag (7 byte UID)");   
      for (uint8_t i = 7; i < 40; i++) 
      {
        success = nfc.ntag2xx_ReadPage(i, data);
        if (success) 
        {
          nfc.PrintHexChar(data, 4);}
      }      
    }
    else
    {Serial.println("This doesn't NTAG203 tag (UUID length != 7 bytes)!");}
    }
     break;
      case 3:
      Serial.println("Now ready to update tag data");
      Serial.println("Place your NDEF formatted NTAG2xx tag on the reader to update the");
  Serial.println("NDEF record and press any key to continue ...");
  // Wait for user input before proceeding
  while (!Serial.available());
  // a key was pressed1
  while (Serial.available()) 
  Serial.read();
  Serial.flush();
  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);
  if (success) 
  {
    // 2.) Display some basic information about the card
    digitalWrite(led,HIGH);
    Serial.println("Found an ISO14443A card");
    Serial.print("  UID Length: ");Serial.print(uidLength, DEC);Serial.println(" bytes");
    Serial.print("  UID Value: ");
    nfc.PrintHex(uid, uidLength);
    Serial.println("");
    if (uidLength != 7)
    {
      Serial.println("This doesn't seem to be an NTAG203 tag (UUID length != 7 bytes)!");
    }
    else
    {
      uint8_t data[32];
      Serial.println("Seems to be an NTAG2xx tag (7 byte UID)");    
      memset(data, 0, 4);
      success = nfc.ntag2xx_ReadPage(3, data);
      if (!success)
      {
        Serial.println("Unable to read the Capability Container (page 3)");
        return;
      }
      else
      {
        // If the tag has already been formatted as NDEF, byte 0 should be:
        // Byte 0 = Magic Number (0xE1)
        // Byte 1 = NDEF Version (Should be 0x10)
        // Byte 2 = Data Area Size (value * 8 bytes)
        // Byte 3 = Read/Write Access (0x00 for full read and write)
        if (!((data[0] == 0xE1) && (data[1] == 0x11)))
        {
          Serial.println("This doesn't seem to be an NDEF formatted tag.");
          Serial.println("Page 3 should start with 0xE1 0x10.");
        }
        else
        {
          dataLength = data[2]*8;
          Serial.print("Tag is NDEF formatted. Data area size = ");
          Serial.print(dataLength);
          Serial.println(" bytes");
          Serial.flush();
          Serial.println("Enter string:");
          while (!Serial.available());
    while (Serial.available())
    {
          String str;
          str = Serial.readStringUntil('\n');
          char * url="help!";
          str.toCharArray(url,str.length() + 1);
          // 5.) Erase the old data area
          Serial.print("Erasing previous data area ");
          for (uint8_t i = 6; i < (dataLength/4)+4; i++) 
          {
            memset(data, 0, 4);
            success = nfc.ntag2xx_WritePage(i, data);
            Serial.print(".");
            if (!success)
            {
              Serial.println(" ERROR!");
              return;
            }
          }
          Serial.println(" DONE!");         
          // 6.) Try to add a new NDEF URI record
          Serial.print("Writing ");
          Serial.print(url);
          Serial.println(" ...");
          uint8_t ndefprefix = NDEF_URIPREFIX_NONE;
          success = nfc.ntag2xx_WriteNDEFURI(ndefprefix, url, dataLength);
          if (success)
          {
            Serial.println("DONE!");
          }
          else
          {
            Serial.println("ERROR!");
          }
         }                       
        } 
      } 
    } 
  }
      break;
    default: 
      //Serial.println("Error!");
    break;
  }
  }
  }
