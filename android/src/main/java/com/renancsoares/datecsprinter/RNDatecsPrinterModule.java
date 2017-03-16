package com.renancsoares.datecsprinter;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import com.datecs.api.BuildInfo;
import com.datecs.api.printer.PrinterInformation;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.ProtocolAdapter;

import android.app.Application;
import android.app.Activity;
import java.util.Map;
import java.util.HashMap;

public class RNDatecsPrinterModule extends ReactContextBaseJavaModule{

	// Debugging
	private static final Boolean DEBUG = true;
	private static final String LOG_TAG = "RNDatecsPrinterModule";

	private ReactApplicationContext mReactContext;
	private Application app;

	/**
     * Interface de eventos da Impressora
     */
    private final ProtocolAdapter.ChannelListener mChannelListener = new ProtocolAdapter.ChannelListener() {
    	@Override
    	public void onReadEncryptedCard() {
            // TODO: onReadEncryptedCard
    	}

    	@Override
    	public void onReadCard() {
            // TODO: onReadCard
    	}

    	@Override
    	public void onReadBarcode() {
            // TODO: onReadBarcode
    	}

    	@Override
    	public void onPaperReady(boolean state) {
    		if (state) {
    			showToast(DatecsUtil.getStringFromStringResource(app, "paper_ok"));
    		} else {
    			closeActiveConnections();
    			showToast(DatecsUtil.getStringFromStringResource(app, "no_paper"));
    		}
    	}

    	@Override
    	public void onOverHeated(boolean state) {
    		if (state) {
    			showToast(DatecsUtil.getStringFromStringResource(app, "overheating"));
    		}
    	}

    	@Override
    	public void onLowBattery(boolean state) {
    		if (state) {
    			showToast(DatecsUtil.getStringFromStringResource(app, "low_battery"));
    		}
    	}
    };

	private Map<Integer, String> errorCode = new HashMap<Integer, String>();

	public RNDatecsPrinterModule(ReactApplicationContext reactContext) {
		super(reactContext);
		mReactContext = reactContext;
		app = getCurrentActivity().getApplication(); // need to test if really gets application

		this.errorCode.put(1, DatecsUtil.getStringFromStringResource(app, "err_no_bt_adapter"));
		this.errorCode.put(2, DatecsUtil.getStringFromStringResource(app, "err_no_bt_device"));
		this.errorCode.put(3, DatecsUtil.getStringFromStringResource(app, "err_lines_number"));
		this.errorCode.put(4, DatecsUtil.getStringFromStringResource(app, "err_feed_paper"));
		this.errorCode.put(5, DatecsUtil.getStringFromStringResource(app, "err_print"));
		this.errorCode.put(6, DatecsUtil.getStringFromStringResource(app, "err_fetch_st"));
		this.errorCode.put(7, DatecsUtil.getStringFromStringResource(app, "err_fetch_tmp"));
		this.errorCode.put(8, DatecsUtil.getStringFromStringResource(app, "err_print_barcode"));
		this.errorCode.put(9, DatecsUtil.getStringFromStringResource(app, "err_print_test"));
		this.errorCode.put(10, DatecsUtil.getStringFromStringResource(app, "err_set_barcode"));
		this.errorCode.put(11, DatecsUtil.getStringFromStringResource(app, "err_print_img"));
		this.errorCode.put(12, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
		this.errorCode.put(13, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
		this.errorCode.put(14, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
		this.errorCode.put(15, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
		this.errorCode.put(16, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
		this.errorCode.put(17, DatecsUtil.getStringFromStringResource(app, "err_print_rect"));
		this.errorCode.put(18, DatecsUtil.getStringFromStringResource(app, "failed_to_connect"));
		this.errorCode.put(19, DatecsUtil.getStringFromStringResource(app, "err_bt_socket"));
		this.errorCode.put(20, DatecsUtil.getStringFromStringResource(app, "failed_to_initialize"));

		// mReactContext.addActivityEventListener(this);
		// mReactContext.addLifecycleEventListener(this);
	}

	@Override
	public String getName() {
		return "DatecsPrinter";
	}

}
