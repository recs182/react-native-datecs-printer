package com.renancsoares.datecsprinter;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;
import java.util.Set;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.UUID;

import com.datecs.api.BuildInfo;
import com.datecs.api.printer.PrinterInformation;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.ProtocolAdapter;

public class RNDatecsPrinterModule extends ReactContextBaseJavaModule implements LifecycleEventListener{

	// Debugging
	private static final Boolean DEBUG = true;
	private static final String LOG_TAG = "RNDatecsPrinterModule";

	private String address;
	private byte[] readBuffer;
	private int readBufferPosition;
	volatile boolean stopWorker;

	//Promises
	Promise feedPaperPromise;
	Promise printSelfTestPromise;

	//Members
	private Printer mPrinter;
	private ProtocolAdapter mProtocolAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mmSocket;
	private BluetoothDevice mmDevice;
	private OutputStream mmOutputStream;
	private InputStream mmInputStream;
	private Thread workerThread;

	private final ProtocolAdapter.ChannelListener mChannelListener = new ProtocolAdapter.ChannelListener(){
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
				showToast("Papel Ok");
			} else {
				disconnect(null);
				showToast("Sem Papel");
			}
		}

		@Override
		public void onOverHeated(boolean state) {
			if (state) {
				showToast("Superaquecimento");
			}
		}
		// 6ca1a08e05c9439bbb6c2825ae7fdec4
		@Override
		public void onLowBattery(boolean state) {
			if (state) {
				showToast("Pouca Bateria");
			}
		}
	};

	public RNDatecsPrinterModule(ReactApplicationContext reactContext) {
		super(reactContext);

		reactContext.addLifecycleEventListener(this);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public void onHostResume() {
	    // Activity `onResume`
	}

	@Override
	public void onHostPause() {
		disconnect(null);
	}

	@Override
	public void onHostDestroy() {
		disconnect(null);
	}

	@Override
	public String getName() {
		return "DatecsPrinter";
	}

	/**
     * Get paired device and start connection
     *
     * @param promise
     */
	@ReactMethod
	public void connect(Promise promise) throws IOException {
		try {
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			ArrayList list = new ArrayList();
			for(BluetoothDevice device : pairedDevices){
				list.add(device);
			}

			if(list.size() > 0){
				mmDevice = (BluetoothDevice) list.get(0);
			}else{
				showToast("Nenhum dispositivo pareado");
				return;
			}

			// Standard SerialPortService ID
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
			mmSocket.connect();
			mmOutputStream = mmSocket.getOutputStream();
			mmInputStream = mmSocket.getInputStream();

			try{
				initializePrinter(mmInputStream, mmOutputStream, promise);
			}catch(Exception e){
				promise.reject("ERRO: " + e.getMessage());
				return;
			}

			// promise.resolve("BLUETOOTH CONNECTED");
		}catch(Exception e){
			promise.reject("ERRO: " + e.getMessage());
		}
	}

	/**
     * Connect printer
     *
     * @param promise
     */
	protected void initializePrinter(InputStream inputStream, OutputStream outputStream, Promise promise) throws IOException {
		mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);
		if (mProtocolAdapter.isProtocolEnabled()) {
			final ProtocolAdapter.Channel channel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
			
			// it was causing errors, need to be reviewed
            // channel.setListener(mChannelListener);

            // Create new event pulling thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						try {
							channel.pullEvent();
						} catch (IOException e) {
							e.printStackTrace();
							break;
						}
					}
				}
			}).start();
			mPrinter = new Printer(channel.getInputStream(), channel.getOutputStream());
		} else {
			mPrinter = new Printer(mProtocolAdapter.getRawInputStream(), mProtocolAdapter.getRawOutputStream());
		}
		promise.resolve("PRINTER_INITIALIZED");
	}

	/**
     * Get Printer status
     *
     * @param promise
     */
	@ReactMethod
	public void getStatus(Promise promise) {
		try {
			int status = mPrinter.getStatus();
			promise.resolve(status);
		} catch (Exception e) {
			promise.reject("Erro ao buscar status: " + e.getMessage());
		}
	}

	/**
     * Feed paper to the printer (roll out blank paper)
     *
     * @param linesQuantity
     */
	@ReactMethod
	public void feedPaper(int linesQuantity) {
		if (linesQuantity < 0 || linesQuantity > 255) {
			if(feedPaperPromise != null) feedPaperPromise.reject("AMOUNT_LINES_0_255");
			feedPaperPromise = null;
			return;
		}
		try {
			mPrinter.feedPaper(linesQuantity);
			mPrinter.flush();

			if(feedPaperPromise != null) feedPaperPromise.resolve("PAPER_FED");
			feedPaperPromise = null;
		} catch (Exception e) {
			if(feedPaperPromise != null) feedPaperPromise.reject("Erro: " + e.getMessage());
			feedPaperPromise = null;
		}
	}

	/**
     * Print self test
     */
	@ReactMethod
	public void printSelfTest() {
		try {
			mPrinter.printSelfTest();
			mPrinter.flush();

			if(printSelfTestPromise != null) printSelfTestPromise.resolve("SELF_TEST_PRINTED");
			printSelfTestPromise = null;
		} catch (Exception e) {
			if(printSelfTestPromise != null) printSelfTestPromise.reject("Erro: " + e.getMessage());
			printSelfTestPromise = null;
		}
	}

	/**
     * Print custom text
     *
     * @param text
     * @param promise
     */
	@ReactMethod
	public void printText(String text, Promise promise) {
		String charset = "ISO-8859-1";
		try {
			mPrinter.printTaggedText(text, charset);
			mPrinter.flush();

			if(promise != null) promise.resolve("PRINTED");
		} catch (Exception e) {
			if(promise != null) promise.reject("Erro: " + e.getMessage());
		}
	}

	/**
     * Disconnect printer
     *
     * @param promise
     */
	@ReactMethod
	public void disconnect(Promise promise){
		try {
			mmSocket.close();

			if (mPrinter != null) {
				mPrinter.release();
			}

			if (mProtocolAdapter != null) {
				mProtocolAdapter.release();
			}

			if(promise != null) promise.resolve("DISCONNECTED");
		} catch (Exception e) {
			if(promise != null) promise.reject("ERRO: " + e.getMessage());
		}
	}

	private void showToast(final String text) {
		Toast.makeText(getReactApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

}
