package it.polimi.camparollo.expoconnect.wifi;

import android.annotation.SuppressLint;
import android.net.nsd.NsdManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import it.polimi.camparollo.expoconnect.exceptions.CannotEnableWifiP2pException;
import java.lang.reflect.Method;

public class CustomWifiP2pManager {
	
	static Method turnOffICS;
	static Method turnOnICS;
	static Method turnOnJB;

  
	@SuppressLint({"NewApi"})
	public static void turnOnP2P(Channel c, WifiP2pManager m) throws CannotEnableWifiP2pException {
		  Log.d(StartActivity.TAG, "turning on Wifi Direct");
	      if (android.os.Build.VERSION.SDK_INT == 14 || android.os.Build.VERSION.SDK_INT == 15) {
	    	  Log.d(StartActivity.TAG, "Version is ICS");
	          try {
	              turnOnICS = WifiP2pManager.class.getDeclaredMethod("enableP2p",WifiP2pManager.Channel.class);
	              turnOnICS.setAccessible(true);
	              turnOnICS.invoke(m, c);
	          } catch (NoSuchMethodException e) {
	        	  Log.d(StartActivity.TAG, "ICS enableP2p() not found");
	        	  throw new CannotEnableWifiP2pException();
	          } catch (Exception e) {
	        	  Log.d(StartActivity.TAG, "turnOnICS invocation failure");
	        	  throw new CannotEnableWifiP2pException();
	          }
	      } else if (android.os.Build.VERSION.SDK_INT >= 16) {
	    	  Log.d(StartActivity.TAG, "Version is JB and higher");
	          try {
	              turnOnJB = NsdManager.class.getDeclaredMethod("setEnabled", boolean.class);
	              turnOnJB.setAccessible(true);
	              turnOnJB.invoke(NsdManager.class, true);
	              //must feed it an nsdmanager, but none exists in wifidirectactivity
	              Log.d(StartActivity.TAG, "problem");
	          } catch (NoSuchMethodException e) {
	        	  Log.d(StartActivity.TAG, "JB setEnabled() not found");
	        	  throw new CannotEnableWifiP2pException();
	          } catch (Exception e) {
	        	  Log.d(StartActivity.TAG, "turnOnJB invocation failure");
	              e.printStackTrace();
	              throw new CannotEnableWifiP2pException();
	          }
	      }
	}

	public static void turnOffP2P(Channel c, WifiP2pManager m) {
		  Log.d(StartActivity.TAG, "turning Off Wifi Direct");
	      if (android.os.Build.VERSION.SDK_INT == 14 || android.os.Build.VERSION.SDK_INT == 15) {
	    	  Log.d(StartActivity.TAG, "Version is ICS");
	          try {
	              turnOffICS = WifiP2pManager.class.getDeclaredMethod("disableP2p", WifiP2pManager.Channel.class);
	              turnOffICS.setAccessible(true);
	              turnOffICS.invoke(m, c);
	          } catch (NoSuchMethodException e) {
	        	  Log.d(StartActivity.TAG, "ICS disableP2P() not found");
	          } catch (Exception e) {
	        	  Log.d(StartActivity.TAG, "turnOffICS invocation failure");
	          }
	      }
	}
}
