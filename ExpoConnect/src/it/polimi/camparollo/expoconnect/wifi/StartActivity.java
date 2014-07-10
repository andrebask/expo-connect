package it.polimi.camparollo.expoconnect.wifi;

import java.net.InetAddress;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;
import it.polimi.camparollo.expoconnect.R;
import it.polimi.camparollo.expoconnect.datatransfer.IPAddressSender;
import it.polimi.camparollo.expoconnect.exceptions.CannotEnableWifiP2pException;
import it.polimi.camparollo.expoconnect.recommendations.RestaurantListActivity;

public class StartActivity extends Activity implements ChannelListener {

	public static final String ScreenMAC = "08:60:6e:25:eb:bb";
	public static final String TAG = "ExpoConnect";

	private WifiP2pManager manager;
	private Channel channel;

	private final IntentFilter intentFilter = new IntentFilter();
	private BroadcastReceiver receiver = null;

	private boolean isWifiP2pEnabled = false;
	private boolean retryChannel = false;
	private boolean isDeviceConnected = false;
	private String data;

	public void setWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	public void setRetryChannel(boolean retryChannel) {
		this.retryChannel = retryChannel;
	}

	public boolean isDeviceConnected() {
		return isDeviceConnected;
	}

	public void setIsDeviceConnected(boolean isDeviceConnected) {
		this.isDeviceConnected = isDeviceConnected;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		manager = ((WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE));
		channel = manager.initialize(this, getMainLooper(), null);

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(TAG, 0);
		data = settings.getString("Data", null);

		if (data != null) {

			Button b = (Button) findViewById(R.id.historyButton);

			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(getApplicationContext(), RestaurantListActivity.class);
					i.putExtra("Data", data);
					startActivity(i);

				}
			});

			b.setVisibility(View.VISIBLE);

		}

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
		registerReceiver(receiver, intentFilter);
	}

	public void connect() {
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = ScreenMAC;
		config.wps.setup = WpsInfo.PBC;
		manager.connect(channel, config, new InfoActionListener("Connecting", "Connection Error", this));
	}

	public void sendIPAddress(InetAddress ownerAddr){
		ArrayList<String> params = new ArrayList<String>();
		params.add(ownerAddr.toString());
		params.add("ExpoConnect");
		new IPAddressSender(this).sendData(params);
	}

	@Override
	public void onChannelDisconnected() {
		if (manager != null && !retryChannel) {
			Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
			setRetryChannel(true);
			manager.initialize(this, getMainLooper(), this);
		} else {
			Toast.makeText(this, "Channel lost premanently. Try Disable/Re-Enable P2P.", Toast.LENGTH_LONG).show();
		}
	}

	public void restartWifiP2p() {
		try {
			CustomWifiP2pManager.turnOffP2P(channel, manager);
			CustomWifiP2pManager.turnOnP2P(channel, manager);
		} catch (CannotEnableWifiP2pException e) {
			manualP2pActivation();
		}
	}

	public void manualP2pActivation() {
		startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.start, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.menu_share:
			showMenu(findViewById(R.id.menu_share));;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showMenu(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.actions, popup.getMenu());
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.menu_item_qr:
					try {

						Intent intent = new Intent(
								"com.google.zxing.client.android.SCAN");
						intent.putExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
						startActivityForResult(intent, 0);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "ERROR:" + e, 1).show();
						return false;

					}
					return true;
				case R.id.menu_item_demo:
					String data = "[{'id':1,'sex':'m','age':23},{'id':2,'sex':'f','age':60}]!0";
					Intent i = new Intent(getApplicationContext(), RestaurantListActivity.class);
					i.putExtra("Data", data);
					startActivity(i);
				default:
					return false;
				}
			}

		});
		popup.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {

			if (resultCode == RESULT_OK) {
				//ntent.getStringExtra("SCAN_RESULT_FORMAT");
				String data = intent.getStringExtra("SCAN_RESULT");
				Intent i = new Intent(this, RestaurantListActivity.class);
				i.putExtra("Data", data);
				this.startActivity(i);
			} else if (resultCode == RESULT_CANCELED) {
			}
		}
	}
}
