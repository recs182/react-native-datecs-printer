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

	protected void initializePrinter(InputStream inputStream, OutputStream outputStream, final Promise promise) throws IOException {
        mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);
        if (mProtocolAdapter.isProtocolEnabled()) {
            final ProtocolAdapter.Channel channel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
            channel.setListener(mChannelListener);
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
                            promise.reject("ERRO: " + e.getMessage());
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
     * Alimenta papel à impressora (rola papel em branco)
     *
     * @param linesQuantity
     */
	@ReactMethod
    public void feedPaper(int linesQuantity) {
        if (linesQuantity < 0 || linesQuantity > 255) {
        }
        try {
            mPrinter.feedPaper(linesQuantity);
            mPrinter.flush();
        } catch (Exception e) {
			e.printStackTrace();
        }
    }

	@ReactMethod
	public void printSelfTest() {
        try {
            mPrinter.printSelfTest();
            mPrinter.flush();
        } catch (Exception e) {
        }
    }

	@ReactMethod
	public void printText(String text) {
        printTaggedText(text, "ISO-8859-1");
    }

	@ReactMethod
	public void disconnect(Promise promise){
		try {
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

	private void printTaggedText(String text, String charset) {
        try {
            mPrinter.printTaggedText(text, charset);
            mPrinter.flush();
			feedPaper(4);
        } catch (Exception e) {
			e.printStackTrace();
        }
    }

	private void showToast(final String text) {
        Toast.makeText(getReactApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

}
