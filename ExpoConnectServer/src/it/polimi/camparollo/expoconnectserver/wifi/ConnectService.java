package it.polimi.camparollo.expoconnectserver.wifi;

import java.net.InetAddress;
import java.util.ArrayList;

import it.polimi.camparollo.expoconnectserver.wifi.WiFiDirectBroadcastReceiver;
import it.polimi.camparollo.expoconnectserver.CommandReceiver;
import it.polimi.camparollo.expoconnectserver.InfoActionListener;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ConnectService extends Service implements ChannelListener {

	public static final String TAG = "ExpoConnectServer";
	public static final String COMMAND_ACTION = "it.polimi.camparollo.expoconnectserver.SCREEN_COMMAND_ACTION";
	
	private WifiP2pManager manager;
	private Channel channel;
	private ArrayList<InetAddress> peersAddr = new ArrayList<InetAddress>();
  
	private final IntentFilter wifiIntentFilter = new IntentFilter();
	private final IntentFilter commandIntentFilter = new IntentFilter();
	private BroadcastReceiver wifiReceiver = null;
	private BroadcastReceiver commandReceiver = null;
	
	private boolean isWifiP2pEnabled = false;
  	private boolean retryChannel = false;
  	private boolean isDeviceConnected = false;
  	
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
	public IBinder onBind(Intent arg0) {
		return null;
	}	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		wifiIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    
	    manager = ((WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE));
	    channel = manager.initialize(this, getMainLooper(), null);
	    
	    wifiReceiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
    	registerReceiver(wifiReceiver, wifiIntentFilter);
    	
    	commandIntentFilter.addAction(COMMAND_ACTION);
    	commandReceiver =  new CommandReceiver(this);
    	registerReceiver(commandReceiver, commandIntentFilter);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void stop(){
		stopSelf();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(commandReceiver);
		unregisterReceiver(wifiReceiver);
		Toast.makeText(this, "Service killed", Toast.LENGTH_SHORT).show();
	}
	
	public void connect(String deviceAddress) {
	    WifiP2pConfig config = new WifiP2pConfig();
	    config.deviceAddress = deviceAddress;
	    config.wps.setup = WpsInfo.PBC;
	    manager.connect(channel, config, new InfoActionListener("Connecting", "Connection Error", this));
  	}
	
	public void addPeerAddress(InetAddress groupOwnerAddress) {
		peersAddr.add(groupOwnerAddress);
	}
	
	public void sendSessionId(String id){
		for (InetAddress addr : peersAddr) {
			Log.d(ConnectService.TAG, "Sending data to " + addr.toString());
			ArrayList<String> params = new ArrayList<String>();
			params.add(addr.toString());
			params.add(id);
			new ScreenDataSender(this).sendData(params);
		}
	}

  	public void onChannelDisconnected() {
	    if (manager != null && !retryChannel) {
	    	Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
	    	setRetryChannel(true);
	    	manager.initialize(this, getMainLooper(), this);
	    } else {
	    	Toast.makeText(this, "Channel lost premanently. Try Disable/Re-Enable P2P.", Toast.LENGTH_LONG).show();
	    }
  	}

}
