package it.polimi.camparollo.expoconnectserver.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.Toast;
import it.polimi.camparollo.expoconnectserver.uilisteners.ConnectionInfo;
import it.polimi.camparollo.expoconnectserver.uilisteners.PeersListUpdater;
import it.polimi.camparollo.expoconnectserver.InfoActionListener;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
	
	private ConnectService service;
	private Channel channel;
	private WifiP2pManager manager;
	
	private InfoActionListener infoListener;
	private PeersListUpdater peersUpdater;
	private ConnectionInfo connInfo;

	public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, ConnectService service) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.service = service;
		this.infoListener = new InfoActionListener("Discovery Initiated", "Discovery Failed", service);
		this.peersUpdater = new PeersListUpdater(service);
		this.connInfo = new ConnectionInfo(service);
	}

	public void onReceive(Context context, Intent intent) {
	  
	    String action = intent.getAction();
	    if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
	      int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
	      Log.d(ConnectService.TAG, "P2P state changed - " + state);
	      Toast.makeText(service, "P2P state changed - " + state, Toast.LENGTH_SHORT).show();
	      if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
	    	  service.setWifiP2pEnabled(true);
	    	  manager.discoverPeers(channel, infoListener);
	      } else {
	          service.setWifiP2pEnabled(false);
	          service.setIsDeviceConnected(false);
	          //service.restartWifiP2p();
	      }
	    } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
	
	        if (manager != null && !service.isDeviceConnected())
	            manager.requestPeers(channel, peersUpdater);
	        Log.d(ConnectService.TAG, "P2P peers changed");
	    	
	    } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
	
	        if (manager != null) {
	
		        NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
		
		        if (info.isConnected()) {
		        	manager.requestConnectionInfo(channel, connInfo);
		            Log.d(ConnectService.TAG, "P2P connection changed");
		        } else {
		        	service.setIsDeviceConnected(false);
		        }
	        }
	    } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
	    	Log.d(ConnectService.TAG, "P2P device details changed");
	    }
	}
}
