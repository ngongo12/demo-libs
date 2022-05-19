package com.reactnativedemolibs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.emvcore.PosEmvErrorCode;
import com.reactnativedemolibs.card_reader.POIEmvCoreListener;
import com.reactnativedemolibs.card_reader.data.InjectorUtils;
import com.reactnativedemolibs.card_reader.data.TransactionData;
import com.reactnativedemolibs.card_reader.data.TransactionRepository;
import com.reactnativedemolibs.card_reader.utils.AppUtils;
import com.reactnativedemolibs.card_reader.utils.GlobalData;

public class CartReaderManager {
  private final String TAG = "CartReaderManager";
  private boolean isFallBack = false;
  private POIEmvCoreManager emvCoreManager;
  private POIEmvCoreListener emvCoreListener;
  private int transType = 1;
  private TransactionData transData;
  Promise promise;

  public CartReaderManager(Context context) {
    AppUtils.init(context);
    this.isFallBack = false;
    this.emvCoreManager = POIEmvCoreManager.getDefault();
    this.emvCoreListener = new POIEmvCoreListener(context);
    this.transType = GlobalData.getTransType();
    this.transData = new TransactionData();
  }
//  private Promise promiseCardInformation;

  @SuppressLint("DefaultLocale")
  public void startScanCard(Promise promiseCardInformation) {
    Log.d(TAG, "startScanCard: ");
    promise = promiseCardInformation;
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
        if (GlobalData.isSupportContact()) {
          mode |= POIEmvCoreManager.DEVICE_CONTACT;
        }
        if (GlobalData.isSupportContactless()) {
          mode |= POIEmvCoreManager.DEVICE_CONTACTLESS;
        }
        if (GlobalData.isSupportMagstripe()) {
          mode |= POIEmvCoreManager.DEVICE_MAGSTRIPE;
        }
        Log.d(TAG, "startScanCard: Trans Mode: " + mode);
        bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_MODE, mode);
        Log.d(TAG, "startScanCard: isSupportAppleVas: " + GlobalData.isSupportAppleVas());
        bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.APPLE_VAS, GlobalData.isSupportAppleVas());
      }
      bundle.putInt(POIEmvCoreManager.EmvTransDataConstraints.TRANS_TIMEOUT, 60);
      bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.SPECIAL_CONTACT, false);
      bundle.putBoolean(POIEmvCoreManager.EmvTransDataConstraints.SPECIAL_MAGSTRIPE, false);

      transData = new TransactionData();
      transData.setTransType(transType);
      transData.setTransAmount((double) 15);
      transData.setTransAmountOther((double) 0);
      transData.setTransResult(PosEmvErrorCode.EMV_OTHER_ERROR);
      //??
      emvCoreListener.promiseCardInfomation = promiseCardInformation;

      int result = emvCoreManager.startTransaction(bundle, emvCoreListener);
      Log.i(TAG, "emvCoreManager.startTransaction");
      isFallBack = false;
      Log.d(TAG, "result: " + result);
      if (result == PosEmvErrorCode.EXCEPTION_ERROR) {
        Log.e("Card reader", "startTransaction exception error");
        throwError(result+"", "startTransaction exception error");
        onTransEnd();
      } else if (result == PosEmvErrorCode.EMV_ENCRYPT_ERROR) {
        Log.e("Card reader", "startTransaction encrypt error");
        throwError(result+"", "startTransaction encrypt error");
        onTransEnd();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void onTransEnd() {
    Log.i(TAG, "onTransEnd");
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
      Log.i("Card reader", "onTransEnd");
    }, 300);
  }

  public void stopScanCard() {
    Log.i(TAG, "stopScanCard");
    emvCoreManager.stopTransaction();
  }

  private void throwError(String code, String message) {
    if(promise != null){
      promise.reject(code, message);
    }
  }
}
