/* Deveice to Device Communication
** @Author: Prathyush SP
** This is the Main Activity. It is also a launcher Activity
** It loads activity_main layout.
*/

package com.example.d2d;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    
        //Declare Buttons
	private Button sendBtn, recBtn;
	//Check for WiFi
	private boolean wifiEnabled;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		//Assign the button
		sendBtn = (Button) findViewById(R.id.button1);
		recBtn =  (Button) findViewById(R.id.button2);
		
                
                //OnClick listener for Send Button
		sendBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				Toast.makeText(getBaseContext(),"Send : Switching WiFi", 
		                Toast.LENGTH_SHORT).show();			
				send();}

		});
		
		//OnClick Listener for Receive Button
		recBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				Toast.makeText(getBaseContext(),"Receive : Switching WiFi HotSpot", 
		                Toast.LENGTH_SHORT).show();			
				receive();}

		});
	}
	
	//Send Function
	void send(){
		//Jump to Send Activity
		Intent i = new Intent(this, Client.class);
        startActivity(i);}
	
	//Recieve Function
	void receive(){
		//Jump to Receive Activity
		Intent i = new Intent(this, Server.class);
        startActivity(i);}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			
		if (id == R.id.action_about) {
			Intent i = new Intent(this, About.class);
	        startActivity(i);
		
				return true;
		}
		return super.onOptionsItemSelected(item);
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
	
	//Exit function
		private Boolean exit = false;
		@Override
		    public void onBackPressed() {
		        if (exit) {
		            finish(); // finish activity
		            final WifiManager wifiManager = (WifiManager) this
		    				.getSystemService(Context.WIFI_SERVICE);
		            wifiManager.setWifiEnabled(true);
		            turnOnOffHotspot(this, false);
		            
		        } else {
		            Toast.makeText(this, "Press Back again to Exit.",
		                    Toast.LENGTH_SHORT).show();
		            exit = true;
		            new Handler().postDelayed(new Runnable() {
		                @Override
		                public void run() {
		                    exit = false;
		                }
		            }, 3 * 1000);
		        }
		    }
		
}
