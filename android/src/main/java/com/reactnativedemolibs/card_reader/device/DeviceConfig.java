package com.reactnativedemolibs.card_reader.device;

public class DeviceConfig {

    public static final int DEVICE_P3 = 1;
    public static final int DEVICE_P4 = 2;

    public static boolean isHardwareKeyboard;
    public static boolean isHardwarePrinter;

    public static int    DUKPT_INDEX = 1;
    public static String DUKPT_IPEK  = "0123456789ABCDEF0123456789ABCDEF";
    public static String DUKPT_KSN   = "FFFF9080102495000001";

    public static int    PIN_INDEX = 2;
    public static String PIN_DATA  = "00112233445566778899AABBCCDDEEFF";

    public static void initDevice(int device) {
        if (device == DEVICE_P3) {
            isHardwareKeyboard = true;
            isHardwarePrinter = true;
        } else if (device == DEVICE_P4) {
            isHardwareKeyboard = false;
            isHardwarePrinter = false;
        }
    }
}
