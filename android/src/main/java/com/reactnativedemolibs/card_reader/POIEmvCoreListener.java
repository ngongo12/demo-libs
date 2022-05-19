package com.reactnativedemolibs.card_reader;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.pos.sdk.emvcore.IPosEmvCoreListener;
import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.emvcore.POIEmvCoreManager.EmvCardInfoConstraints;
import com.pos.sdk.emvcore.POIEmvCoreManager.EmvOnlineConstraints;
import com.pos.sdk.emvcore.PosEmvErrorCode;
import com.reactnativedemolibs.card_reader.data.TransactionData;
import com.reactnativedemolibs.card_reader.device.DeviceConfig;
import com.reactnativedemolibs.card_reader.emv.utils.EmvCard;
import com.reactnativedemolibs.card_reader.emv.utils.EmvTrack1;
import com.reactnativedemolibs.card_reader.utils.AppExecutors;
import com.reactnativedemolibs.card_reader.utils.GlobalData;
import com.reactnativedemolibs.card_reader.utils.tlv.BerTlv;
import com.reactnativedemolibs.card_reader.utils.tlv.BerTlvBuilder;
import com.reactnativedemolibs.card_reader.utils.tlv.BerTlvParser;
import com.reactnativedemolibs.card_reader.utils.tlv.BerTlvs;
import com.reactnativedemolibs.card_reader.utils.tlv.HexUtil;
import com.reactnativedemolibs.card_reader.view.PasswordDialog;

import java.util.List;

public class POIEmvCoreListener extends IPosEmvCoreListener.Stub {

  private final String TAG = "POIEmvCoreListener";
  private TransactionData transactionData;
  public Promise promiseCardInfomation;
  private POIEmvCoreManager emvCoreManager;
  private Context context;

  public POIEmvCoreListener(Context context) {
    transactionData = new TransactionData();
    promiseCardInfomation = null;
    emvCoreManager = POIEmvCoreManager.getDefault();
    this.context = context;
  }

  @Override
  public void onEmvProcess(int type, Bundle bundle) throws RemoteException {
    Log.d(TAG, "onEmvProcess: ");
    transactionData.setCardType(type);
    AppExecutors.getInstance().mainThread().execute(() -> {
      switch (type) {
        case POIEmvCoreManager.DEVICE_CONTACT:
          Log.i(TAG, "Contact Card Trans");
          break;
        case POIEmvCoreManager.DEVICE_CONTACTLESS:
          Log.i(TAG, "Contactless Card Trans");
          break;
        case POIEmvCoreManager.DEVICE_MAGSTRIPE:
          Log.i(TAG, "Magstripe Card Trans");
          break;
        default:
          Log.i(TAG, "Not found");
      }
      Log.i(TAG, "Processing");

      byte[] data = bundle.getByteArray("track1");
//      EmvCard track1 = new EmvCard(data);
//      data = bundle.getByteArray("track2");

      Log.d(TAG, "onEmvProcess: data: " + bundle.toString());
    });

  }

  @Override
  public void onSelectApplication(List<String> list, boolean b) throws RemoteException {
    Log.d(TAG, "onSelectApplication: ");
    AppExecutors.getInstance().mainThread().execute(new Runnable() {
      @Override
      public void run() {
        String[] name = list.toArray(new String[0]);
        Log.i(TAG, "run: " + name.toString());
      }
    });

  }

  @Override
  public void onConfirmCardInfo(int mode, Bundle bundle) throws RemoteException {
    Log.d(TAG, "onConfirmCardInfo: ");
    Bundle outBundle = new Bundle();
    switch (mode) {
      case POIEmvCoreManager.CMD_AMOUNT_CONFIG: {
        outBundle.putString(EmvCardInfoConstraints.OUT_AMOUNT, "11");
        outBundle.putString(EmvCardInfoConstraints.OUT_AMOUNT_OTHER, "22");
        break;
      }
      case POIEmvCoreManager.CMD_TRY_OTHER_APPLICATION:
      case POIEmvCoreManager.CMD_ISSUER_REFERRAL: {
        outBundle.putBoolean(EmvCardInfoConstraints.OUT_CONFIRM, true);
        break;
      }
    }
    emvCoreManager.onSetCardInfoResponse(outBundle);
  }

  @Override
  public void onKernelType(int type) throws RemoteException {
    Log.d(TAG, "onKernelType: ");
    transactionData.setCardType(type);

  }

  @Override
  public void onSecondTapCard() throws RemoteException {
    AppExecutors.getInstance().mainThread().execute(new Runnable() {
      @Override
      public void run() {
        Log.i(TAG, "run: onSecondTapCard");
      }
    });

  }

  @Override
  public void onRequestInputPin(Bundle bundle) throws RemoteException {
    Log.d(TAG, "onRequestInputPin: ");
    AppExecutors.getInstance().mainThread().execute(new Runnable() {
      @Override
      public void run() {
        boolean isIcLost = transactionData.getCardType() ==POIEmvCoreManager.DEVICE_CONTACT;
        PasswordDialog dialog = new PasswordDialog(context, isIcLost, bundle, DeviceConfig.PIN_INDEX);
//        dialog.showDialog();
        byte[] data = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.EMV_DATA);
        Log.d(TAG, "mainThread: onRequestInputPin: " + bundle.toString());
      }
    });

  }

  @Override
  public void onRequestOnlineProcess(Bundle bundle) throws RemoteException {
    Log.d(TAG, "onRequestOnlineProcess: " + bundle.toString());
    AppExecutors.getInstance().mainThread().execute(new Runnable() {
      @Override
      public void run() {
        Log.i(TAG, "onRequestOnlineProcess");
      }
    });

    AppExecutors.getInstance().networkIO().execute(new Runnable() {
      @Override
      public void run() {
        byte[] data;
        int vasResult;
        byte[] vasData;
        byte[] vasMerchant;
        int encryptResult;
        byte[] encryptData;

        vasResult = bundle.getInt(EmvOnlineConstraints.APPLE_RESULT, PosEmvErrorCode.APPLE_VAS_UNTREATED);
        encryptResult = bundle.getInt(EmvOnlineConstraints.ENCRYPT_RESULT, PosEmvErrorCode.EMV_UNENCRYPTED);
        if(vasResult != -1){
          Log.d(TAG, "Vas Result: " + vasResult);
        }
        if(encryptResult != -1){
          Log.d(TAG, "EncryptResult: " + encryptResult);
        }

        vasData = bundle.getByteArray(EmvOnlineConstraints.APPLE_DATA);
        if(vasData != null){
          Log.d(TAG, "Vas Data: " + HexUtil.toHexString(vasData));
        }
        vasMerchant = bundle.getByteArray(EmvOnlineConstraints.APPLE_MERCHANT);
        if(vasMerchant != null){
          Log.d(TAG, "Vas Merchant: " + HexUtil.toHexString(vasMerchant));
        }
        data = bundle.getByteArray(EmvOnlineConstraints.EMV_DATA);
        if (data != null) {
          Log.d(TAG, "Trans Data : " + HexUtil.toHexString(data));
          EmvCard emvCard = new EmvCard(data);
          Log.d(TAG, "Trans Data: " +  emvCard);
          promiseCardInfomation.resolve(emvCard.convertWritable());
        }
        encryptData = bundle.getByteArray(EmvOnlineConstraints.ENCRYPT_DATA);
        if (encryptData != null) {
          Log.d(TAG, "Encrypt Data : " + HexUtil.toHexString(encryptData));
        }

        if (data != null) {
          BerTlvBuilder tlvBuilder = new BerTlvBuilder();
          BerTlvParser tlvParser = new BerTlvParser();
          BerTlvs tlvs = tlvParser.parse(data);
          for (BerTlv tlv : tlvs.getList()) {
            tlvBuilder.addBerTlv(tlv);
          }

          if (encryptResult == PosEmvErrorCode.EMV_OK && encryptData != null) {
            BerTlvs encryptTlvs = new BerTlvParser().parse(encryptData);
            for (BerTlv tlv : encryptTlvs.getList()) {
              tlvBuilder.addBerTlv(tlv);
            }
          }

          data = tlvBuilder.buildArray();
        }

        transactionData.setTransData(data);
        if (vasResult != PosEmvErrorCode.APPLE_VAS_UNTREATED) {
          BerTlvBuilder tlvBuilder = new BerTlvBuilder();
          if (vasData != null) {
            BerTlvs tlvs = new BerTlvParser().parse(vasData);
            for (BerTlv tlv : tlvs.getList()) {
              tlvBuilder.addBerTlv(tlv);
            }
          }

          if (vasMerchant != null) {
            BerTlvs tlvs = new BerTlvParser().parse(vasMerchant);
            for (BerTlv tlv : tlvs.getList()) {
              tlvBuilder.addBerTlv(tlv);
            }
          }

          transactionData.setAppleVasResult(vasResult);
          transactionData.setAppleVasData(tlvBuilder.buildArray());
        }

        Bundle outBundle = new Bundle();

        outBundle.putInt(EmvOnlineConstraints.OUT_AUTH_RESP_CODE, GlobalData.getTransOnlineResult());
      }
    });

  }

  @Override
  public void onTransactionResult(final int result, Bundle bundle) throws RemoteException {
    Log.d(TAG, "onTransactionResult: " + result);
    switch (result){
      case PosEmvErrorCode.EMV_CANCEL:
        throwError(result+"", "Emv Cancel");
        return;
      case PosEmvErrorCode.EMV_TIMEOUT:
        throwError(result+"", "Emv scanning timeout");
        return;
      default:
        break;
    }

    AppExecutors.getInstance().mainThread().execute(new Runnable() {
      @Override
      public void run() {
        byte[] data;
        int vasResult;
        byte[] vasData;
        byte[] vasMerchant;
        int encryptResult;
        byte[] encryptData;
        byte[] scriptResult;

        vasResult = bundle.getInt(POIEmvCoreManager.EmvResultConstraints.APPLE_RESULT, PosEmvErrorCode.APPLE_VAS_UNTREATED);
        encryptResult = bundle.getInt(POIEmvCoreManager.EmvResultConstraints.ENCRYPT_RESULT, PosEmvErrorCode.EMV_UNENCRYPTED);

        if (vasResult != -1) {
          Log.d(TAG, "VAS Result : " + vasResult);
        }
        if (encryptResult != -1) {
          Log.d(TAG, "Encrypt Result : " + encryptResult);
        }

        vasData = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.APPLE_DATA);
        if (vasData != null) {
          Log.d(TAG, "VAS Data : " + HexUtil.toHexString(vasData));
        }
        vasMerchant = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.APPLE_MERCHANT);
        if (vasMerchant != null) {
          Log.d(TAG, "VAS Merchant : " + HexUtil.toHexString(vasMerchant));
        }
        data = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.EMV_DATA);
        Log.d(TAG, "run: data: " + data);
        if (data != null) {
          Log.d(TAG, "Trans Data : " + HexUtil.toHexString(data));
          EmvCard emvCard = new EmvCard(data);
          Log.d(TAG, "Trans Data: " +  emvCard);
          promiseCardInfomation.resolve(emvCard.convertWritable());
        }
        encryptData = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.ENCRYPT_DATA);
        if (encryptData != null) {
          Log.d(TAG, "Encrypt Data : " + HexUtil.toHexString(encryptData));
        }
        scriptResult = bundle.getByteArray(POIEmvCoreManager.EmvResultConstraints.SCRIPT_RESULT);
        if (scriptResult != null) {
          Log.d(TAG, "Script Result : " + HexUtil.toHexString(scriptResult));
        }

        if (data != null) {
          BerTlvBuilder tlvBuilder = new BerTlvBuilder();
          BerTlvParser tlvParser = new BerTlvParser();
          BerTlvs tlvs = tlvParser.parse(data);
          for (BerTlv tlv : tlvs.getList()) {
            tlvBuilder.addBerTlv(tlv);
            if (tlv.isConstructed()) {
              Log.d(TAG, String.format("Tag : %1$-4s  >>", tlv.getTag().getBerTagHex()));
              for (BerTlv value : tlv.getValues()) {
                Log.d(TAG, String.format("Tag : %1$-4s", value.getTag().getBerTagHex()) +
                  " Value : " + value.getHexValue());
              }
              Log.d(TAG, String.format("Tag : %1$-4s  <<", tlv.getTag().getBerTagHex()));
            } else {
              Log.d(TAG, String.format("Tag : %1$-4s", tlv.getTag().getBerTagHex()) + " Value : " + tlv.getHexValue());
            }
          }

          if (encryptResult == PosEmvErrorCode.EMV_OK && encryptData != null) {
            BerTlvs encryptTlvs = new BerTlvParser().parse(encryptData);
            for (BerTlv tlv : encryptTlvs.getList()) {
              tlvBuilder.addBerTlv(tlv);
              Log.d(TAG, String.format("Tag : %1$-4s", tlv.getTag().getBerTagHex()) + " Value : " + tlv.getHexValue());
            }
          }

          data = tlvBuilder.buildArray();
        }
        Log.d(TAG, "run: " + result );
        switch (result) {
          case PosEmvErrorCode.EMV_MULTI_CONTACTLESS:
//            isViewUpdate = true;
//            tvMessage1.setText("Multiple cards , Present a single card");
//            onTransStart();
            return;
          case PosEmvErrorCode.EMV_FALLBACK:
//            isViewUpdate = true;
//            isFallBack = true;
//            tvMessage1.setText("Please Magnetic Stripe");
//            tvMessage2.setText("FallBack");
//            onTransStart();
            return;
          case PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE:
//            tvMessage1.setText("Please Insert Card");
//            onTransStart();
            return;
          case PosEmvErrorCode.EMV_APP_EMPTY:
//            isViewUpdate = true;
//            isFallBack = true;
//            tvMessage1.setText("Please Magnetic Stripe");
//            tvMessage2.setText("AID Empty");
//            onTransStart();
            return;
          case PosEmvErrorCode.EMV_SEE_PHONE:
          case PosEmvErrorCode.APPLE_VAS_WAITING_INTERVENTION:
          case PosEmvErrorCode.APPLE_VAS_WAITING_ACTIVATION:
//            isViewUpdate = true;
//            tvMessage2.setText("Please See Phone");
//            onTransStart();
            return;
          default:
            break;
        }

        if (vasResult != PosEmvErrorCode.APPLE_VAS_UNTREATED) {
          BerTlvBuilder tlvBuilder = new BerTlvBuilder();
          if (vasData != null) {
            BerTlvs tlvs = new BerTlvParser().parse(vasData);
            for (BerTlv tlv : tlvs.getList()) {
              tlvBuilder.addBerTlv(tlv);
            }
          }

          if (vasMerchant != null) {
            BerTlvs tlvs = new BerTlvParser().parse(vasMerchant);
            for (BerTlv tlv : tlvs.getList()) {
              tlvBuilder.addBerTlv(tlv);
            }
          }
          transactionData.setAppleVasResult(vasResult);
          transactionData.setAppleVasData(tlvBuilder.buildArray());
        }

        transactionData.setTransData(data);
        transactionData.setTransResult(result);

        Log.d(TAG, "data: " + data);
        if (data != null) {
          EmvCard emvCard = new EmvCard(data);
          if (emvCard.getCardNumber() != null) {
//            transRepository.createTransaction(transactionData);
          }
        } else if (vasData != null) {
          if (vasResult == PosEmvErrorCode.APPLE_VAS_APPROVED) {
//            transRepository.createTransaction(transactionData);
          }
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
          @Override
          public void run() {
//            TransDetailActivity.startActivity(TransActivity.this, transactionData);
//            finish();
          }
        }, 1000);
      }
    });
  }

  private Bundle processOnlineResult(String data) {
    Bundle bundle = new Bundle();
    BerTlvBuilder tlvBuilder = new BerTlvBuilder();
    String authRespCode = null;
    String authCode = null;
    String script = null;
    BerTlvParser tlvParser = new BerTlvParser();
    List<BerTlv> tlvs = tlvParser.parse(HexUtil.parseHex(data)).getList();
    for (BerTlv tlv : tlvs) {
      switch (tlv.getTag().getBerTagHex()) {
        case "8A":
          authRespCode = tlv.getHexValue();
          break;
        case "91":
          authCode = tlv.getHexValue();
          break;
        case "71":
        case "72":
          tlvBuilder.addBerTlv(tlv);
          break;
        default:
          break;
      }
    }
    if (tlvBuilder.build() != 0) {
      script = HexUtil.toHexString(tlvBuilder.buildArray());
    }

    return bundle;
  }

  private void onTransEnd(String code){
    promiseCardInfomation.reject(code, code);
  }

  private void throwError(String code, String message) {
    if(promiseCardInfomation != null){
      promiseCardInfomation.reject(code, message);
    }
  }
}
