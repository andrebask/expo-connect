package it.polimi.camparollo.expoconnectserver.wifi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;


public class ScreenDataSender {

	private SenderTask task = new SenderTask(); 
	private ConnectService context;
	
	public ScreenDataSender(ConnectService context) {
		this.context = context;
	}
	
	public void sendData(ArrayList<String> data){
		
		task.execute(data);
		
	}

	public class SenderTask extends AsyncTask<ArrayList<String>, Void, Boolean> {

		@Override
		protected Boolean doInBackground(ArrayList<String>... params) {
			
			Socket socket = new Socket();
			
			try {
				
				String ipstr = params[0].get(0).split("/")[1];
				InetAddress host = InetAddress.getByName(ipstr);
				Log.d(ConnectService.TAG, host.toString());
				int port = 8888;
				int len;
				byte buf[]  = new byte[1024];
				
			    /**
			     * Create a client socket with the host,
			     * port, and timeout information.
			     */
			    socket.bind(null);
			    socket.connect(new InetSocketAddress(host, port), 10000);

			    OutputStream outputStream = socket.getOutputStream();
			    InputStream inputStream = new ByteArrayInputStream(params[0].get(1).getBytes());
			    while ((len = inputStream.read(buf)) != -1) {
			    	Log.d(ConnectService.TAG, "sending:" + buf.toString());
			        outputStream.write(buf, 0, len);
			    }
			    outputStream.close();
			    inputStream.close();
			    Log.d(ConnectService.TAG, "Data sent");
			} catch (Exception e) {
			    e.printStackTrace();
			} finally {
			    if (socket != null) {
			        if (socket.isConnected()) {
			            try {
			                socket.close();
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
			        }
			    }
			}
			return null;
		}
		
	}
}
