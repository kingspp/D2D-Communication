/* Deveice to Device Communication
** @Author: Prathyush SP
** This is the Recieve Activity. 
** It loads activity_receive layout.
** Receive Logic: Turn off WiFi . Turn on HotSpot
*/


package com.example.d2d;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.whitebyte.wifihotspotutils.ClientScanResult;
import com.whitebyte.wifihotspotutils.FinishScanListener;
import com.whitebyte.wifihotspotutils.WifiApManager;
import com.example.d2d.WifiApControl;

public class Server extends ActionBarActivity {

	private WifiConfiguration apconfig;
	private String ssid= "d2dcommunication";
	private String key= "raksytk1234";
	WifiApManager wifiApManager; 
	TextView clientsDisp;
	private int clientNo=0;
	
	
	WifiApControl apControl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		
		clientsDisp = (TextView) findViewById(R.id.textView3);
		wifiApManager = new WifiApManager(this);
		
		//Declare WifiManager Class
		final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		
		//Check for WiFi, if On turn off
		if(wifiManager.isWifiEnabled())
			wifiManager.setWifiEnabled(false);
		
		WifiConfiguration netConfig = new WifiConfiguration();

		netConfig.SSID = ssid;
		//netConfig.preSharedKey=key;
		netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

		try{
		    Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
		    boolean apstatus=(Boolean) setWifiApMethod.invoke(wifiManager, netConfig,true);

		    Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled"); 
		    while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){};
		    Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState"); 
		    int apstate=(Integer)getWifiApStateMethod.invoke(wifiManager);
		    Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
		    netConfig=(WifiConfiguration)getWifiApConfigurationMethod.invoke(wifiManager);
		    Log.e("CLIENT", "\nSSID:"+netConfig.SSID+"\nPassword:"+netConfig.preSharedKey+"\n");

		} catch (Exception e) {
		    Log.e(this.getClass().toString(), "", e);
		}
		scan();
		new Thread(new Task()).start();
		
		
		
	}
	
	class Task implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i <= 300; i++) {				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				scan();

			}
		}

	}
	
	private void scan() {
		wifiApManager.getClientList(false, new FinishScanListener() {
			@Override
			public void onFinishScan(final ArrayList<ClientScanResult> clients) {		
				clientsDisp.setText(" ");
				clientNo=0;
				for (ClientScanResult clientScanResult : clients) {					
					clientsDisp.append((++clientNo)+": ");
					clientsDisp.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");					
					clientsDisp.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");					
				}
			}
		});
	}
	
	void wifiapconfig(){
		apconfig=new WifiConfiguration();
		apconfig.SSID=String.format("\"%s\"", ssid);
		apconfig.preSharedKey=String.format("\"%s\"", key);
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
