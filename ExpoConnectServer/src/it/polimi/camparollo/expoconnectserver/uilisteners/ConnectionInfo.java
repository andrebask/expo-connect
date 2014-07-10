package it.polimi.camparollo.expoconnectserver.uilisteners;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;
import it.polimi.camparollo.expoconnectserver.wifi.ConnectService;
import it.polimi.camparollo.expoconnectserver.wifi.IPAddressReceiver;
import it.polimi.camparollo.expoconnectserver.wifi.ScreenDataSender;

public class ConnectionInfo implements ConnectionInfoListener {
	
	private ConnectService context;

	public ConnectionInfo(ConnectService context) {
		this.context = context;
	}

	public void onConnectionInfoAvailable(WifiP2pInfo info) {
	  
		Log.d(ConnectService.TAG, "Connected");
		Log.d(ConnectService.TAG, info.toString());
		if (info.groupFormed){
			context.setIsDeviceConnected(true);
			
			Log.d(ConnectService.TAG, "Connection established" +
					 "\n" + "IP: " + info.groupOwnerAddress);
			if (!info.isGroupOwner){
				this.context.addPeerAddress(info.groupOwnerAddress);
			} else {
				Log.d(ConnectService.TAG, "Waiting the IP address from the client");
				IPAddressReceiver rec = new IPAddressReceiver(context);
				rec.receiveData();
			}
		} else {
			context.setIsDeviceConnected(false);
		}

    
	}
}
