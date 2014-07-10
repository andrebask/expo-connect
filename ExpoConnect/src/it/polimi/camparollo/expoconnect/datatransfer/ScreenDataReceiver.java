package it.polimi.camparollo.expoconnect.datatransfer;

import it.polimi.camparollo.expoconnect.recommendations.SingleRestaurant;
import it.polimi.camparollo.expoconnect.recommendations.RestaurantListActivity;
import it.polimi.camparollo.expoconnect.wifi.StartActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class ScreenDataReceiver {
	
	private ReceiverTask task = new ReceiverTask(); 
	private Activity context;
	
	
	public ScreenDataReceiver(Activity context) {
		super();
		this.context = context;
	}

	public void receiveData(){
		
		task.execute();
		
	}
	
	public class ReceiverTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			try {
				
				ServerSocket serverSocket = new ServerSocket(8888);
				Socket client = serverSocket.accept();

				Log.d(StartActivity.TAG, "Processing incoming data");
				InputStream inputstream = client.getInputStream();
	            InputStreamReader ir = new InputStreamReader(inputstream);
	            BufferedReader br = new BufferedReader(ir, 8*1024);
	            
	            String inputLine;
	            StringBuilder sb = new StringBuilder();
	            while((inputLine = br.readLine()) != null)
	            	sb.append(inputLine);
	            br.close();   
	            
	            inputstream.close();
				serverSocket.close();
				
				Log.d(StartActivity.TAG, "Received data: " + sb.toString());
				return sb.toString();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String receivedData) {
			
			SharedPreferences settings = context.getSharedPreferences(StartActivity.TAG, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("Data", receivedData);
			editor.commit();

			
			Intent i = new Intent(context, RestaurantListActivity.class);
			i.putExtra("Data", receivedData);
			context.startActivity(i);
			
		}
		
		
	}
}
