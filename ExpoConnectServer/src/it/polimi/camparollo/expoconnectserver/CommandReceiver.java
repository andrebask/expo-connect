package it.polimi.camparollo.expoconnectserver;

import it.polimi.camparollo.expoconnectserver.wifi.ConnectService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CommandReceiver extends BroadcastReceiver {
	
	private static final String SESSION_ID_TAG = "sessionid"; 
	private ConnectService service;
	

	public CommandReceiver(ConnectService service) {
		super();
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String id = intent.getStringExtra(SESSION_ID_TAG);
		Log.d(ConnectService.TAG, "Command received, message to send: " + id);
		service.sendSessionId(id);
	}

}
