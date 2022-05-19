package com.reactnativedemolibs;

import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

import java.util.HashMap;

@ReactModule(name = DemoLibsModule.NAME)
public class DemoLibsModule extends ReactContextBaseJavaModule {
  public static final String NAME = "DemoLibs";
  public static final String TAG = "DemoLibs";
  private CartReaderManager cardReader;
  private PrintController printController;

  public DemoLibsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    cardReader = new CartReaderManager(reactContext);
    printController = new PrintController(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(int a, int b, Promise promise) {
    promise.resolve(a * b);
  }

  public static native int nativeMultiply(int a, int b);

  @ReactMethod
  public void startScanCard(Promise promise) {
    Log.d(TAG, "startScanCard: ");
    cardReader.startScanCard(promise);
//    promise.resolve("startScanCard Native");
  }

  @ReactMethod
  public void stopScanCard() {
    cardReader.stopScanCard();
  }

  //---------------- Máy in -------------------
  @ReactMethod
  public void openPrinter() {
    Log.d(TAG, "openPrinter: ");
    printController.open();
  }

  @ReactMethod
  public void closePrinter() {
    Log.d(TAG, "closePrinter: ");
    printController.close();
  }

  @ReactMethod
  public void cleanCache() {
    Log.d(TAG, "cleanCache: ");
    printController.cleanCache();
  }

  @ReactMethod
  public void beginPrint() {
    Log.d(TAG, "beginPrint: ");
    printController.beginPrint();
  }

  @ReactMethod
  public void printTextLine(String content, int size, int position, boolean isBold, boolean isItalic, boolean isInvert) {
    Log.d(TAG, "printTextLine: content " + content);
    Log.d(TAG, "printTextLine: size " + size);
    Log.d(TAG, "printTextLine: position " + position);
//    HashMap data = params.toHashMap();
//    Log.d(TAG, "printTextLine: " + data.toString());
    printController.printTextLine(content, size, position, isBold, isItalic, isInvert);
  }

  @ReactMethod
  public void sleep(long ms){
    try{
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
