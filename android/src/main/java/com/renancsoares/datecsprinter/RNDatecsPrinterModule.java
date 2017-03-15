package com.renancsoares.datecsprinter;

import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNDatecsPrinterModule extends ReactContextBaseJavaModule {
	private final ReactApplicationContext reactContext;

	public RNDatecsPrinterModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
	}

	@Override
	public String getName() {
		return "RNDatecsPrinterModule";
	}

	@ReactMethod
	public void show(){
		Toast.makeText("");
	}
}