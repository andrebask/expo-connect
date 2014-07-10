package it.polimi.camparollo.expoconnect.wifi;

import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.util.Log;

public class InfoActionListener implements ActionListener {
	
	private StartActivity context;
	private String failureMsg;
	private String successMsg;

	public InfoActionListener(String successMsg, String failureMsg, StartActivity context) {
		this.successMsg = successMsg;
	    this.failureMsg = failureMsg;
	    this.context = context;
	}

	public void onFailure(int reason) {
		Log.d(StartActivity.TAG, this.failureMsg + " ErrorCode: " + reason);
	}

	public void onSuccess() {
		Log.d(StartActivity.TAG, this.successMsg);
	}
}
