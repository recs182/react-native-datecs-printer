package com.renancsoares.datecsprinter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

import com.datecs.api.printer.Printer;
import com.datecs.api.printer.Printer.ConnectionListener;
import com.datecs.api.printer.PrinterInformation;
import com.datecs.api.printer.ProtocolAdapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import com.renancsoares.datecsprinter.network.PrinterServer;
import com.renancsoares.datecsprinter.network.PrinterServerListener;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;

public class RNDatecsPrinterModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener{

	// Debugging
	private static final Boolean DEBUG = true;
	private static final String LOG_TAG = "RNDatecsPrinterModule";

	// Request to get the bluetooth device
	private static final int REQUEST_GET_DEVICE = 0;

	// Request to get the bluetooth device
	private static final int DEFAULT_NETWORK_PORT = 9100;

	//
	private String deviceAddress;
	private Set<BluetoothDevice> pairedDevices;

	// Members
	private ReactApplicationContext mReactContext;
	private Printer mPrinter;
	private ProtocolAdapter mProtocolAdapter;
	private ProtocolAdapter.Channel mPrinterChannel;
	private PrinterServer mPrinterServer;
	private BluetoothAdapter mBluetoothAdapter;
	private Socket mNetSocket;
	private BluetoothSocket mBtSocket;


	// Interface, used to invoke asynchronous printer operation.
	private interface PrinterRunnable {
		public void run(Printer printer) throws IOException;
	}

	public RNDatecsPrinterModule(ReactApplicationContext reactContext) {
		super(reactContext);
		mReactContext = reactContext;

		if(mBluetoothAdapter == null){
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}

		mReactContext.addActivityEventListener(this);
		mReactContext.addLifecycleEventListener(this);

		this.waitForConnection();
	}

	@Override
	public String getName() {
		return "DatecsPrinter";
	}

	@Override
	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
		// getting first paired device
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		ArrayList list = new ArrayList();
		for(BluetoothDevice device : pairedDevices){
			list.add(device);
		}
		deviceAddress = list.get(0).toString();

		if (BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
			establishBluetoothConnection(deviceAddress);
		} else {
			establishNetworkConnection(deviceAddress);
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		if(DEBUG) Log.d(LOG_TAG, "On new intent");
	}

	@Override
	public void onHostResume() {
		if (DEBUG) Log.d(LOG_TAG, "Host resume");
	}

	@Override
	public void onHostPause() {
		if (DEBUG) Log.d(LOG_TAG, "Host pause");
	}

	@Override
	public void onHostDestroy() {
		if (DEBUG) Log.d(LOG_TAG, "Host destroy");
	}

	@Override
	public void onCatalystInstanceDestroy() {
		if (DEBUG) Log.d(LOG_TAG, "Catalyst instance destroyed");
		super.onCatalystInstanceDestroy();
	}

	private void runTask(final PrinterRunnable r) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					r.run(mPrinter);
				} catch (IOException e) {
					e.printStackTrace();
					// error("I/O error occurs: " + e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					// error("Critical error occurs: " + e.getMessage());
					// finish();
				}
			}
		});
		t.start();
	}

	protected void initPrinter(InputStream inputStream, OutputStream outputStream) throws IOException {
		Log.d(LOG_TAG, "Initialize printer...");

        // Here you can enable various debug information
        //ProtocolAdapter.setDebug(true);
		Printer.setDebug(true);

        // Check if printer is into protocol mode. Ones the object is created it can not be released
        // without closing base streams.
		mProtocolAdapter = new ProtocolAdapter(inputStream, outputStream);
		if (mProtocolAdapter.isProtocolEnabled()) {
            // Get printer instance
			mPrinterChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
			mPrinter = new Printer(mPrinterChannel.getInputStream(), mPrinterChannel.getOutputStream());            
		} else {
			Log.d(LOG_TAG, "Protocol mode is disabled");
            // Protocol mode it not enables, so we should use the row streams.
			mPrinter = new Printer(mProtocolAdapter.getRawInputStream(), mProtocolAdapter.getRawOutputStream());
		}
	}

	private synchronized void waitForConnection() {
		// closeActiveConnection();

        // Start server to listen for network connection.
		try {
			mPrinterServer = new PrinterServer(new PrinterServerListener() {
				@Override
				public void onConnect(Socket socket) {
					Log.d(LOG_TAG, "Accept connection from " + socket.getRemoteSocketAddress().toString());

					mNetSocket = socket;
					try {
						InputStream in = socket.getInputStream();
						OutputStream out = socket.getOutputStream();
						initPrinter(in, out);
					} catch (IOException e) {
						e.printStackTrace();
						// error("FAILED to initialize: " + e.getMessage());
						waitForConnection();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void establishBluetoothConnection(final String address) {
		final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d(LOG_TAG, "Connecting to " + address + "...");

				btAdapter.cancelDiscovery();

				try {
					UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
					BluetoothDevice btDevice = btAdapter.getRemoteDevice(address);

					InputStream in = null;
					OutputStream out = null;

					try {
						BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
						btSocket.connect();

						mBtSocket = btSocket;
						in = mBtSocket.getInputStream();
						out = mBtSocket.getOutputStream();
					} catch (IOException e) {
						// error("FAILED to connect: " + e.getMessage());
						waitForConnection();
						return;
					}

					try {
						initPrinter(in, out);
					} catch (IOException e) {
						// error("FAILED to initiallize: " + e.getMessage());
						return;
					}
				} finally {
				}
			}
		});
		t.start();
	}

	private void establishNetworkConnection(final String address) {
		// closePrinterServer();

		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d(LOG_TAG, "Connectiong to " + address + "...");
				try {
					Socket s = null;
					try {
						String[] url = address.split(":");
						int port = DEFAULT_NETWORK_PORT;

						try {
							if (url.length > 1) {
								port = Integer.parseInt(url[1]);
							}
						} catch (NumberFormatException e) {
						}

						s = new Socket(url[0], port);
						s.setKeepAlive(true);
						s.setTcpNoDelay(true);
					} catch (UnknownHostException e) {
						// error("FAILED to connect: " + e.getMessage());
						waitForConnection();
						return;
					} catch (IOException e) {
						// error("FAILED to connect: " + e.getMessage());
						waitForConnection();
						return;
					}

					InputStream in = null;
					OutputStream out = null;

					try {
						mNetSocket = s;
						in = mNetSocket.getInputStream();
						out = mNetSocket.getOutputStream();
					} catch (IOException e) {
						// error("FAILED to connect: " + e.getMessage());
						waitForConnection();
						return;
					}

					try {
						initPrinter(in, out);
					} catch (IOException e) {
						// error("FAILED to initiallize: " + e.getMessage());
						return;
					}
				} finally {
				}
			}
		});
		t.start();
	}

	//Print Self Test
	@ReactMethod
	public void printSelfTest() {
		if(DEBUG) Log.d(LOG_TAG, "Print Self Test");

		runTask(new PrinterRunnable() {
			@Override
			public void run(Printer printer) throws IOException {
				printer.printSelfTest();
				printer.flush();
			}
		});
	}
}