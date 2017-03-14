
package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import com.datecs.api.printer.Printer;

public class RNDatecsPrinterModule extends ReactContextBaseJavaModule {

  private Printer printer;
  private final ReactApplicationContext reactContext;

  public RNDatecsPrinterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNDatecsPrinter";
  }

  @ReactMethod
  public void printTaggedText(){
  	printer.printTaggedText('{reset}{center}texto');
  }

}