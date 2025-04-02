package com.example.customerlauncher.service.ledcontrol;


/**
 * Interface for System Service
 */
/** {@hide} */
interface ILedControl {
    void switchLedColorsOnDeviceState(String deviceState);
    void handleOnRcuPress();
    void handleManualPairing();
    void handlePulseLedFrom15To100();
    void ledOff(String ledColor);
    void ledOn(String ledColor);
}
