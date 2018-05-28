/*************************************************** 
  This is an example for our Adafruit 16-channel PWM & Servo driver
  Servo test - this will drive 16 servos, one after the other

  Pick one up today in the adafruit shop!
  ------> http://www.adafruit.com/products/815

  These displays use I2C to communicate, 2 pins are required to  
  interface. For Arduino UNOs, thats SCL -> Analog 5, SDA -> Analog 4

  Adafruit invests time and resources providing this open source code, 
  please support Adafruit and open-source hardware by purchasing 
  products from Adafruit!

  Written by Limor Fried/Ladyada for Adafruit Industries.  
  BSD license, all text above must be included in any redistribution
 ****************************************************/

#include <Wire.h>
#include <Adafruit_PWMServoDriver.h>

// called this way, it uses the default address 0x40
Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver(0x40);
// you can also call it with a different address you want
//Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver(0x41);

// Depending on your servo make, the pulse width min and max may vary, you 
// want these to be as small/large as possible without hitting the hard stop
// for max range. You'll have to tweak them as necessary to match the servos you
// have!
#define SERVOXMIN  220 // this is the 'minimum' pulse length count (out of 4096)
#define SERVOXMAX  340 // this is the 'maximum' pulse length count (out of 4096)

#define SERVOYMIN  145 // this is the 'minimum' pulse length count (out of 4096)
#define SERVOYMAX  480 // this is the 'maximum' pulse length count (out of 4096)

#define SERVOZMIN  120 // this is the 'minimum' pulse length count (out of 4096)
#define SERVOZMAX  445 // this is the 'maximum' pulse length count (out of 4096)

// our servo # counter
uint8_t servonum = 0;
int servpulse = 300;
String inString;

void setup() {
  Serial.begin(115200);
  pwm.begin();
  pwm.setPWMFreq(60);  // Analog servos run at ~60 Hz updates
  yield();
  pwm.setPWM(0, 0, constrain(map(0, -100, 100, SERVOXMIN, SERVOXMAX), SERVOXMIN, SERVOXMAX));
  pwm.setPWM(1, 0, constrain(map(0, -100, 100, SERVOYMIN, SERVOYMAX), SERVOYMIN, SERVOYMAX));
  pwm.setPWM(2, 0, constrain(map(50, -100, 100, SERVOZMIN, SERVOZMAX), SERVOZMIN, SERVOZMAX));
}
 
void loop() {
  while (Serial.available()) {
    char inChar = Serial.read();
    if (inChar != '&') {
      inString += inChar; 
    } else {
      //Serial.print((int) inString.charAt(0));
      //Serial.println(inString);
      int input = 0;
      switch ((int) inString.charAt(0)){
        case 120 : //x
          //Serial.println("x " + inString.substring(1));
          //Serial.println(inString.substring(1).toInt());
          pwm.setPWM(0, 0, constrain(map(inString.substring(1).toInt(), -100, 100, SERVOXMIN, SERVOXMAX), SERVOXMIN, SERVOXMAX));
          //pwm.setPWM(0, 0, constrain(inString.substring(1).toInt(), SERVOXMIN, SERVOXMAX));
          break;
        case 121 : //y
          //Serial.println("y " + inString.substring(1));
          //Serial.println(inString.substring(1).toInt());
          pwm.setPWM(1, 0, constrain(map(inString.substring(1).toInt(), -100, 100, SERVOYMIN, SERVOYMAX), SERVOYMIN, SERVOYMAX));
          //pwm.setPWM(1, 0, constrain(inString.substring(1).toInt(), SERVOYMIN, SERVOYMIN));
          break;
        case 122 : //z
          //Serial.println("z " + inString.substring(1));
          //Serial.println(inString.substring(1).toInt());
          pwm.setPWM(2, 0, constrain(map(inString.substring(1).toInt(), -100, 100, SERVOZMIN, SERVOZMAX), SERVOZMIN, SERVOZMAX));
          //pwm.setPWM(2, 0, constrain(inString.substring(1).toInt(), SERVOZMIN, SERVOZMIN));
          break;
        case 102 :
            if ((int)inString.charAt(1) == 116) {
              Serial.println("fire on");
            } else {
              Serial.println("fire off");
            }
      }
      inString = "";
    }
  }
}




 
