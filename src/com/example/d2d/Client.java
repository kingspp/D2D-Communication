/* Deveice to Device Communication
 ** @Author: Prathyush SP
 ** This is the Send Activity. 
 ** It loads activity_send layout.
 ** Receive Logic: Turn on WiFi . Turn off HotSpot
 */

package com.example.d2d;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Client extends ActionBarActivity {

	private String ssid= "d2dcommunication";
	private String key= "raksytk1234";

	private TextView ssidName;
	private ProgressBar spinner;

	// Declare Variables
	private boolean wifiAP = false, wifi = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);

		ssidName = (TextView) findViewById(R.id.textView3);
		spinner = (ProgressBar)findViewById(R.id.progressBar1);

		// Declare variable for WifiManager Class
		final WifiManager wifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		// Turn Off HotSpot
		turnOnOffHotspot(this, false);

		wifiAccess(this);
		// Turn on WiFi if Off.
		if (!wifiManager.isWifiEnabled())
			wifiManager.setWifiEnabled(true);
		wifiAccess(this);

		//wifiAccess(this);
		DisplayWifiState();
		this.registerReceiver(this.myWifiReceiver,
		         new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
	

	}

	private BroadcastReceiver myWifiReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			NetworkInfo networkInfo = (NetworkInfo) arg1
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				DisplayWifiState();
			}
		}
	};

	private void DisplayWifiState() {

		ConnectivityManager myConnManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo myNetworkInfo = myConnManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		WifiManager myWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();

		if (myNetworkInfo.isConnected()){
			spinner.setVisibility(View.GONE);
			ssidName.setText("SSID: "+myWifiInfo.getSSID().toString());
			
		}
		else{
			ssidName.setText("Scanning for Server");
			spinner.setVisibility(View.VISIBLE);			
		}

	}

	// Turn on/off HotSpot Function
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void wifiAccess(Context context) {

		WifiConfiguration wifiConfig = new WifiConfiguration();
		wifiConfig.SSID = String.format("\"%s\"", ssid);
		wifiConfig.priority=3; 
		//wifiConfig.preSharedKey = String.format("\"%s\"", key);
		//wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// remember id
		int netId = wifiManager.addNetwork(wifiConfig);
		wifiManager.disconnect();
		wifiManager.enableNetwork(netId, true);
		wifiManager.reconnect();
	}
}
