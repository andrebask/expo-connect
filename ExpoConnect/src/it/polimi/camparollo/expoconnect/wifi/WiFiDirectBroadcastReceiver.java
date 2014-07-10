package it.polimi.camparollo.expoconnect.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.Toast;
import it.polimi.camparollo.expoconnect.uilisteners.ConnectionInfo;
import it.polimi.camparollo.expoconnect.uilisteners.PeersListUpdater;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
	
	private StartActivity activity;
	private Channel channel;
	private WifiP2pManager manager;
	
	private InfoActionListener infoListener;
	private PeersListUpdater peersUpdater;
	private ConnectionInfo connInfo;

	public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, StartActivity activity) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.activity = activity;
		this.infoListener = new InfoActionListener("Discovery Initiated", "Discovery Failed", activity);
		this.peersUpdater = new PeersListUpdater(activity);
		this.connInfo = new ConnectionInfo(activity);
	}

	public void onReceive(Context context, Intent intent) {
	  
	    String action = intent.getAction();
	    if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
	      int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
	      Log.d("ExpoConnect", "P2P state changed - " + state);
	      Toast.makeText(activity, "P2P state changed - " + state, Toast.LENGTH_SHORT).show();
	      if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
	    	  activity.setWifiP2pEnabled(true);
	    	  manager.discoverPeers(channel, infoListener);
	      } else {
	          activity.setWifiP2pEnabled(false);
	          activity.setIsDeviceConnected(false);
	          activity.restartWifiP2p();
	      }
	    } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
	
	        if (manager != null && !activity.isDeviceConnected())
	            manager.requestPeers(channel, peersUpdater);
	        Log.d("ExpoConnect", "P2P peers changed");
	    	
	    } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
	
	        if (manager != null) {
	
		        NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
		
		        if (info.isConnected()) {
		        	manager.requestConnectionInfo(channel, connInfo);
		            Log.d("ExpoConnect", "P2P connection changed");
		        } else {
		        	activity.setIsDeviceConnected(false);
		        }
	        }
	    } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
	    	Log.d("ExpoConnect", "P2P device details changed");
	    }
	}
}
