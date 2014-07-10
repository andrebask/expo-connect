package it.polimi.camparollo.expoconnect.datatransfer;

import it.polimi.camparollo.expoconnect.recommendations.RestaurantListAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Restaurant implements Serializable {

	protected int id;
	protected String name;
	protected String description;
	protected Float rating;
	protected String address;
	protected String mainPhotoUrl;
	protected List<String> photoUrls;
	protected double latitude;
	protected double longitude;
	protected String price;
	protected String website;
	protected String cousines;

	
	public Restaurant(HashMap<String, String> restaurantMap) throws Exception {

		this.id = Integer.parseInt(restaurantMap.get(RestaurantListAdapter.TAG_ID_RESTAURNAT));
		this.name = restaurantMap.get(RestaurantListAdapter.TAG_NAME_RESTAURNAT).replace("Name: ", "");
		//this.description = "Nulla per il momento";
		this.rating = Float.parseFloat(restaurantMap.get(RestaurantListAdapter.TAG_RATING).replace("User rating: ", ""));
		this.address = restaurantMap.get(RestaurantListAdapter.TAG_RESTAURANT_ADDRESS);
		//this.mainPhotoUrl = "niente";
		this.latitude = Double.parseDouble(restaurantMap.get(RestaurantListAdapter.TAG_LATITUDE));
		this.longitude = Double.parseDouble(restaurantMap.get(RestaurantListAdapter.TAG_LONGITUDE));
		this.price = restaurantMap.get(RestaurantListAdapter.TAG_PRICE).replace("Price range: ", "");
		this.website = restaurantMap.get(RestaurantListAdapter.TAG_WEB_PAGE);
		this.cousines = restaurantMap.get(RestaurantListAdapter.TAG_COUSINES).replace("Cuisines: ", "");
		this.description = restaurantMap.get(RestaurantListAdapter.TAG_OPTIONS);
		this.photoUrls = convertPhotoUrls(restaurantMap.get(RestaurantListAdapter.TAG_PHOTOS));
		this.mainPhotoUrl = photoUrls.get(0);
		this.photoUrls.remove(0);

	}

	private List<String> convertPhotoUrls(String string) {
		ArrayList<String> photos = new ArrayList<String>();
		try {
			JSONArray ja = new JSONArray(string);
			for (int i = 0; i < ja.length(); i++) {
				String photo = ja.getString(i);
				photos.add(photo);
			}
			return photos;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String getCousines() {
		return cousines;
	}

	public Float getRating() {
		return rating;
	}

	public String getAddress() {
		return address;
	}

	public String getMainPhotoUrl() {
		return mainPhotoUrl;
	}

	public List<String> getPhotoUrls() {
		return photoUrls;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getPrice() {
		return price;
	}

	public String getWebsite() {
		return website;
	}
}
