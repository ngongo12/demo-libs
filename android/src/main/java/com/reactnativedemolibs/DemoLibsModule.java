package com.reactnativedemolibs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import java.io.InputStream;

@ReactModule(name = DemoLibsModule.NAME)
public class DemoLibsModule extends ReactContextBaseJavaModule {
  public static final String NAME = "DemoLibs";
  public static final String TAG = "DemoLibs";
  private CartReaderManager cardReader;
  private PrintController printController;
  private Context context;

  public DemoLibsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    cardReader = new CartReaderManager(reactContext);
    printController = new PrintController(reactContext);
    this.context = reactContext;
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

  @SuppressLint({"ResourceType", "NewApi"})
  @ReactMethod
  public void printLogo(){
    try {
      Log.d(TAG, "printLogo: ");
      Resources resources = context.getResources();
      InputStream logo = resources.openRawResource(R.drawable.logo_header);
      BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, logo);
      Bitmap bitmap = bitmapDrawable.getBitmap();
      printController.bitmapPrintLine(bitmap, 1);
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }
}
