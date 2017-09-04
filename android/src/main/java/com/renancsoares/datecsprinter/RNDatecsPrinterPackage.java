package com.renancsoares.datecsprinter;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;

public class RNDatecsPrinterPackage implements ReactPackage {
	static final String TAG = "DatecsPrinter";
	
	@Override
	public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
		List<NativeModule> modules = new ArrayList<>();
		modules.add(new RNDatecsPrinterModule(reactContext));
		return modules;
	}

	@Override
	public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
		return Collections.emptyList();
	}
}
