package it.polimi.camparollo.expoconnect.uilisteners;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import it.polimi.camparollo.expoconnect.R;
import it.polimi.camparollo.expoconnect.datatransfer.ScreenDataReceiver;
import it.polimi.camparollo.expoconnect.wifi.StartActivity;

public class ConnectionInfo implements ConnectionInfoListener {
	
	private StartActivity context;

	public ConnectionInfo(StartActivity context) {
		this.context = context;
	}

	public void onConnectionInfoAvailable(WifiP2pInfo info) {
	  
		Log.d(StartActivity.TAG, "Connected");
		Log.d(StartActivity.TAG, info.toString());
		if (info.groupFormed){
			context.setIsDeviceConnected(true);
			((LinearLayout) context.findViewById(R.id.connectionLayout)).setVisibility(View.GONE);
			
			TextView text = (TextView) context.findViewById(R.id.connectionInfo);
			text.setText("Connection established");
			text.setVisibility(View.VISIBLE);
			Log.d(StartActivity.TAG, "Waiting for incoming data");
			if (info.isGroupOwner){
				new ScreenDataReceiver(context).receiveData();
			} else {
				context.sendIPAddress(info.groupOwnerAddress);
			}
		} else {
			context.setIsDeviceConnected(false);
		}

    
	}
	
	
}
