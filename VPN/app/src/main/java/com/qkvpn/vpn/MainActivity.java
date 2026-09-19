package com.qkvpn.vpn;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.qkvpn.vpn.handlers.PrefManager;
import com.qkvpn.vpn.handlers.SharedPreferencesManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ConnectionStatus;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;
import dmax.dialog.SpotsDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.qkvpn.vpn.Choose_Server.Choose_Server_Activity;
import com.qkvpn.vpn.Choose_Server.Model_From_Api;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.qkvpn.vpn.handlers.VPN_Time_Service;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.qkvpn.vpn.VpnAuthActivity.KEY_PASSWORD;
import static com.qkvpn.vpn.VpnAuthActivity.KEY_USERNAME;
import static com.qkvpn.vpn.handlers.VPN_Time_Service.*;


public class MainActivity extends AppCompatActivity {

	public final static String BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS";
	protected OpenVPNService openvpn_service;
	private ConnectionStatus openVpnStatus;
	CountDownTimer timer_connecting;
	ServiceConnection service_connection;
	ImageView connection_btn,flag_icon , flag_icon2;
	private long mStartRX = 0;
	private long mStartTX = 0;
	TextView textView_download,textView_upload , server_country;
	private Handler mHandler = new Handler();
	RelativeLayout choose_server;
	public static boolean is_run = false;
	ArrayList<api_response_model> api_array;
	int array_id_num;
	api_response_model api_model;
	String cert_Value;
	SharedPreferences main_Activity_SP;

	int position = 0;
	int lastPosition = 0;
	String hrSize;
	boolean isRunning_connected = true;

	private Chronometer chronometer2;

	public static String privacypolicy = "";
	public static String termsandconditions = "";
	public static String aboutus = "";
	public static String contactus = "";




	private DrawerLayout dl;
	private TextView title_tv,des_tv,PlayButton;
	private Dialog PTDiaglogbox;


	Button Dis_disconnect;
	Button Dis_Cancle;
	FrameLayout Dis_FrameLayout;

	public static Dialog LocationDialogSub;
	public static Dialog DisconnectDialog;
	private PrefManager prefManager;
	public static BillingProcessor bp;
	private com.google.android.gms.ads.formats.UnifiedNativeAd nativeAd = null;
	RelativeLayout layout;
	com.google.android.gms.ads.AdView adView;
	private com.google.android.gms.ads.InterstitialAd sInterstitial;
	public BroadcastReceiver mMessageReceiver3 = new BroadcastReceiver() {
		@SuppressLint("SetTextI18n")
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("mMessageReceiver3" , "heree");
			if (intent.getExtras()!=null)
			{
				int position_array = intent.getExtras().getInt("ValueId_ovpn");
				if (is_run)
				{
					if (chronometer2 != null) {
						chronometer2.setBase(SystemClock.elapsedRealtime());
						chronometer2.stop();
					}

					Log.d("Position_array" , ""+position_array);
					if (isVpnConnectionActive()) {

						ProfileManager.setConntectedVpnProfileDisconnected(MainActivity.this);
						if (openvpn_service != null && openvpn_service.getManagement() != null) {
							openvpn_service.getManagement().stopVPN(false);
						}

						new Handler().postDelayed(() -> {
							perform_connection(position_array);
						}, 1000);
					}
					else {
						perform_connection(position_array);
					}
				}
			}
		}
	};

	public BroadcastReceiver mMessageReceiver4 = new BroadcastReceiver() {
		@SuppressLint("SetTextI18n")
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("mMessageReceiver4" , "heree");
			if (intent.getExtras() != null) {
				String premium_value = intent.getExtras().getString("premium_dialog");
				if (premium_value != null) {
					if (premium_value.equals("premium")) {
						if (is_run) {
							if (LocationDialogSub != null) {
								LocationDialogSub.show();
							}
						}
					} else {
						Toast.makeText(MainActivity.this, "Unable to fetch data, try again..!!", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};

	private LottieAnimationView connecting_animationView,connect_animationview;
	private RequestQueue requestQueue;
	private final String TAG = null;
	private com.facebook.ads.InterstitialAd fbinterstitialAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// Initialize the Audience Network SDK
		AudienceNetworkAds.initialize(this);
		SharedPreferencesManager.init(this);
		textView_download= findViewById(R.id.textView_download);
		textView_upload= findViewById(R.id.textView_upload);
		server_country= findViewById(R.id.server_country);
		flag_icon= findViewById(R.id.flag_icon);
		flag_icon2= findViewById(R.id.flag_icon2);
		main_Activity_SP = getSharedPreferences("DATA", MODE_PRIVATE);
		connection_btn = findViewById(R.id.connection_btn);
		choose_server = findViewById(R.id.choose_server);
		ImageView share_us_tv = (ImageView) findViewById(R.id.share_us_tv);
		PlayButton = (TextView)findViewById(R.id.play);
		chronometer2 = findViewById(R.id.time_info_tv);
//		mLottieAnimationView = findViewById(R.id.animationView);
		connecting_animationView = findViewById(R.id.connecting_animationView);
		connect_animationview = findViewById(R.id.connect_animationView);
		adView = new AdView(this);
		layout = findViewById(R.id.banner);
		LinearLayout subscription_ll = (LinearLayout) findViewById(R.id.subscription_ll);
		setupNavDrawable();
		initiateprivacytermscondition();
		initSubscription();
		InitiateSubscription();
		InitiateDiscountDialog();
		fetchDataFrommysql();

		choose_server.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Choose_Server_Activity.class)));

		flag_icon.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Choose_Server_Activity.class)));




		load_ovpn_Services();


		if (!isVpnConnectionActive()) {
			fetch_data_from_api();
//			connect_animationview.setSpeed((float) 0.2);
//			connected_animationview.setSpeed((float) 0.2);
//			mLottieAnimationView.setVisibility(View.INVISIBLE);
			connection_btn.setVisibility(View.VISIBLE);
			connecting_animationView.setVisibility(View.INVISIBLE);
//			connect_animationview.setVisibility(View.VISIBLE);
		} else {

			PlayButton.setText("Connected");
			connection_btn.setImageResource(R.drawable.connected);
			connection_btn.setVisibility(View.VISIBLE);
			show_timer_value();
//
//			connect_animationview.setSpeed((float) 0.2);
//			connected_animationview.setSpeed((float) 0.2);
//			mLottieAnimationView.setVisibility(View.INVISIBLE);
			connecting_animationView.setVisibility(View.INVISIBLE);
//			connect_animationview.setVisibility(View.INVISIBLE);
		}
		LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMessageReceiver3, new IntentFilter("server_ovpn"));
		LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMessageReceiver4, new IntentFilter("show_premium_dialog"));

		connection_btn.setOnClickListener(view -> {
			array_id_num = 0;
			perform_connection(array_id_num);
		});






		prefManager = new PrefManager(getBaseContext(),PrefManager.PRF_APP_DATA,PrefManager.MODE_READ);
		int opencCount = prefManager.ReadInt(PrefManager.KEY_OPEN_COUNT);
		if(opencCount != 0){
		}
		opencCount++;
		prefManager = new PrefManager(getBaseContext(),PrefManager.PRF_APP_DATA,PrefManager.MODE_WRITE);
		prefManager.SaveIntData(PrefManager.KEY_OPEN_COUNT, opencCount);






		share_us_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(dl!=null)
					dl.openDrawer(Gravity.LEFT);
			}
		});

	/*	btnrate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rateUs();

			}
		});*/

		subscription_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LocationDialogSub.show();

			}
		});
	}



	private void show_timer_value()
	{
		Log.d("stoppedMilliseconds" , ""+ getTimer_value());
		show_download_upload();
		chronometer2.setOnChronometerTickListener(chronometer -> chronometer2.setText(getTimer_value()));
		chronometer2.setBase(SystemClock.elapsedRealtime());
		chronometer2.start();
	}

	private void stop_timer()
	{
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
		stopService(new Intent(MainActivity.this, VPN_Time_Service.class));
		Log.d("Rating_Value_timer", "" + getTimer_value());
		if (getTimer_value() != null) {
			if (ending_time != null && starting_time != null) {
				chronometer2.setText(getTimer_value());
			}
		}
	}


	private void start_timer_again()
	{
		download_upload();
		try {
			stopService(new Intent(MainActivity.this, VPN_Time_Service.class));
		} catch (Exception e) {
			e.printStackTrace();
		}

		mHandler=new Handler();
		mHandler.postDelayed(() -> {
			chronometer2.setBase(SystemClock.elapsedRealtime());
			chronometer2.stop();
			startService(new Intent(MainActivity.this, VPN_Time_Service.class));
			chronometer2.setOnChronometerTickListener(chronometer -> {

				if (getTimer_value() == null) {
					long time = SystemClock.elapsedRealtime() - chronometer.getBase();
					int h = (int) (time / 3600000);
					int m = (int) (time - h * 3600000) / 60000;
					int s = (int) (time - h * 3600000 - m * 60000) / 1000;
					String t = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
					chronometer2.setText(t);
				} else {
					chronometer2.setText("" + getTimer_value());
				}
			});
			chronometer2.setBase(SystemClock.elapsedRealtime());
			chronometer2.start();

		},1000);
	}


	private void fetch_data_from_api()
	{
		hit_api();
	}

	private void hit_api()
	{
		if (isNetworkAvailable()) {
			show_dialog();
			RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
			String url = DataManager.servers;
			StringRequest stringRequest = new StringRequest(Request.Method.GET, url, this::parse_data, error -> {
			});
			stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 3, 3));
			queue.add(stringRequest);
		} else {
			Toast.makeText(this, "Internet Required..!!", Toast.LENGTH_SHORT).show();
		}


	}

	public void showInterstitialIfAvailable() {
		if (DataManager.ADMOB_ENABLE) {
		if (fbinterstitialAd != null && fbinterstitialAd.isAdLoaded()) {
			{ fbinterstitialAd.show(); }
		}
		else if (sInterstitial != null && sInterstitial.isLoaded()) {
			{
				sInterstitial.show();
			}
		}
		}
	}
    private void loadAdmobBanner() {


			if (getString(R.string.Ad_type).equals("1")) {

				adView.setAdSize(AdSize.BANNER);
				layout.setVisibility(View.VISIBLE);
				layout.addView(adView);
				adView.setAdUnitId(getString(R.string.Admob_banner_ad));
				AdRequest adRequest = new AdRequest.Builder().build();
				adView.loadAd(adRequest);

				adView.setAdListener(new AdListener() {
					@Override
					public void onAdFailedToLoad(int i) {
						super.onAdFailedToLoad(i);
						// loadfbBanner();
					}
				});
				adView.loadAd(adRequest);
			} else {

				com.facebook.ads.AdView fbadView = new com.facebook.ads.AdView(MainActivity.this, getString(R.string.Facebook_banner_ad), com.facebook.ads.AdSize.BANNER_HEIGHT_50);

				// Find the Ad Container
				RelativeLayout adContainer = (RelativeLayout) findViewById(R.id.fb_banner);
				adContainer.setVisibility(View.VISIBLE);

				// Add the ad view to your activity layout
				adContainer.addView(fbadView);

				// Request an ad
				fbadView.loadAd();
			}


    }

	private void loadadmobinterstitial() {


		if (getString(R.string.Ad_type).equals("1")) {

			sInterstitial = new com.google.android.gms.ads.InterstitialAd(getApplicationContext());
			sInterstitial.setAdUnitId(getString(R.string.Admob_interstitial_ad));
			sInterstitial.loadAd(new AdRequest.Builder().build());
			sInterstitial.setAdListener(new AdListener() {
				@Override
				public void onAdLoaded() {
					super.onAdLoaded();
				//	sInterstitial.show();
				}

				@Override
				public void onAdClosed() {
					super.onAdClosed();
					loadadmobinterstitial();


				}

				@Override
				public void onAdFailedToLoad(int errorCode) {
					// Code to be executed when an ad request fails.

				}
			});

		} else {


			loadfbinterstitial();

		}



	}

	private void loadfbinterstitial() {
		fbinterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.Facebook_interstitial));


		fbinterstitialAd.setAdListener(new InterstitialAdListener() {
			@Override
			public void onInterstitialDisplayed(Ad ad) {
				Log.e(TAG, "Interstitial ad displayed.");
			}

			@Override
			public void onInterstitialDismissed(Ad ad) {
				Log.e(TAG, "Interstitial ad dismissed.");
				loadadmobinterstitial();
			}

			@Override
			public void onError(Ad ad, AdError adError) {
				Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());

			}

			@Override
			public void onAdLoaded(Ad ad) {
				Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
			}

			@Override
			public void onAdClicked(Ad ad) {
				Log.d(TAG, "Interstitial ad clicked!");
			}

			@Override
			public void onLoggingImpression(Ad ad) {
				Log.d(TAG, "Interstitial ad impression logged!");
			}
		});
		fbinterstitialAd.loadAd();
	}


	public void InitiateSubscription(){
		LocationDialogSub = new Dialog(this,R.style.AppTheme);
		LocationDialogSub.setContentView(R.layout.layout_subscription);


		ImageView ic_back_iv = (ImageView) LocationDialogSub.findViewById(R.id.iv_close_sub);
		LinearLayout one_mounth_ll = (LinearLayout) LocationDialogSub.findViewById(R.id.three_mounth_ll);
		LinearLayout six_month_ll = (LinearLayout) LocationDialogSub.findViewById(R.id.six_months_ll);
		LinearLayout yearly_ll = (LinearLayout) LocationDialogSub.findViewById(R.id.one_year_ll);
		prefManager = new PrefManager(getBaseContext(),PrefManager.PRF_APP_DATA, PrefManager.MODE_WRITE);
		ic_back_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LocationDialogSub.dismiss();
			}
		});
		one_mounth_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(bp.isSubscribed(SharedPreferencesManager.getString("subscription",""))){
					bp.updateSubscription(MainActivity.this,SharedPreferencesManager.getString("subscription",""),DataManager.ONE_SUBSCRIPTION_ID);
				}else{
					bp.subscribe(MainActivity.this,DataManager.ONE_SUBSCRIPTION_ID);
				}
			}
		});
		six_month_ll.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(bp.isSubscribed(SharedPreferencesManager.getString("subscription",""))){
							bp.updateSubscription(MainActivity.this,SharedPreferencesManager.getString("subscription",""),DataManager.THREE_SUBSCRIPTION_ID);
						}else{
							bp.subscribe(MainActivity.this,DataManager.THREE_SUBSCRIPTION_ID);
						}
					}
				});
		yearly_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(bp.isSubscribed(SharedPreferencesManager.getString("subscription",""))){
					bp.updateSubscription(MainActivity.this,SharedPreferencesManager.getString("subscription",""),DataManager.YEARLY_SUBSCRIPTION_ID);
				}else{
					bp.subscribe(MainActivity.this,DataManager.YEARLY_SUBSCRIPTION_ID);
				}
			}
		});

	}


	private void initSubscription(){

		if(!BillingProcessor.isIabServiceAvailable(this)) {
			Toast.makeText(MainActivity.this,"In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16",Toast.LENGTH_SHORT).show();
		}

		bp = new BillingProcessor(this, DataManager.LICENSE_KEY, DataManager.MERCHANT_ID, new BillingProcessor.IBillingHandler() {
			@Override
			public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
				checkIfUserIsSusbcribed();
			}
			@Override
			public void onBillingError(int errorCode, @Nullable Throwable error) {
			}
			@Override
			public void onBillingInitialized() {
				DataManager.readyToPurchase = true;
				checkIfUserIsSusbcribed();
			}
			@Override
			public void onPurchaseHistoryRestored() {
				checkIfUserIsSusbcribed();
			}
		});

	}



	private void rateUs() {
		final RatingDialog ratingDialog;
		ratingDialog = new RatingDialog.Builder(this)


				.positiveButtonTextColor(R.color.colorPrimary)
				.feedbackTextColor(R.color.colorPrimary)
				.threshold(4)
				.ratingBarColor(R.color.colorPrimary)
				.onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
					@Override
					public void onFormSubmitted(String feedback) {
						StringRequest stringRequest=new StringRequest(Request.Method.POST, DataManager.feedback, new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {

							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {

							}
						}){
							@Override
							protected Map<String, String> getParams()  {
								Map<String,String>parms=new HashMap<String, String>();
								parms.put("msg",feedback);
								return parms;
							}
						};
						RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
						requestQueue.add(stringRequest);


					}
				}).build();

		ratingDialog.show();
	}
	private void setupNavDrawable(){
		dl = (DrawerLayout)findViewById(R.id.activity_main);
		ActionBarDrawerToggle t = new ActionBarDrawerToggle(this, dl, R.string.ok, R.string.cancel);

		dl.addDrawerListener(t);
		t.syncState();


		NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
//		nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//			@Override
//			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//				int id = item.getItemId();
//				switch(id)
//				{
//					case R.id.nav_vip:
//
//						LocationDialogSub.show();
//
//						break;
//					case R.id.nav_contact_us:
//						Spanned sp;
//
//						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//							sp = Html.fromHtml(contactus, Html.FROM_HTML_MODE_COMPACT);
//						}else {
//							sp = Html.fromHtml(contactus);
//						}
//						title_tv.setText("Contact Us");
//						des_tv.setText(sp);
//						PTDiaglogbox.show();
//						break;
//					case R.id.nav_rate:
//
//						rateUs();
//
//						break;
//					case R.id.nav_terms:
//
//
//
//						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//							sp = Html.fromHtml(termsandconditions, Html.FROM_HTML_MODE_COMPACT);
//						}else {
//							sp = Html.fromHtml(termsandconditions);
//						}
//						title_tv.setText(R.string.TermsConditions);
//						des_tv.setText(sp);
//						PTDiaglogbox.show();
//						break;
//					case R.id.nav_privacy:
//
//						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//							sp = Html.fromHtml(privacypolicy, Html.FROM_HTML_MODE_COMPACT);
//						}else {
//							sp = Html.fromHtml(privacypolicy);
//						}
//						title_tv.setText("Privacy Policy");
//						des_tv.setText(sp);
//						PTDiaglogbox.show();
//						break;
//					case R.id.nav_about:
//
//						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//							sp = Html.fromHtml(aboutus, Html.FROM_HTML_MODE_COMPACT);
//						}else {
//							sp = Html.fromHtml(aboutus);
//						}
//						title_tv.setText("About Us");
//						des_tv.setText(sp);
//						PTDiaglogbox.show();
//						break;
//					case R.id.nav_share:
//						Intent sendIntent = new Intent();
//						sendIntent.setAction(Intent.ACTION_SEND);
//						sendIntent.putExtra(Intent.EXTRA_TEXT,
//								"Download "+getResources().getString(R.string.app)+" : "+"https://play.google.com/store/apps/details?id=" + getPackageName());
//						sendIntent.setType("text/plain");
//						startActivity(sendIntent);
//						break;
//					default:
//						return true;
//				}
//
//				dl.closeDrawer(Gravity.LEFT);
//				return true;
//
//			}
//		});
	}





	void checkIfUserIsSusbcribed(){
		boolean purchaseResult = bp.loadOwnedPurchasesFromGoogle();
		RelativeLayout admob_rl = findViewById(R.id.banner_layout);
		if(bp.listOwnedSubscriptions().size()>0) {
			SharedPreferencesManager.setString("subscription", bp.listOwnedSubscriptions().get(0));
		}else{
			SharedPreferencesManager.setString("subscription","");
		}
		if(purchaseResult) {
			TransactionDetails subscriptionTransactionDetails = bp.getSubscriptionTransactionDetails(SharedPreferencesManager.getString("subscription", ""));
			if (subscriptionTransactionDetails != null) {

				DataManager.ADMOB_ENABLE=false;
				admob_rl.setVisibility(View.GONE);
				getExpireDate(subscriptionTransactionDetails.purchaseInfo.purchaseData.purchaseTime.toString());
			}else{
				DataManager.ADMOB_ENABLE=true;
				admob_rl.setVisibility(View.VISIBLE);
				getExpireDate("");
			}
		}else{
			getExpireDate("");
		}



	}
	private void getExpireDate(String inputText){
		TextView sub_tv = (TextView) LocationDialogSub.findViewById(R.id.Sub_tv);
		TextView text = (TextView)findViewById(R.id.subcription_tv);
		if(!TextUtils.isEmpty(inputText) && bp.isSubscribed(SharedPreferencesManager.getString("subscription",""))) {
			SimpleDateFormat inputFormat = new SimpleDateFormat
					("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
			inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

			SimpleDateFormat outputFormat =
					new SimpleDateFormat("MMM dd, yyyy");
			Date date = null;
			try {
				date = inputFormat.parse(inputText);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				switch (SharedPreferencesManager.getString("subscription","")){
					case DataManager.ONE_SUBSCRIPTION_ID:
						cal.add(Calendar.MONTH, 1);
						break;
					case DataManager.THREE_SUBSCRIPTION_ID:
						cal.add(Calendar.MONTH, 3);
						break;
					case DataManager.YEARLY_SUBSCRIPTION_ID:
						cal.add(Calendar.MONTH, 12);
						break;
				}

				String outputText = outputFormat.format(cal.getTime());
				text.setText(String.format("%s","Expired at: "+outputText));
				sub_tv.setText(R.string.Expired_At+"\n"+outputText);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			text.setText(String.format("%s","GO PREMIUM"));
			sub_tv.setText(R.string.VIP_specific_features);

		}

	}



	public void initiateprivacytermscondition(){
		PTDiaglogbox = new Dialog(this,R.style.AppTheme);
		PTDiaglogbox.setContentView(R.layout.layout_termscondition);

		title_tv=(TextView)PTDiaglogbox.findViewById(R.id.title_tv);
		des_tv=(TextView)PTDiaglogbox.findViewById(R.id.des_tv);
		ImageView ic_back_iv = (ImageView) PTDiaglogbox.findViewById(R.id.iv_close_sub) ;
		ic_back_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PTDiaglogbox.dismiss();
			}
		});
	}

	private void fetchDataFrommysql() {

		StringRequest stringRequest = new StringRequest(DataManager.settings, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.i("mytag", response);

				try {
					JSONObject jsonObject = new JSONObject(response);

					privacypolicy = jsonObject.getString("privacy_policy");
					termsandconditions = jsonObject.getString("terms");
					aboutus = jsonObject.getString("about_us");
					contactus = jsonObject.getString("contact_us");


				} catch (JSONException e) {
					e.printStackTrace();

				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();


			}
		});

		Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
	}

	SpotsDialog AlertDialog;
	private void show_dialog()
	{

		AlertDialog = (SpotsDialog) new SpotsDialog.Builder()
				.setContext(this)
				.setMessage(R.string.custom_title)
				.setTheme(R.style.Custom)
				.setCancelable(false)
				.build();
		AlertDialog.show();



	}

	private void  hide_dialog()
	{
		AlertDialog.dismiss();

	}

	private void parse_data(String s)
	{
		api_array = new ArrayList<>();
		try {
			JSONObject jsonObject = new JSONObject(s);
			JSONArray jsonArray = jsonObject.getJSONArray("servers");

			if (jsonArray.length() > 0) {

				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject object = jsonArray.getJSONObject(i);
					try {
						api_model = new api_response_model();
						api_model.setServer_id(object.getInt("server_id"));
						api_model.setType(object.getInt("Type"));
						api_model.setHostName(object.getString("HostName"));
						api_model.setFlag(object.getString("Flag"));

						byte[] data = new byte[0];
						try {
							if (!object.getString("IP").isEmpty()) {
								data = Base64.decode(object.getString("IP"), Base64.DEFAULT);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
							cert_Value = new String(data, StandardCharsets.UTF_8);
						}
						api_model.setIp_cert(cert_Value);
						api_array.add(api_model);
						Log.d("ARRAY_Ee", "" + api_array.get(i).getType());
						Log.d("ARRAY_ELEMENTS2", "" + api_array.get(i).getIp_cert());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				if (main_Activity_SP != null) {

					if (api_array != null && !api_array.isEmpty()) {
						SharedPreferences.Editor editor = main_Activity_SP.edit();
						if (editor != null) {
							//list saved to cache
							Gson gson = new Gson();
							String json = gson.toJson(api_array);
							if (!json.isEmpty()) {
								if (isJSONValid(json)) {
									editor.remove("saved_list_to_cache").remove("host_name_sp").remove("flag_sp").apply();

									if (api_array != null && !api_array.isEmpty()) {
										editor.putString("host_name_sp", api_array.get(0).getHostName())
												.putString("flag_sp" , api_array.get(0).getFlag()).apply();
										server_country.setText(api_array.get(0).getHostName());
										flag_icon.setImageResource(getResources().getIdentifier(api_array.get(0).getFlag(), "drawable", getPackageName()));
										flag_icon2.setImageResource(getResources().getIdentifier(api_array.get(0).getFlag(), "drawable", getPackageName()));
									}
									editor.putString("saved_list_to_cache", json).apply();
									hide_dialog();

								}
							}

						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			// edited, to include @Arthur's comment
			// e.g. in case JSONArray is valid as well...
			try {
				new JSONArray(test);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}


	private void load_ovpn_Services()
	{
		BroadcastReceiver opnVPNStateReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent.getStringExtra("status") != null) {

					openVpnStatus = ConnectionStatus.valueOf(intent.getStringExtra("status"));

					Log.d("OpenVpnStatus", intent.getStringExtra("status"));
				}
			}
		};

		registerReceiver(opnVPNStateReceiver, new IntentFilter(BROADCAST_ACTION));

		load_services();
	}

	private void load_services() {
		load_ovpn_service();
		Intent intentss = new Intent(MainActivity.this, OpenVPNService.class);
		intentss.setAction(OpenVPNService.START_SERVICE);
		bindService(intentss, service_connection, Context.BIND_AUTO_CREATE);
	}

	private void load_ovpn_service() {
		service_connection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName className, IBinder service) {
				OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
				openvpn_service = binder.getService();
			}

			@Override
			public void onServiceDisconnected(ComponentName componentName) {

			}
		};
	}

	private void perform_connection(int array_id)
	{
		if (isNetworkAvailable()) {
			is_run = false;
			Log.d("Aarray_id", "" + array_id);
			if (api_array == null || api_array.isEmpty()) {
				if (getAllServers() != null) {
					api_array = getAllServers();
				} else {
					Toast.makeText(this, "Error in retreiving , try again..!!", Toast.LENGTH_SHORT).show();
				}
			}

			if (!isVpnConnectionActive()) {
				PlayButton.setText("Connecting...");

				connection_btn.setImageResource(R.drawable.connecting);
				connection_btn.setVisibility(View.VISIBLE);
//				mLottieAnimationView.setVisibility(View.INVISIBLE);
				connecting_animationView.setVisibility(View.VISIBLE);
				connecting_animationView.playAnimation();
//				connect_animationview.setVisibility(View.INVISIBLE);
				if (api_array != null && api_array.size() > 0) {
					String config_data = api_array.get(array_id).getIp_cert();
					if (config_data != null && !config_data.isEmpty()) {

						Log.d("Config_data", "" + config_data);
						try {
							start_My_Vpn(MainActivity.this, config_data, null, null);
						} catch (RemoteException e) {

							e.printStackTrace();
						}

						if (timer_connecting != null) {
							timer_connecting.cancel();
						}

						timer_connecting = new CountDownTimer(20000, 1000) {
							public void onTick(long millisUntilFinished) {
								if (openVpnStatus != null && openVpnStatus.equals(ConnectionStatus.LEVEL_CONNECTED)) {
									runOnUiThread(() -> {
										if (timer_connecting != null) {
											timer_connecting.cancel();
										}

										show_download_upload();

										PlayButton.setText("Connected");
										showInterstitialIfAvailable();
										connection_btn.setImageResource(R.drawable.connected);
//										chronometer2.setBase(SystemClock.elapsedRealtime());
//										chronometer2.start();
										start_timer_again();
//										connect_animationview.setSpeed((float) 0.2);
//										connected_animationview.setSpeed((float) 0.2);
//										mLottieAnimationView.setVisibility(View.INVISIBLE);
										connection_btn.setVisibility(View.VISIBLE);
//										connect_animationview.setVisibility(View.INVISIBLE);
										connecting_animationView.setVisibility(View.INVISIBLE);

									});
								}
							}

							public void onFinish() {

								hide_dialog();
								runOnUiThread(() -> {
									ProfileManager.setConntectedVpnProfileDisconnected(MainActivity.this);
									if (openvpn_service != null && openvpn_service.getManagement() != null) {
										openvpn_service.getManagement().stopVPN(false);

										if (VpnStatus.getLevel("") != null) {

											PlayButton.setText("Not Connected");
//											chronometer2.stop();

											stop_timer();
											connection_btn.setImageResource(R.drawable.disconnected);
//											connect_animationview.setSpeed((float) 0.2);
//											connected_animationview.setSpeed((float) 0.2);
//											mLottieAnimationView.setVisibility(View.INVISIBLE);
											connection_btn.setVisibility(View.VISIBLE);
											connecting_animationView.setVisibility(View.INVISIBLE);
//											connect_animationview.setVisibility(View.VISIBLE);
										}
									}
								});
							}
						}.start();
					}
				}
			} else {
				showInterstitialIfAvailable();
				DisconnectDialog.show();

			}
		} else {
			Toast.makeText(MainActivity.this, "Your Internet is not Working", Toast.LENGTH_SHORT).show();
		}
	}




	public void start_My_Vpn(Context context, String inlineConfig, String userName, String pw) throws RemoteException {

		if (context != null) {
			if (TextUtils.isEmpty(inlineConfig)) {

				throw new RemoteException("config is empty");
			}

			Intent intent = VpnService.prepare(context);

			if (intent != null) {
				intent = new Intent(context, VpnAuthActivity.class);
				intent.putExtra(VpnAuthActivity.KEY_CONFIG, inlineConfig);
				if (!TextUtils.isEmpty(userName)) {
					intent.putExtra(KEY_USERNAME, userName);
				}
				if (!TextUtils.isEmpty(pw)) {
					intent.putExtra(KEY_PASSWORD, pw);
				}

				context.startActivity(intent);
			} else {
				startVpnInternal(context, inlineConfig, userName, pw);
			}
		}
	}

	void startVpnInternal(Context context, String inlineConfig, String userName, String pw) throws RemoteException {

		if (context != null) {
			ConfigParser cp = new ConfigParser();
			try {
				cp.parseConfig(new StringReader(inlineConfig));
				VpnProfile vp = cp.convertProfile();
				vp.mName = Build.MODEL;
				if (vp.checkProfile(context) != R.string.no_error_found) {

					throw new RemoteException(context.getString(vp.checkProfile(context)));
				}

				try {
					vp.mProfileCreator = context.getPackageName();
					vp.mUsername = userName;
					vp.mPassword = pw;
					ProfileManager.setTemporaryProfile(MainActivity.this, vp);
					VPNLaunchHelper.startOpenVpn(vp, context);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (IOException | ConfigParser.ConfigParseError e) {
				throw new RemoteException(e.getMessage());
			}
		}
	}

	public void stopHandler_home_screen() {
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
			isRunning_connected=false;
		}
	}


	@Override
	public void onBackPressed() {
		finishAffinity();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		stopHandler_home_screen();
		chronometer2.stop();
		super.onDestroy();
	}


	public static boolean isVpnConnectionActive() {
		List<String> networks = new ArrayList<>();

		try {
			for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				if (networkInterface.isUp()) {
					networks.add(networkInterface.getName());
				}
			}
		} catch (Exception ignored) {

		}

		return networks.contains("tun0");
	}

	@Override
	protected void onResume() {

		if (main_Activity_SP != null) {
			String host_name = main_Activity_SP.getString("host_name_sp", null);
			String flag_name = main_Activity_SP.getString("flag_sp", null);
			if (host_name != null && flag_name!=null) {
				server_country.setText(host_name);
				flag_icon.setImageResource(getResources().getIdentifier(flag_name, "drawable", getPackageName()));
				flag_icon2.setImageResource(getResources().getIdentifier(flag_name, "drawable", getPackageName()));
			}
		}
		LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMessageReceiver3, new IntentFilter("server_ovpn"));
		LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMessageReceiver4, new IntentFilter("show_premium_dialog"));
		super.onResume();
	}



	public ArrayList<api_response_model> getAllServers() {

		try {
			Gson gson = new Gson();
			if (main_Activity_SP != null) {
				String json = main_Activity_SP.getString("saved_list_to_cache", null);
				if (json != null) {
					if (!json.isEmpty() || !json.equals("")) {
						return gson.fromJson(json, Model_From_Api.class);
					} else {
						Log.d("Empty11", "empty11");
					}
				} else {
					Log.d("Empty222", "empty222");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}


	public void show_download_upload()
	{
		mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Log.d("Show_download" , "download_upload");
				download_upload();
				mHandler.postDelayed(this, 500);
			}
		},500);
	}

	private void download_upload() {
		long resetdownload = TrafficStats.getTotalRxBytes();
		long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;

		textView_download.setText(rxBytes + " B/s");
		if (rxBytes >= 1024) {
			long rxKb = rxBytes / 1024;
			textView_download.setText(rxKb + " KB/s");
			if (rxKb >= 1024) {
				long rxMB = rxKb / 1024;
				textView_download.setText(rxMB + " MB/s");

				if (rxMB >= 1024) {
					long rxGB = rxMB / 1024;
					textView_download.setText(rxGB + " GB/s");
				}
			}
		}

		mStartRX = resetdownload;
		long resetupload = TrafficStats.getTotalTxBytes();
		long txBytes = TrafficStats.getTotalTxBytes() - mStartTX;
		Log.d("UploadDownload", "" + rxBytes  + "" + txBytes);

		textView_upload.setText(txBytes + " B/s");

		if (txBytes >= 1024) {
			long txKb = txBytes / 1024;
			textView_upload.setText(txKb + " KB/s");
			if (txKb >= 1024) {

				long txMB = txKb / 1024;
				textView_upload.setText(txMB + " MB/s");

				if (txMB >= 1024) {

					long txGB = txMB / 1024;
					textView_upload.setText(txGB + " GB/s");
				}
			}
		}

		mStartTX = resetupload;



	}


	public boolean isNetworkAvailable() {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}



	                                  ///////////////////Native Ad CODE///////////////////



	private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
		MediaView mediaView = adView.findViewById(R.id.ad_media);
		adView.setMediaView(mediaView);
		adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
		adView.setBodyView(adView.findViewById(R.id.ad_body));
		adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
		adView.setIconView(adView.findViewById(R.id.ad_app_icon));
		adView.setPriceView(adView.findViewById(R.id.ad_price));
		adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
		adView.setStoreView(adView.findViewById(R.id.ad_store));
		adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

		((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
		if (nativeAd.getBody() == null) {
			adView.getBodyView().setVisibility(View.INVISIBLE);
		} else {
			adView.getBodyView().setVisibility(View.VISIBLE);
			((TextView) adView.getBodyView()).setText(nativeAd.getBody());
		}

		if (nativeAd.getCallToAction() == null) {
			adView.getCallToActionView().setVisibility(View.INVISIBLE);
		} else {
			adView.getCallToActionView().setVisibility(View.VISIBLE);
			((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
		}

		if (nativeAd.getIcon() == null) {
			adView.getIconView().setVisibility(View.GONE);
		} else {
			((ImageView) adView.getIconView()).setImageDrawable(
					nativeAd.getIcon().getDrawable());
			adView.getIconView().setVisibility(View.VISIBLE);
		}

		if (nativeAd.getPrice() == null) {
			adView.getPriceView().setVisibility(View.INVISIBLE);
		} else {
			adView.getPriceView().setVisibility(View.VISIBLE);
			((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
		}

		if (nativeAd.getStore() == null) {
			adView.getStoreView().setVisibility(View.INVISIBLE);
		} else {
			adView.getStoreView().setVisibility(View.VISIBLE);
			((TextView) adView.getStoreView()).setText(nativeAd.getStore());
		}

		if (nativeAd.getStarRating() == null) {
			adView.getStarRatingView().setVisibility(View.INVISIBLE);
		} else {
			((RatingBar) adView.getStarRatingView())
					.setRating(nativeAd.getStarRating().floatValue());
			adView.getStarRatingView().setVisibility(View.VISIBLE);
		}

		if (nativeAd.getAdvertiser() == null) {
			adView.getAdvertiserView().setVisibility(View.INVISIBLE);
		} else {
			((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
			adView.getAdvertiserView().setVisibility(View.VISIBLE);
		}

		adView.setNativeAd(nativeAd);
		VideoController vc = nativeAd.getVideoController();
		if (vc.hasVideoContent()) {
			vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
				@Override
				public void onVideoEnd() {
					super.onVideoEnd();
				}
			});
		}
	}

	private void refreshAd() {
		AdLoader.Builder builder = new AdLoader.Builder(MainActivity.this, getString(R.string.Admob_native_ad));

		builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
			// OnUnifiedNativeAdLoadedListener implementation.
			@Override
			public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
				if (nativeAd != null) {
					nativeAd.destroy();
				}
				nativeAd = unifiedNativeAd;
				FrameLayout frameLayout = DisconnectDialog.findViewById(R.id.dscnt_ad);
				UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
						.inflate(R.layout.ad_unified, null);
				populateUnifiedNativeAdView(unifiedNativeAd, adView);
				frameLayout.removeAllViews();
				frameLayout.addView(adView);
			}

		});

		VideoOptions videoOptions = new VideoOptions.Builder()
				.setStartMuted(false)
				.build();

		NativeAdOptions adOptions = new NativeAdOptions.Builder()
				.setVideoOptions(videoOptions)
				.build();

		builder.withNativeAdOptions(adOptions);

		AdLoader adLoader = builder.withAdListener(new AdListener() {
			@Override
			public void onAdFailedToLoad(int errorCode) {

			}
		}).build();

		adLoader.loadAd(new AdRequest.Builder().build());


	}

	public void InitiateDiscountDialog(){

		DisconnectDialog = new Dialog(this);
		DisconnectDialog.setContentView(R.layout.disconnect_window);
		Dis_disconnect = (Button) DisconnectDialog.findViewById(R.id.btn_dscnt);
		Dis_Cancle = (Button) DisconnectDialog.findViewById(R.id.btn_cncl);
		Dis_FrameLayout = DisconnectDialog.findViewById(R.id.dscnt_ad);





		Dis_disconnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				runOnUiThread(() -> {
					ProfileManager.setConntectedVpnProfileDisconnected(MainActivity.this);
					if (openvpn_service != null && openvpn_service.getManagement() != null) {
						openvpn_service.getManagement().stopVPN(false);

						if (VpnStatus.getLevel("") != null) {

							PlayButton.setText("Not Connected");
							chronometer2.stop();
							connection_btn.setImageResource(R.drawable.disconnected);
//							connect_animationview.setSpeed((float) 0.2);
//							connected_animationview.setSpeed((float) 0.2);
//							mLottieAnimationView.setVisibility(View.INVISIBLE);
							connection_btn.setVisibility(View.VISIBLE);
//							connect_animationview.setVisibility(View.VISIBLE);
							connecting_animationView.setVisibility(View.INVISIBLE);
							stopHandler_home_screen();
						}
					}
				});

				DisconnectDialog.dismiss();
			}
		});
		Dis_Cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DisconnectDialog.dismiss();
			}
		});

		DisconnectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		DisconnectDialog.setCancelable(false);
	}


}