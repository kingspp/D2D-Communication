/* Deveice to Device Communication
** @Author: Prathyush SP
** This is the Recieve Activity. 
** It loads activity_receive layout.
** Receive Logic: Turn off WiFi . Turn on HotSpot
*/


package com.example.d2d;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Receive extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive);
		
		//Declare WifiManager Class
		final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		
		//Check for WiFi, if On turn off
		if(wifiManager.isWifiEnabled())
			wifiManager.setWifiEnabled(false);
		//Turn On HotSpot
		turnOnOffHotspot(this, true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.receive, menu);
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
	
	//Turn off/on HotSpot function
	public static void turnOnOffHotspot(Context context, boolean isTurnToOn) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiApControl apControl = WifiApControl.getApControl(wifiManager);
        if (apControl != null) {
 
                 // TURN OFF YOUR WIFI BEFORE ENABLE HOTSPOT
            //if (isWifiOn(context) && isTurnToOn) {
            //  turnOnOffWifi(context, false);
            //}
 
            apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
                    isTurnToOn);
        }
    }
}
