package it.polimi.camparollo.expoconnectserver.wifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class IPAddressReceiver {
	
	private ReceiverTask task = new ReceiverTask(); 
	private ConnectService context;
	
	
	public IPAddressReceiver(ConnectService context) {
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
				
				ServerSocket serverSocket = new ServerSocket(7777);
				Socket client = serverSocket.accept();

				Log.d(ConnectService.TAG, "Reading client ip address");
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
				
				Log.d(ConnectService.TAG, "Received ip: " + client.getInetAddress().toString());
				context.addPeerAddress(client.getInetAddress());
				return client.getInetAddress().toString();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String receivedIP) {
			Log.d(ConnectService.TAG, "IP address received: " + receivedIP);
		}
		
		
	}
}
