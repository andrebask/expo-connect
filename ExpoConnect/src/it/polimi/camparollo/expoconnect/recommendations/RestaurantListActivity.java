package it.polimi.camparollo.expoconnect.recommendations;

import it.polimi.camparollo.expoconnect.R;
import it.polimi.camparollo.expoconnect.datatransfer.RESTClient;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupMenu.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

public class RestaurantListActivity extends ListActivity {

	private ProgressDialog pDialog;

	// JSON Node names
	private static final String TAG_RESTAURANTS = "restaurants";

	// restaurants JSONArray
	JSONArray restaurants = null;

	// Hashmap for ListView
	ArrayList<HashMap<String, String>> restaurantList;
	

	public void setRestaurantList(ArrayList<HashMap<String, String>> restaurantList) {
		this.restaurantList = restaurantList;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_list);

		ListView lv = getListView();

		// Listview on item click listener
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				HashMap<String, String> r = restaurantList.get(position);
				// Starting single restaurant activity
				Intent in = new Intent(getApplicationContext(),
						SingleRestaurant.class);

				in.putExtra("restaurantMap", r);
				in.putExtra("DataShare", getIntent().getStringExtra("Data"));

				startActivity(in);

			}
		});

		// Calling async task to get json
		// Data from Wifi Direct that we have to parse and use here
		String data = getIntent().getStringExtra("Data");
		String json = data.split("!")[0];
		int index;
		try{
			index = Integer.parseInt(data.split("!")[1]);
		} catch (IndexOutOfBoundsException e) {
			index = -1;
		}

		RESTClient rc = new RESTClient(this);
		try {
			rc.setRequestBody(new JSONArray(json));
			rc.setIndex(index);
			rc.executeQuery();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
  	@Override
  	public boolean onCreateOptionsMenu(Menu menu) {
  	    // Inflate the menu items for use in the action bar
  	    MenuInflater inflater = getMenuInflater();
  	    inflater.inflate(R.menu.recommender, menu);
  	    return super.onCreateOptionsMenu(menu);
  	}
  	
  	@Override
  	public boolean onOptionsItemSelected(MenuItem item) {
  	    // Handle presses on the action bar items
  	    switch (item.getItemId()) {
  	        case R.id.menu_item_share_rec:
  	            showMenu(findViewById(R.id.menu_item_share_rec));;
  	            return true;
  	        default:
  	            return super.onOptionsItemSelected(item);
  	    }
  	}

  	public void showMenu(View v) {
  	    PopupMenu popup = new PopupMenu(this, v);
  	    MenuInflater inflater = popup.getMenuInflater();
  	    inflater.inflate(R.menu.recommender, popup.getMenu());
  	    popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				try {
					
					Intent intent = new Intent("com.google.zxing.client.android.ENCODE");
	                intent.addCategory(Intent.CATEGORY_DEFAULT);
	                intent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
	                intent.putExtra("ENCODE_DATA", getIntent().getStringExtra("Data").split("!")[0]);
	                startActivity(intent);
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "ERROR:" + e, Toast.LENGTH_SHORT).show();
					return false;

				}
				return true;
		    }

		});
  	    popup.show();
  	}
  	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			
			ListAdapter adapterRestaurantList = new RestaurantListAdapter(this, restaurantList);
			setListAdapter(adapterRestaurantList);
		}
	}
}