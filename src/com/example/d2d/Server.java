/* Deveice to Device Communication
 ** @Author: Prathyush SP
 ** This is the Recieve Activity. 
 ** It loads activity_receive layout.
 ** Receive Logic: Turn off WiFi . Turn on HotSpot
 */

package com.example.d2d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.whitebyte.wifihotspotutils.ClientScanResult;
import com.whitebyte.wifihotspotutils.FinishScanListener;
import com.whitebyte.wifihotspotutils.WifiApManager;
import com.example.d2d.WifiApControl;

public class Server extends ActionBarActivity {

	private WifiConfiguration apconfig;
	private String ssid = "d2dcommunication";
	private String key = "raksytk1234";
	TextView wifiSSID;
	TextView Status;
	TextView wifiAuth;
	WifiApManager wifiApManager;
	TextView clientsDisp;
	private int clientNo = 0;
	private ProgressBar spinner;
	private int scanSec = 10;
	private Button scanBtn;
	WifiApControl apControl;
	
	//Server Controls
	private final int SERVER_PORT = 8080;
	private TextView tvClientMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		
		//Server COntrols
		tvClientMsg = (TextView) findViewById(R.id.textView5);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ServerSocket socServer = new ServerSocket(SERVER_PORT);
					Socket socClient = null;
					while (true) {
						socClient = socServer.accept();
						ServerAsyncTask serverAsyncTask = new ServerAsyncTask();
						serverAsyncTask.execute(new Socket[] { socClient });
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		

		clientsDisp = (TextView) findViewById(R.id.textView4);
		wifiApManager = new WifiApManager(this);

		// Declare WifiManager Class
		final WifiManager wifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		spinner = (ProgressBar) findViewById(R.id.progressBar1);

		scanBtn = (Button) findViewById(R.id.button1);
		scanBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new LongOperation().execute("");
			}
		});

		wifiSSID = (TextView) findViewById(R.id.TextView02);
		wifiAuth = (TextView) findViewById(R.id.TextView03);
		Status = (TextView) findViewById(R.id.textView1);
		wifiSSID.setText("SSID: " + ssid);

		// Check for WiFi, if On turn off
		if (wifiManager.isWifiEnabled())
			wifiManager.setWifiEnabled(false);

		WifiConfiguration netConfig = new WifiConfiguration();

		netConfig.SSID = ssid;
		netConfig.preSharedKey=key;
		netConfig.allowedAuthAlgorithms
				.set(WifiConfiguration.AuthAlgorithm.OPEN);
		netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wifiAuth.setText("Authentication Type: WPA_PSK");

		try {
			Method setWifiApMethod = wifiManager.getClass().getMethod(
					"setWifiApEnabled", WifiConfiguration.class, boolean.class);
			boolean apstatus = (Boolean) setWifiApMethod.invoke(wifiManager,
					netConfig, true);

			Method isWifiApEnabledmethod = wifiManager.getClass().getMethod(
					"isWifiApEnabled");
			while (!(Boolean) isWifiApEnabledmethod.invoke(wifiManager)) {
			}
			;
			Method getWifiApStateMethod = wifiManager.getClass().getMethod(
					"getWifiApState");
			int apstate = (Integer) getWifiApStateMethod.invoke(wifiManager);
			Method getWifiApConfigurationMethod = wifiManager.getClass()
					.getMethod("getWifiApConfiguration");
			netConfig = (WifiConfiguration) getWifiApConfigurationMethod
					.invoke(wifiManager);
			Log.e("CLIENT", "\nSSID:" + netConfig.SSID + "\nPassword:"
					+ netConfig.preSharedKey + "\n");

		} catch (Exception e) {
			Log.e(this.getClass().toString(), "", e);
		}
		scan();

		
		//server operation
		
		
		spinner.setVisibility(View.VISIBLE);
		Status.setText("Forming Cluster . .");
		new LongOperation().execute("");
		

	}

	private class LongOperation extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			for (int i = 0; i < scanSec; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.interrupted();
				}
				scan();
			}
			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			scanBtn.setVisibility(View.VISIBLE);
			spinner.setVisibility(View.GONE);
		}

		@Override
		protected void onPreExecute() {
			scanBtn.setVisibility(View.GONE);
			spinner.setVisibility(View.VISIBLE);
			clientsDisp.setText("");
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

	}

	public void setVisible() {
		Status.setText("Clients in Cluster:");
	}

	private void scan() {
		// clientsDisp.setText(" ");
		wifiApManager.getClientList(true, new FinishScanListener() {
			@Override
			public void onFinishScan(final ArrayList<ClientScanResult> clients) {
				clientsDisp.setText("");
				clientNo = 0;
				int temp=0;
				for (ClientScanResult clientScanResult : clients) {					
					temp=clientNo;
					clientsDisp.append((++clientNo) + ": ");
					clientsDisp.append("IpAddr: "
							+ clientScanResult.getIpAddr() + "\n");
					clientsDisp.append("HWAddr: "
							+ clientScanResult.getHWAddr() + "\n");			
				
				}
			}
		});

	}

	void wifiapconfig() {
		apconfig = new WifiConfiguration();
		apconfig.SSID = String.format("\"%s\"", ssid);
		apconfig.preSharedKey = String.format("\"%s\"", key);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_about) {
			Intent i = new Intent(this, About.class);
	        startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Turn off/on HotSpot function
	public static void turnOnOffHotspot(Context context, boolean isTurnToOn) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiApControl apControl = WifiApControl.getApControl(wifiManager);
		if (apControl != null) {

			// TURN OFF YOUR WIFI BEFORE ENABLE HOTSPOT
			// if (isWifiOn(context) && isTurnToOn) {
			// turnOnOffWifi(context, false);
			// }

			apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
					isTurnToOn);
		}
	}
	
	/**
	 * AsyncTask which handles the commiunication with clients
	 */
	class ServerAsyncTask extends AsyncTask<Socket, Void, String> {		
		@Override
		protected String doInBackground(Socket... params) {
			String result = null;
			Socket mySocket = params[0];			
			try {

				InputStream is = mySocket.getInputStream();
				PrintWriter out = new PrintWriter(mySocket.getOutputStream(),
						true);

				out.println("Hello from server");

				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));

				result = br.readLine();

				mySocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String s) {

			tvClientMsg.setText(s);
		}
	}
}
