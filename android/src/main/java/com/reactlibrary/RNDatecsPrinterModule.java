
package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import com.datecs.api.printer.Printer;
import com.datecs.api.printer.ProtocolAdapter;

public class RNDatecsPrinterModule extends ReactContextBaseJavaModule {

	private Printer mPrinter;
	private ProtocolAdapter mProtocolAdapter;

	private final ReactApplicationContext reactContext;
	
	private static final String REACT_CLASS = "RNDatecsPrinterModule";

	public RNDatecsPrinterModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
	}

	@Override
	public String getName() {
		return "RNDatecsPrinter";
	}

	@ReactMethod
	/**
	* Print text expecting markup formatting tags (default encoding is ISO-8859-1)
	*
	* @param text
	*/
	public void printTaggedText(String text) {
		printTaggedText(text, "ISO-8859-1");
	}

	/**
	* Print text expecting markup formatting tags and a defined charset
	*
	* @param text
	* @param charset
	*/
	public void printTaggedText(String text, String charset) {
		try {
			mPrinter.printTaggedText(text, charset);
			mPrinter.flush();
			//TODO: callback success
		} catch (Exception e) {
			//TODO: callback error instead Log
			Log.d(REACT_CLASS, "error -> " + e);
		}
	}

	@ReactMethod
	/**
     * Print a selftest page
     */
	public void printSelfTest() {
		try {
			mPrinter.printSelfTest();
			mPrinter.flush();
			//TODO: callback success
		} catch (Exception e) {
			//TODO: callback error instead Log
			Log.d(REACT_CLASS, "error -> " + e);
		}
	}

	// /**
 //     * Inicializa a troca de dados com a impressora
 //     * @param inputStream
 //     * @param outputStream
 //     * @param callbackContext
 //     * @throws IOException
 //     */
	// protected void initializePrinter(InputStream inputStream, OutputStream outputStream, CallbackContext callbackContext) throws IOException {
	// 	mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);
	// 	if (mProtocolAdapter.isProtocolEnabled()) {
	// 		final ProtocolAdapter.Channel channel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
	// 		channel.setListener(mChannelListener);
 //            // Create new event pulling thread
	// 		new Thread(new Runnable() {
	// 			@Override
	// 			public void run() {
	// 				while (true) {
	// 					try {
	// 						Thread.sleep(50);
	// 					} catch (InterruptedException e) {
	// 						e.printStackTrace();
	// 					}

	// 					try {
	// 						channel.pullEvent();
	// 					} catch (IOException e) {
	// 						e.printStackTrace();
	// 						// sendStatusUpdate(false);
	// 						// showError(e.getMessage(), mRestart);
	// 						break;
	// 					}
	// 				}
	// 			}
	// 		}).start();
	// 		mPrinter = new Printer(channel.getInputStream(), channel.getOutputStream());
	// 	} else {
	// 		mPrinter = new Printer(mProtocolAdapter.getRawInputStream(), mProtocolAdapter.getRawOutputStream());
	// 	}
	// 	// callbackContext.success();
	// }

}