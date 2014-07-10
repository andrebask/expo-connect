package it.polimi.camparollo.expoconnect.uilisteners;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.TextView;
import it.polimi.camparollo.expoconnect.R;
import it.polimi.camparollo.expoconnect.wifi.StartActivity;

import java.util.ArrayList;

public class PeersListUpdater implements PeerListListener {
	
	private StartActivity context;

	public PeersListUpdater(StartActivity context) {
		this.context = context;
	}

	public void onPeersAvailable(WifiP2pDeviceList peers) {
		ArrayList<WifiP2pDevice> devices = new ArrayList<WifiP2pDevice>(peers.getDeviceList());
    
		for (WifiP2pDevice device : devices) {
	    	if (device.deviceAddress.equals(StartActivity.ScreenMAC)){
	    		Log.d(StartActivity.TAG, "Screen detected");
	    		((TextView) context.findViewById(R.id.screenSearchText)).setText(R.string.connectionProgress);
	    	    context.connect();    
	    	    break;
	    	}
		}	
	}
}
