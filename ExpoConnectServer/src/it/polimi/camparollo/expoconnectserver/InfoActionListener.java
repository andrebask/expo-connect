package it.polimi.camparollo.expoconnectserver;

import it.polimi.camparollo.expoconnectserver.wifi.ConnectService;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.util.Log;

public class InfoActionListener implements ActionListener {
	
	private ConnectService context;
	private String failureMsg;
	private String successMsg;

	public InfoActionListener(String successMsg, String failureMsg, ConnectService context) {
		this.successMsg = successMsg;
	    this.failureMsg = failureMsg;
	    this.context = context;
	}

	public void onFailure(int reason) {
		Log.d(ConnectService.TAG, this.failureMsg + " ErrorCode: " + reason);
	}

	public void onSuccess() {
		Log.d(ConnectService.TAG, this.successMsg);
	}
}
