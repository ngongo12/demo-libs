package com.reactnativedemoemvcard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.emvcore.PosEmvErrorCode;
import com.reactnativedemoemvcard.card_reader.POIEmvCoreListener;

public class CartReaderManager {
  private final String TAG = "CartReaderManager";
  private boolean isFallBack = false;
  private POIEmvCoreManager emvCoreManager = POIEmvCoreManager.getDefault();
  private POIEmvCoreListener emvCoreListener = new POIEmvCoreListener();
//  private Promise promiseCardInformation;

  public void startScanCard(Promise promiseCardInformation) {
    Log.d(TAG, "startScanCard: ");
    try {
      Bundle bundle = new Bundle();
      bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_TYPE, POIEmvCoreManager.EMV_PAYMENT);
      bundle.putLong(POIEmvCoreManager.EmvTransDataConstraints.TRANS_AMOUNT, 15);
      bundle.putLong(POIEmvCoreManager.EmvTransDataConstraints.TRANS_AMOUNT_OTHER, 0);
      if (isFallBack) {
        bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_MODE, POIEmvCoreManager.DEVICE_MAGSTRIPE);
        bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.TRANS_FALLBACK, true);
      } else {
        int mode = 0;
        //??
//        mode = mode or POIEmvCoreManager.DEVICE_CONTACT;
//        mode = mode or POIEmvCoreManager.DEVICE_CONTACTLESS;
//        mode = mode or POIEmvCoreManager.DEVICE_MAGSTRIPE;
        bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_MODE, mode);
      }
      //???
      emvCoreListener.promiseCardInfomation = promiseCardInformation;
      int result = emvCoreManager.startTransaction(bundle, emvCoreListener);
      Log.i(TAG, "emvCoreManager.startTransaction");
      isFallBack = false;
      if (result == PosEmvErrorCode.EXCEPTION_ERROR){
        Log.e("Card reader", "startTransaction exception error");
        onTransEnd();
      }else if (PosEmvErrorCode.EMV_ENCRYPT_ERROR == result) {
        Log.e("Card reader", "startTransaction encrypt error");
        onTransEnd();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void onTransEnd(){
    Log.i(TAG, "onTransEnd");
    new Handler(Looper.getMainLooper()).postDelayed(()->{
      Log.i("Card reader", "onTransEnd");
    }, 300);
  }

  public void stopScanCard(){
    Log.i(TAG, "stopScanCard");
    emvCoreManager.stopTransaction();
  }
}
