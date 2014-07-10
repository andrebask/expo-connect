package it.polimi.camparollo.expoconnect.recommendations;

import it.polimi.camparollo.expoconnect.R;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class RestaurantListAdapter extends SimpleAdapter {
	
	public static final String TAG_ID_RESTAURNAT = "idRistorante";
	public static final String TAG_NAME_RESTAURNAT = "nome";
	public static final String TAG_PRICE = "dettaglioPrezzo";
	public static final String TAG_RATING = "voto";
	public static final String TAG_CITY = "localita";
	public static final String TAG_RESTAURANT_ADDRESS = "indirizzo";
	public static final String TAG_LATITUDE = "latitudine";
	public static final String TAG_LONGITUDE = "longitudine";
	public static final String TAG_WEB_PAGE = "paginaWeb";
	public static final String TAG_COUSINES = "dettaglioCucina";
	public static final String TAG_OPTIONS = "opzioni";
	public static final String TAG_PHOTOS = "images";

	public RestaurantListAdapter(Context context, List<? extends Map<String, ?>> data) {
		super(context, data, R.layout.list_restaurants, new String[] {
				TAG_NAME_RESTAURNAT, TAG_COUSINES, TAG_RATING,
				TAG_PRICE }, new int[] { R.id.restaurantName,
				R.id.type, R.id.rating, R.id.price });
	}



}
