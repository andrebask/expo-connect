package it.polimi.camparollo.expoconnectserver;

import it.polimi.camparollo.expoconnectserver.wifi.ConnectService;

import it.polimi.camparollo.expoconnectserver.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ServiceStarterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_starter);
		((Button) findViewById(R.id.stop_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopService(new Intent(ServiceStarterActivity.this, ConnectService.class));
				
			}
		});
		startService(new Intent(this, ConnectService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.service_starter, menu);
		return true;
	}

}
