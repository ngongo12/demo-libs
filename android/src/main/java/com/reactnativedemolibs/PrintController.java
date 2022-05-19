package com.reactnativedemolibs;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.pos.sdk.printer.POIPrinterManager;
import com.pos.sdk.printer.models.PrintLine;
import com.pos.sdk.printer.models.TextPrintLine;

public class PrintController {
  private final String TAG = "PrintController";
  private POIPrinterManager printerManager;
  private PrintListener printListener;
  private Context context;
  private int printState;

  public PrintController(Context context) {
    this.context = context;
    this.printerManager = new POIPrinterManager(context);
    this.printListener = new PrintListener();
    this.printState = printerManager.getPrinterState();
  }

  public void close(){
    Log.d(TAG, "close: ");
    printerManager.close();
  }

  public void open(){
    Log.d(TAG, "open: ");
    printerManager.open();
  }

  public void beginPrint(){
    Log.d(TAG, "beginPrint: ");
    printerManager.beginPrint(printListener);
  }

  public void setPrintGray(int i){
    printerManager.setPrintGray(i);
  }

  public void setPrintFont(String font){
    printerManager.setPrintFont(font);
  }

  public void setLineSpace(int i){
    printerManager.setLineSpace(i);
  }

  public void cleanCache(){
    printerManager.cleanCache();
  }

  public void addPrintLine(PrintLine printLine){
    if(printLine != null) {
      Log.d(TAG, "addPrintLine: " + printLine.toString());
    }else{
      Log.d(TAG, "addPrintLine: Không có printLine");
    }
    printerManager.addPrintLine(printLine);
  }

  public void lineWrap(int i){
    printerManager.lineWrap(i);
  }

  public void printTextLine(String content, int size, int position, boolean isBold, boolean isItalic, boolean isInvert){
    TextPrintLine textPrintLine = new TextPrintLine(content, position, size, isBold, isItalic, isInvert);
    addPrintLine(textPrintLine);
  }

}
