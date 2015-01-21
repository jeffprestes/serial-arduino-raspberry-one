// PiTest.ino
// Communication test between RaspberryPi and Arduino

void setup() {
	Serial.begin(9600);
}

void loop() {
	Serial.println("Hello Pi");
	delay(2500);
}

