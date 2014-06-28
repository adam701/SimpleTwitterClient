package chen.simpleTwitterClient.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import chen.simpleTwitterClient.models.Tweet;
import chen.simpleTwitterClient.models.User;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import eu.erikw.PullToRefreshListView;

public class TimeLineActivity extends Activity {
	private ArrayList<Tweet> tweets;
	private TweetAdapter tweetsAdapter;
	private PullToRefreshListView timeLinesListView;
	private final int REQUEST_CODE = 200;
	private OnRefreshListenerWithStatus onRefreshListenerWithStatus;
	private EndlessScroll onScrollListener;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("DEBUG", "Created!");
		setContentView(R.layout.activity_time_line);
		
		this.timeLinesListView = (PullToRefreshListView) findViewById(R.id.lvTimeLine);
		this.tweets = new ArrayList<Tweet>();
		this.tweetsAdapter = new TweetAdapter(this, this.tweets);
		this.timeLinesListView.setAdapter(tweetsAdapter);
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#33CCFF"));
		ab.setBackgroundDrawable(colorDrawable);
		
		if(isNetworkAvailable()){
			Toast.makeText(getApplicationContext(), "OnLine", Toast.LENGTH_SHORT).show();
			Log.d("DEBUG", "Online");
			init();
		}else{
			Toast.makeText(getApplicationContext(), "OffLine", Toast.LENGTH_SHORT).show();
			Log.d("DEBUG", "Offline");
			for ( Tweet tweet : Tweet.recentTweets()){
				Toast.makeText(getApplicationContext(), tweet.toString(), Toast.LENGTH_SHORT).show();
				Log.d("DEBUG", tweet.toString());
				tweetsAdapter.add(tweet);
			}	
		}
		
		
	}

	public void appendTimeLines(long max, long count) {
		Log.d("DEBUG", "Begin to append load");
		Log.d("DEBUG", "Adapter size is " + tweetsAdapter.getCount());
		TwitterClientApp.getRestClient().getHomeTimeLine(-1, max, count,
				new JsonHttpResponseHandler() {
					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.d("DEBUG", "Json Handler Error");
						Log.d("DEBUG", arg0.toString());
						Log.d("DEBUG", arg1);
					}

					@Override
					protected void handleFailureMessage(Throwable arg0,
							String arg1) {
						Log.d("DEBUG", "Fail to load");
						Log.d("DEBUG", arg0.toString());
						Log.d("DEBUG", arg1);
						onScrollListener.setLoadingStatus(false);
					}
					
					@Override
					public void onSuccess(JSONArray jsonArray) {
						Log.d("DEBUG", jsonArray.toString());
						/*Toast.makeText(getApplicationContext(),
								jsonArray.toString(), Toast.LENGTH_SHORT)
								.show();*/
						// tweetsAdapter.addAll(Tweet.fromJSONArray(jsonArray));
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject;
							try {
								jsonObject = jsonArray.getJSONObject(i);
								Log.d("DEBUG", jsonObject.toString());
								Tweet tweet = Tweet.fromJSON(jsonObject);
								Log.d("DEBUG",
										"Final tweet is " + tweet.toString());
								tweetsAdapter.add(tweet);
							} catch (JSONException e) {
								Log.d("DEBUG", "Fail to get json Object!");
								e.printStackTrace();
							}
						}
					}
				});
	}

	public void loadFirstTimeLines(long count) {
		/*Toast.makeText(getApplicationContext(), "Begin to load",
				Toast.LENGTH_SHORT).show();*/
		Log.d("DEBUG", "Begin to first load");
		Log.d("DEBUG", "Adapter size is " + tweetsAdapter.getCount());
		TwitterClientApp.getRestClient().getHomeTimeLine(-1, -1, count,
				new JsonHttpResponseHandler() {
					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.d("DEBUG", "Json Handler Error");
						Log.d("DEBUG", arg0.toString());
						Log.d("DEBUG", arg1);
					}
					
					@Override
					protected void handleFailureMessage(Throwable arg0,
							String arg1) {
						Log.d("DEBUG", "Fail to load");
						Log.d("DEBUG", arg0.toString());
						Log.d("DEBUG", arg1);
						onScrollListener.setLoadingStatus(false);
						onScrollListener.setInitializedStatus(true);
					}
					
					@Override
					public void onSuccess(JSONArray jsonArray) {
						long sinceid;
						long max;
						Log.d("DEBUG", jsonArray.toString());
						/*Toast.makeText(getApplicationContext(),
								jsonArray.toString(), Toast.LENGTH_SHORT)
								.show();*/
						// tweetsAdapter.addAll(Tweet.fromJSONArray(jsonArray));
						tweetsAdapter.clear();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject;
							try {
								jsonObject = jsonArray.getJSONObject(i);
								Log.d("DEBUG", jsonObject.toString());
								Tweet tweet = Tweet.fromJSON(jsonObject);
								Log.d("DEBUG",
										"Final tweet is " + tweet.toString());
								tweetsAdapter.add(tweet);
							} catch (JSONException e) {
								Log.d("DEBUG", "Fail to get json Object!");
								e.printStackTrace();
							}
						}
						onScrollListener.resetSinceAndMax(
								tweets.get(0).getUid(),
								tweets.get(tweets.size() - 1).getUid());
						onRefreshListenerWithStatus = new OnRefreshListenerWithStatus(
								tweets.get(0).getUid(), tweetsAdapter,
								timeLinesListView);
						timeLinesListView
								.setOnRefreshListener(onRefreshListenerWithStatus);
						timeLinesListView.setOnScrollListener(onScrollListener);
					}

				});
	}

	public void init() {
		onScrollListener = new EndlessScroll(tweetsAdapter) {

			@Override
			public void appendMore(long max, long count) {
				appendTimeLines(max - 1, count);
			}

			@Override
			public void resetAndLoad() {
				loadFirstTimeLines(12);
			}
		};

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				TimeLineActivity.this)
				.memoryCacheExtraOptions(480, 800)
				// default = device screen dimensions
				.threadPoolSize(3)
				// default
				.threadPriority(Thread.NORM_PRIORITY - 1)
				// default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.build();
		ImageLoader.getInstance().init(config);
		
		//tweetsAdapter.addAll(loadDB());
		loadFirstTimeLines(12);
		if(isOnline()){
			Log.d("DEBUG", "Network is good!");
			
		} else{
			Log.d("DEBUG", "Network is bad!");
			//tweetsAdapter.addAll(loadDB());
		}

	}

	public void onCompose(MenuItem v) {
		Intent intent = new Intent(this, ComposeActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
	}

	public List<Tweet> loadDB(){
		List<Tweet> list = new Select().from(Tweet.class).orderBy("uid DESC").execute();
		for(Tweet tweet : list){
			Toast.makeText(getApplicationContext(),tweet.toString(), Toast.LENGTH_SHORT).show();
			Log.d("DEBUG", "Previous Tweet is " + tweet);
		}
		//new Delete().from(Tweet.class).execute();
		//new Delete().from(User.class).execute();
		return list;
	}
	
	public Boolean isOnline() {
	    try {
	        Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
	        int returnVal = p1.waitFor();
	        boolean reachable = (returnVal==0);
	        return reachable;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	private Boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE) {
				String bodyString = data.getStringExtra("body");
				Log.d("DEBUG", "Body is " + bodyString);
				/*Toast.makeText(this, "Return body is " + bodyString,
						Toast.LENGTH_SHORT).show();*/
				TwitterClientApp.getRestClient().tweet(bodyString,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(JSONObject jsonObject) {
								Toast.makeText(getApplicationContext(),
										"Tweet is posted successfully ",
										Toast.LENGTH_SHORT).show();
								//if(onRefreshListenerWithStatus == null){
									//Log.d("DEBUG", "onRefreshListenerWithStatus is null");
								//}
								onRefreshListenerWithStatus.onRefresh();
							}

							@Override
							public void onFailure(Throwable arg0, String arg1) {
								// TODO Auto-generated method stub
								Log.d("DEBUG", "Json Handler Error");
								Log.d("DEBUG", arg0.toString());
								Log.d("DEBUG", arg1);
							}
							
							@Override
							protected void handleFailureMessage(Throwable arg0,
									String arg1) {
								Log.d("DEBUG", "Fail to POST");
								Log.d("DEBUG", arg0.toString());
								Log.d("DEBUG", arg1);
							}

						});
			}
		}
	}

}
