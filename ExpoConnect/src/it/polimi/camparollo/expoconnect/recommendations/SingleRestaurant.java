package it.polimi.camparollo.expoconnect.recommendations;

import it.polimi.camparollo.expoconnect.R;
import it.polimi.camparollo.expoconnect.datatransfer.Restaurant;
import it.polimi.camparollo.expoconnect.utils.ScalingUtilities;
import it.polimi.camparollo.expoconnect.utils.ScalingUtilities.ScalingLogic;
import it.polimi.camparollo.expoconnect.wifi.StartActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

public class SingleRestaurant extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommender);
		
		HashMap<String, String> restaurantMap = (HashMap<String, String>) getIntent().getSerializableExtra("restaurantMap");
		Restaurant restaurant;
		try {
			restaurant = new Restaurant(restaurantMap);
		} catch (Exception e) {
			Log.d(StartActivity.TAG, "Error: " + e.getMessage());
			return;
		}

		getActionBar().setTitle(" " + restaurant.getName());
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), restaurant);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
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
  	    	// Respond to the action bar's Up/Home button
  	  	    case android.R.id.home:
  	  	        //NavUtils.navigateUpFromSameTask(this);
  	  	    	onBackPressed();
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
	                intent.putExtra("ENCODE_DATA", getIntent().getStringExtra("DataShare"));
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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		private Restaurant restaurant;

		public SectionsPagerAdapter(FragmentManager fm, Restaurant r) {
			super(fm);
			this.restaurant = r;
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new RestaurantSectionFragment();
			Bundle args = new Bundle();
			args.putInt(RestaurantSectionFragment.ARG_SECTION_NUMBER, position);
			args.putSerializable(RestaurantSectionFragment.ARG_SECTION_DATA,
					restaurant);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class RestaurantSectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static final String ARG_SECTION_DATA = "Data";
		protected List<String> listenerurls;

		public RestaurantSectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView;
			Restaurant r = (Restaurant) getArguments().getSerializable(
					ARG_SECTION_DATA);

			ItemizedIconOverlay<OverlayItem> currentLocationOverlay;
			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
			case 0:
				rootView = inflater.inflate(R.layout.fragment_recommender_info,
						container, false);
				TextView desc = (TextView) rootView
						.findViewById(R.id.restaurantDescription);
				desc.setText(Html.fromHtml("<b>Rating</b>: " + r.getRating() + "<br/>" + "<b>Price</b>: "
						+ r.getPrice()));
				TextView info = (TextView) rootView
						.findViewById(R.id.restaurantInfo);
				info.setMovementMethod(LinkMovementMethod.getInstance());
				info.setText(Html.fromHtml("<br/>" + r.getAddress() + "<br/><br/><b>Cuisines</b>:<br/>" + r.getCousines()
						+ "<br/><br/><b>Options</b>:<br/>" + r.getDescription() 
						+ "<br/><br/><a href=\"" + "http://www.tripadvisor.it" + r.getWebsite() + "\">website</a>"));
				ImageView img = (ImageView) rootView
						.findViewById(R.id.restaurantPhoto);
				img.setScaleType(ScaleType.CENTER_CROP);
				new DownloadImageTask(img).execute(r.getMainPhotoUrl());
				break;
			case 1:
				rootView = inflater.inflate(R.layout.fragment_recommender_map,
						container, false);
				MapView mv = (MapView) rootView.findViewById(R.id.mapview);
				mv.setBuiltInZoomControls(true);
				mv.setMultiTouchControls(true);
				OverlayItem myLocationOverlayItem = new OverlayItem("Here", "Current Position", 
						new GeoPoint(r.getLatitude(), r.getLongitude()));
		        Drawable myCurrentLocationMarker = this.getResources().getDrawable(R.drawable.map_marker);
		        myLocationOverlayItem.setMarker(myCurrentLocationMarker);

		        final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		        items.add(myLocationOverlayItem);

		        currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(items,
		                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
		                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
		                        return true;
		                    }
		                    public boolean onItemLongPress(final int index, final OverlayItem item) {
		                        return true;
		                    }
		                }, new DefaultResourceProxyImpl(getActivity()));
		        mv.getOverlays().add(currentLocationOverlay);
								
				IMapController mc = mv.getController();
				mc.setZoom(20);
				mc.animateTo(new GeoPoint(r.getLatitude(), r.getLongitude()));
				break;
			case 2:
				rootView = inflater.inflate(
						R.layout.fragment_recommender_photos, container, false);
				GridView g = (GridView) rootView
						.findViewById(R.id.restaurantPhotoGrid);
				g.setAdapter(new ImageUrlAdapter(rootView.getContext(), r
						.getPhotoUrls()));
				listenerurls = r.getPhotoUrls();
				g.setOnItemClickListener(new OnItemClickListener() {
			        public void onItemClick(AdapterView<?> parent, View v,
			            int position, long id) {
			    		String url = listenerurls.get(position);
			    		Intent intent = new Intent();
			    		intent.setAction(Intent.ACTION_VIEW);
			    		intent.setDataAndType(Uri.parse(url), "image/*");
			    		startActivity(intent);
			        }
			 
				});

				break;

			default:
				rootView = inflater.inflate(R.layout.fragment_recommender_info,
						container, false);
				break;
			}

			return rootView;
		}

		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
			private ImageView imageView;

			public DownloadImageTask(ImageView iv) {
				this.imageView = iv;
			}

			protected Bitmap doInBackground(String... urls) {
				Bitmap image = null;
				BitmapFactory.Options options = new BitmapFactory.Options();
				//options.inSampleSize = 2;
				try {
					InputStream in = new URL(urls[0]).openStream();
					image = BitmapFactory.decodeStream(in, null, options);
					image = ScalingUtilities.createScaledBitmap(image, 135, 250, ScalingLogic.CROP);
					in.close();
				} catch (Exception e) {
					Log.e(StartActivity.TAG, e.getMessage());
					e.printStackTrace();
				}
				return image;
			}

			protected void onPostExecute(Bitmap result) {
				imageView.setImageBitmap(result);
			}
		}

		public class ImageUrlAdapter extends BaseAdapter {
			private Context mContext;
			protected List<String> photoUrls;
			private List<ImageView> images = new ArrayList<ImageView>();

			public ImageUrlAdapter(Context c, List<String> urls) {
				
				mContext = c;
				photoUrls = urls;
				
				for (String url : urls) {
					ImageView imageView;
					imageView = new ImageView(mContext);
					imageView.setScaleType(ScaleType.CENTER_CROP);
					AbsListView.LayoutParams vp = new AbsListView.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					imageView.setLayoutParams(vp);
					new DownloadImageTask(imageView).execute(url);
					images.add(imageView);
				}
			}

			public int getCount() {
				return photoUrls.size();
			}

			public Object getItem(int position) {
				return photoUrls.get(position);
			}

			public long getItemId(int position) {
				return 0;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				return images.get(position);
			}
		}
		
	}
}
