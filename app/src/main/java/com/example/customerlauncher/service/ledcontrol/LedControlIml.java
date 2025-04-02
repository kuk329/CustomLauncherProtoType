package com.example.customerlauncher.service.ledcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

public class LedControlIml extends ILedControl.Stub{

    private static final String TAG = LedControlIml.class.getSimpleName(); // 로그 출력을 위한 TAG로, 현재 클래스 이름 "LedControlIml" 을 사용용
    private final Context mContext; // 안드로이드 시스템의 Context를 저장해두는 변수. 서비스, 브로드캐스트 등록 등 시스템 기능을 사용할 때 필요요
    private final IKaonDeviceControl mKaonDeviceControl; // 실제 LED를 제어하는 하드웨어 제어 인터페이스. HAL(Service)과 통신하기 위해 사용한다.
    private BroadcastReceiver myReceiver; // 시스템 이벤트를 감지하기 위해 등록할 브로드캐스트 리시버 객체
    private boolean isHdmiConnected = true; // HDMI 연결 여부와 기기의 화면 상태(켜짐/꺼짐)를 저장하는 플래그
    private boolean deviceIsOn = true; // HDMI 연결 여부와 기기의 화면 상태(켜짐/꺼짐)를 저장하는 플래그
    final String actionStartPairing = "com.android.tv.settings.accessories.STATE_START_PAIRING"; // 리모컨 페어링 시작/종료 이벤트를 감지하기 위한 intent action 문자열열
    final String actionEndPairing = "com.android.tv.settings.accessories.STATE_END_PAIRING";

    private void setAndroidBootCompleted() { // 기기 부팅이 완료되었음을 하드웨어 컨트롤러에 알려주는 함수. (에러는 무시)
        try {
            mKaonDeviceControl.setAndroidBootCompleted();
        } catch(RemoteException e){}
    }

    public LedControlIml(Context context) { // 생성자 (클래스 생성될 때 초기화)
        mContext = context;
        final String fqName = IKaonDeviceControl.DESCRIPTOR + "/default"; // HAL서비스의 이름을 구성. AIDL 서비스의 full name
        mKaonDeviceControl = IKaonDeviceControl.Stub.asInterface(ServiceManager.waitForDeclaredService(fqName)); // ServiceManager에서 하드웨어 제어 서비스 인스턴스를 가져와서 바인딩
        registerReceiverBroadCast(); // 브로드캐스트 리시버를 등록하여 다양한 시스템 이벤트를 감지할 수 있도록 한다.
        setAndroidBootCompleted(); // 부팅 완료를 시스템(HAL)에 알려준다.
        deviceIsOn = true; // 기기의 상태를 켜짐으로 설정
        ledOn("LED_WHITE"); // LED를 초기 상태로 설정. 흰색 켜고 빨간색을 끔 -> 우리는 반대?
        ledOff("LED_RED");
        Log.d(TAG, "isNetworkAvailable: " + isNetworkAvailable(context)); // 현재 네트워크 연결 상태를 로그로 출력
        if (isNetworkAvailable(context)) { // 네트워크 상태에 따라 LED 상태를 업데이트. (에러시 빨간 LED)
            updateState(1, true);
            stopRedLedBlinkError();
        } else {
            updateState(1, false);
            startRedLedBlinkError();
        }
        handleHdmiBehavior(isHdmiConnected); // HDMI 연결 상태에 따라 LED 상태를 설정
    }


    private void registerReceiverBroadCast() { // 시스템 이벤트(HDMI 연결, 화면 켜짐, 페어링 시작 등)를 감지할 브로드캐스트 리시버 등록 함수
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) { // 수신한 인텐트를 처리할 익명 브로드캐스트 리시버 객체.
                String action = intent.getAction(); // 어떤 이벤트가 발생했는지 로그로 남기기 위해 액션 이름을 가져온다.
                Log.d(TAG, "registerReceiverBroadCast - isScreenOn: " + deviceIsOn + ", action: " + action);

                if ("com.android.service.WAKEUP_FEEDBACK".equals(action)) { // 화면이 켜졌을 때 LED를 "on" 상태로 설정
                    deviceIsOn = true;
                    switchLedColorsOnDeviceState("on");
                } else if ("com.android.service.GOTO_SLEEP_FEEDBACK".equals(action)) { // 화면 꺼졌을 때 "off" 상태로 바꾸고 LED도 꺼지게
                    Log.d(TAG, "Received GOTO_SLEEP_FEEDBACK");
                    deviceIsOn = false;
                    switchLedColorsOnDeviceState("off");
                } else if (actionStartPairing.equals(action)) { // 페어링 시작 이벤트가 들어오면 해당 LED 효과를 실행
                    Log.d(TAG, "Received actionStartPairing");
                    handleManualPairing();
                } else if (actionEndPairing.equals(action)) { // 페어링이 끝나면 LED를 원래 상태로 복원
                    Log.d(TAG, "Received actionEndPairing");
                    endPairingRCU();
                } else if(Intent.ACTION_SHUTDOWN.equals(action)) { // 시스템 종료 전 LED를 꺼주거나 마무리 정리리
                    shutdownDevice();
                } else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action) && deviceIsOn){ // 네트워크가 연결되었는지 여부에 따라 빨간 LED를 켜거나 끈다.
                    if (isNetworkAvailable(context)) {
                        updateState(1, true);
                        stopRedLedBlinkError();
                    } else {
                        startRedLedBlinkError();
                        updateState(1, false);
                    }
                }
                if ("android.intent.action.HDMI_PLUGGED".equals(action) && deviceIsOn) { // HDMI가 연결되었는지 여부를 받아서 LED 상태를 HDMI 연결 상태에 맞게 바꾼다.
                    boolean connected = intent.getBooleanExtra("state", false);
                    isHdmiConnected = connected;
                    handleHdmiBehavior(connected);
                }
            }
        };

        /**
         * 인텐트 필터 만들고 등록 -> 어떤 이벤트를 감지할지 정의하는 인텐트 필터터
         */
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.service.WAKEUP_FEEDBACK");
        filter.addAction("com.android.service.GOTO_SLEEP_FEEDBACK");
        filter.addAction("android.intent.action.BOOT_COMPLETED");
        filter.addAction("android.intent.action.HDMI_PLUGGED");
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(actionStartPairing);
        filter.addAction(actionEndPairing);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        mContext.registerReceiver(myReceiver, filter, Context.RECEIVER_EXPORTED); // 정의한 리시버를 시스템에 등록. 외부 브로트캐스트도 받을 수 있도록 RECEIVER_EXPORTED 설정
    }

    private void handleHdmiBehavior(boolean connected) { // HDMI 관련 동작 관리 함수
        if(connected) {
            stopRedLedBlinkHdmiOff(); // 비연결 상태에서 깜빡이던 빨간 LED를 멈춘다.
            updateState(0, true); // 내부 상태값을 업데이트
            Log.d(TAG, "HDMI connect"); // HDMI 연결 로그 출력력
            Log.d(TAG, "isNetworkAvailable: " + isNetworkAvailable(mContext));
            if (isNetworkAvailable(mContext)) { // 네트워크가 연결되어 있다면
                stopRedLedBlinkError(); // 에러 표시용 빨간 LED 꺼줌
                ledOn("WHITE_LED"); // 화이트 LED 켬
            } else {
                startRedLedBlinkError();
            }
        } else { // HDMI 끊겨있을 때
            updateState(0, false); // 내부 상태값을 업데이트
            Log.d(TAG, "HDMI disconnect"); // HDMI 연결 해체체 로그 출력력
            ledOff("WHITE_LED"); // 화이트 LED 끔
            handlePulseLedFrom15To100();
        }
    }

    private void stopRedLedBlinkHdmiOff() {
        try {
            mKaonDeviceControl.stopRedLedBlinkHdmiOff();
        } catch(RemoteException e){}
    }

    private void endPairingRCU() {
        try {
            mKaonDeviceControl.endPairingRCU();
        } catch(RemoteException e){}
    }
    void resetStateLedScreenOff() {
        stopRedLedBlinkError();
        stopRedLedBlinkHdmiOff();
        ledOff("LED_WHITE");
        ledOn("LED_RED");
    }

    private void shutdownDevice() {
        try {
            mKaonDeviceControl.stopRedLedBlinkError();
            mKaonDeviceControl.stopRedLedBlinkHdmiOff();
            deviceIsOn = false;
            switchLedColorsOnDeviceState("off");
        } catch(RemoteException e){}
    }



    private final IKaonCallbackPower.Stub mCall = new IKaonCallbackPower.Stub() {
        @Override
        public void onCompleted() {
            Log.d(TAG, "LED color switched successfully. deviceIsOn: " + deviceIsOn);
            if(deviceIsOn){
                if (isNetworkAvailable(mContext)) {
                    stopRedLedBlinkError();
                } else {
                    startRedLedBlinkError();
                    return;
                }

                handleHdmiBehavior(isHdmiConnected);

            } else {
                resetStateLedScreenOff();
            }

        }
        @Override
        public String getInterfaceHash() {
            return "IKaonCallbackPower";
        }
        @Override
        public int getInterfaceVersion() {
            return IKaonCallbackPower.VERSION;
        }
    };


    @Override
    public void switchLedColorsOnDeviceState(String deviceState) {
        try {
            int deviceEnumState ;
            if (deviceState.equals("on")) {
                deviceEnumState = DeviceOnOff.ON;
            } else {
                deviceEnumState = DeviceOnOff.OFF;
            }
            mKaonDeviceControl.switchLedColorsOnDeviceState(deviceEnumState, mCall);


        } catch(RemoteException e){}
    }

    @Override
    public void handleOnRcuPress() {
        try {
            mKaonDeviceControl.handleOnRcuPress();
        } catch(RemoteException e){}
    }

    @Override
    public void handleManualPairing() {
        try {
            mKaonDeviceControl.handleManualPairing();
        } catch(RemoteException e){}
    }

    @Override
    public void handlePulseLedFrom15To100() {
        try {
            mKaonDeviceControl.handlePulseLedFrom15To100(LedColor.RED);
        } catch(RemoteException e){}
    }

    @Override
    public void ledOn(String ledColor) {
        try {
            if(ledColor.equals("LED_RED")) {
                mKaonDeviceControl.ledOn(LedColor.RED);
            } else {
                mKaonDeviceControl.ledOn(LedColor.WHITE);
            }

        }catch(RemoteException e){}
    }

    @Override
    public void ledOff(String ledColor) {
        try {
            if(ledColor.equals("LED_RED")) {
                mKaonDeviceControl.ledOff(LedColor.RED);
            } else {
                mKaonDeviceControl.ledOff(LedColor.WHITE);
            }

        } catch(RemoteException e) {}
    }
    @Override
    protected void finalize() throws Throwable {
        try {
            mContext.unregisterReceiver(myReceiver);
            ledOn("LED_RED");
            ledOff("LED_WHITE");
            Log.d(TAG, "BroadcastReceiver unregistered");
        } finally {
            super.finalize();
        }
    }

    private void updateState(int state, boolean value) {
        try {
            mKaonDeviceControl.setConnectStatus(state, value);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startRedLedBlinkError(){
        Log.d(TAG, "Start Led red blink");
        try {
            mKaonDeviceControl.startRedLedBlinkError();
        } catch(RemoteException e) {}

    }

    private void stopRedLedBlinkError(){
        Log.d(TAG, "Stop Led red blink");
        try {
            mKaonDeviceControl.stopRedLedBlinkError();
            if(isScreenOn(mContext)){
                if (!isHdmiConnected) {
                    return;
                }
                ledOn("LED_WHITE");
                ledOff("LED_RED");
            }else{
                ledOff("LED_WHITE");
                ledOn("LED_RED");
            }
        } catch(RemoteException e) {}
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    public boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isInteractive();
    }
}
