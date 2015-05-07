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
	private String ssid= "d2dcommunication";
	private String key= "raksytk1234";
	TextView wifiSSID;
	TextView Status;
	TextView wifiAuth;
	WifiApManager wifiApManager; 
	TextView clientsDisp;
	private int clientNo=0;
	private ProgressBar spinner;
	private int scanSec = 5;
	private Button scanBtn;
	
	
	WifiApControl apControl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		
		clientsDisp = (TextView) findViewById(R.id.textView4);
		wifiApManager = new WifiApManager(this);
		
		//Declare WifiManager Class
		final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		spinner = (ProgressBar)findViewById(R.id.progressBar1);
		
		scanBtn = (Button) findViewById(R.id.button1);
		scanBtn.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {	    
		    	new LongOperation().execute("");	    	
		    }
		});
		
		
		
		wifiSSID= (TextView)findViewById(R.id.TextView02);
		wifiAuth= (TextView)findViewById(R.id.TextView03);
		Status= (TextView)findViewById(R.id.textView1);
		wifiSSID.setText("SSID: "+ssid);
		
		
		
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
		wifiAuth.setText("Authentication Type: None");
		
		

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
		
		//new Thread(new Task()).start();
		spinner.setVisibility(View.VISIBLE);
		Status.setText("Forming Cluster . .");
		new LongOperation().execute("");
		
	}
	
	/*
	private void CreateThread() {
		
		  final Thread t = new Thread() {
		    public void run() {
		      for(int i = 0; i < scanSec; i++)
		      {
		        try {
		          Thread.sleep(1000);
		        } catch (Exception e) {
		          Log.v("Error: ", e.toString());
		        }
		        scan();
		      }
		      
		      
		      runOnUiThread(new Runnable() {

		            @Override
		            public void run() {
		            	
		            	findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE);
		            	findViewById(R.id.button1).setVisibility(View.VISIBLE);
		            	setVisible();
		            	
		            }
		        });
		      
		      
		      scanBtn.getHandler().post(new Runnable() {
		    	    public void run() {
		    	        scanBtn.setVisibility(View.VISIBLE);
		    	    }
		    	});
		    
		    }
		  };
		  t.start();
		  //findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE);
	      //findViewById(R.id.button1).setVisibility(View.VISIBLE);
		}
	*/
	
	private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 5; i++) {
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
        protected void onProgressUpdate(Void... values) {}
    
	}
	
	
	

	
	
	public void setVisible(){
		Status.setText("Clients in Cluster:");
	}
	
	private void scan() {
		//clientsDisp.setText(" ");		
		wifiApManager.getClientList(true, new FinishScanListener() {
			@Override
			public void onFinishScan(final ArrayList<ClientScanResult> clients) {	
				clientsDisp.setText("");
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
