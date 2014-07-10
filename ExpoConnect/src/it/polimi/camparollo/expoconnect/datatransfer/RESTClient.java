package it.polimi.camparollo.expoconnect.datatransfer;

import it.polimi.camparollo.expoconnect.R;
import it.polimi.camparollo.expoconnect.recommendations.RestaurantListActivity;
import it.polimi.camparollo.expoconnect.recommendations.RestaurantListAdapter;
import it.polimi.camparollo.expoconnect.recommendations.SingleRestaurant;
import it.polimi.camparollo.expoconnect.wifi.StartActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class RESTClient {
	
	private DefaultHttpClient httpclient = new DefaultHttpClient();
	private JSONArray requestBody = new JSONArray();
	private String url = "http://recommenderservice.apphb.com/RecommenderService.svc/GetRecommendationCached"; 
	
	private RestaurantListActivity context;
	private ProgressDialog pDialog;
	private int index = -1;
	
	public RESTClient(RestaurantListActivity context) {
		this.context = context;
	}

	public void setRequestBody(JSONArray requestBody) {
		this.requestBody = requestBody;
	}
	
	public void executeQuery(){
		new NetworkTask().execute();
	}

	public void setIndex(int index) {
		this.index  = index;
	}
	
	private JSONArray getRecommendations(JSONArray usersData) throws ClientProtocolException, IOException, JSONException{
		
		Log.d(StartActivity.TAG, "Sending request to he server: " + usersData.toString());
		
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Content-type", "application/json");
		
		httppost.setEntity(new StringEntity("\"" + usersData.toString().replaceAll("\"", "'") + "\""));
		
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		  
		String responseStr = EntityUtils.toString(entity);
		
		//Log.d(StartActivity.TAG, responseStr.replace("\"[", "[").replace("\\\"", "\"").replace("\\\\", "/"));
		
		JSONArray responseArray = new JSONArray(responseStr.replace("\"[", "[").replace("\\\"", "\"").replace("\\\\", "/"));
		  
		return responseArray;
		
	}
	
	private class NetworkTask extends AsyncTask<Void, Void, JSONArray>{
		
		private ArrayList<HashMap<String, String>> restaurantList = new ArrayList<HashMap<String,String>>();
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}
		
		@Override
		protected JSONArray doInBackground(Void... params) {
			
			JSONArray restaurants;
			try {
				restaurants = getRecommendations(requestBody);
			} catch (Exception e) {
				restaurants = new JSONArray();
				Log.d(StartActivity.TAG, "Error: " + e.getMessage());
			} 
			
			try {

				// looping through All Restaurants
				for (int i = 0; i < restaurants.length(); i++) {
					JSONObject r = restaurants.getJSONObject(i);

					String id_r = r.getString(RestaurantListAdapter.TAG_ID_RESTAURNAT);
					String name = r.getString(RestaurantListAdapter.TAG_NAME_RESTAURNAT);
					String price = r.getString(RestaurantListAdapter.TAG_PRICE);
					String type = r.getString(RestaurantListAdapter.TAG_COUSINES);
					String rating = r.getString(RestaurantListAdapter.TAG_RATING);
					String addr = r.getString(RestaurantListAdapter.TAG_RESTAURANT_ADDRESS);
					String lat = r.getString(RestaurantListAdapter.TAG_LATITUDE);
					String lon = r.getString(RestaurantListAdapter.TAG_LONGITUDE);
					String web = r.getString(RestaurantListAdapter.TAG_WEB_PAGE);
					String opt = r.getString(RestaurantListAdapter.TAG_OPTIONS);
					JSONArray photos = r.getJSONArray(RestaurantListAdapter.TAG_PHOTOS);
					
					// tmp hashmap for single contact HashMap<String,
					// String>
					HashMap<String, String> restaurant = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					restaurant.put(RestaurantListAdapter.TAG_ID_RESTAURNAT, id_r);
					restaurant.put(RestaurantListAdapter.TAG_NAME_RESTAURNAT, name);
					restaurant.put(RestaurantListAdapter.TAG_PRICE, "Price range: " + price);
					restaurant.put(RestaurantListAdapter.TAG_COUSINES, "Cuisines: " + type);
					restaurant.put(RestaurantListAdapter.TAG_RATING, "User rating: " + rating);
					restaurant.put(RestaurantListAdapter.TAG_RESTAURANT_ADDRESS, addr);
					restaurant.put(RestaurantListAdapter.TAG_LATITUDE, lat);
					restaurant.put(RestaurantListAdapter.TAG_LONGITUDE, lon);
					restaurant.put(RestaurantListAdapter.TAG_WEB_PAGE, web);
					restaurant.put(RestaurantListAdapter.TAG_OPTIONS, opt);
					restaurant.put(RestaurantListAdapter.TAG_PHOTOS, photos.toString());
					
					// adding restaurant to restaurant list
					restaurantList.add(restaurant);
					
				}
				
				context.setRestaurantList(restaurantList);

			} catch (JSONException e) {
				Log.d(StartActivity.TAG, "Error: " + e.getMessage());
			}
			
			return restaurants;
		}

		@Override
		protected void onPostExecute(JSONArray response) {
			
			super.onPostExecute(response);
			//Log.d(StartActivity.TAG, "Response from the server: " + response.toString());

			if (index != -1) {
				HashMap<String, String> r = restaurantList.get(index);
				// Starting single restaurant activity
				Intent in = new Intent(context,
						SingleRestaurant.class);
	
				in.putExtra("restaurantMap", r);
				in.putExtra("DataShare", requestBody.toString());
	
				context.startActivityForResult(in, 0);
				
				// Dismiss the progress dialog
				if (pDialog.isShowing())
					pDialog.dismiss();
				
			} else {
			
				// Dismiss the progress dialog
				if (pDialog.isShowing())
					pDialog.dismiss();
				
				// Updating parsed JSON data into ListView
				ListAdapter adapterRestaurantList = new RestaurantListAdapter(context, restaurantList);
				context.setListAdapter(adapterRestaurantList);
			}
		}
		
	}

}
