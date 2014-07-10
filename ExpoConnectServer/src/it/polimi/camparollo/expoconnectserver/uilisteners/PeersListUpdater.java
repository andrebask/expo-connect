package it.polimi.camparollo.expoconnectserver.uilisteners;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import it.polimi.camparollo.expoconnectserver.wifi.ConnectService;

import java.util.ArrayList;

public class PeersListUpdater implements PeerListListener {
	
	private ConnectService context;
	private ArrayList<String> connectedDevices = new ArrayList<String>();

	public PeersListUpdater(ConnectService context) {
		this.context = context;
	}

	public void onPeersAvailable(WifiP2pDeviceList peers) {
		ArrayList<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>(peers.getDeviceList());
    
		for (WifiP2pDevice device : devices) {
    		if (!connectedDevices.contains(device.deviceAddress)){
    			Log.d(ConnectService.TAG, "Connecting to " + device.toString());
    			context.connect(device.deviceAddress);
    			connectedDevices.add(device.deviceAddress);
    		}
		}	
	}
}
