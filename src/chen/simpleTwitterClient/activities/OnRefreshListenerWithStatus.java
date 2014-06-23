package chen.simpleTwitterClient.activities;

import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import chen.simpleTwitterClient.models.Tweet;

import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class OnRefreshListenerWithStatus implements OnRefreshListener {

	private long since;
	private TweetAdapter tweetAdapter;
	private PullToRefreshListView timeLinesListView;
	private AtomicBoolean loading = new AtomicBoolean(false);

	public OnRefreshListenerWithStatus(final long since,
			final TweetAdapter tweetAdapter,
			final PullToRefreshListView timeLinesListView) {
		this.since = since;
		this.tweetAdapter = tweetAdapter;
		this.timeLinesListView = timeLinesListView;
	}

	@Override
	public void onRefresh() {
		Log.d("DEBUG", "Since is " + since);
		loaddata();
	}
	
	public void loaddata(){
		if(loading.getAndSet(true)){
			return;
		}
		TwitterClientApp.getRestClient().getHomeTimeLine(since, -1, 12,
				new JsonHttpResponseHandler() {
					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO
						// Auto-generated
						// method stub
						Log.d("DEBUG", "Json Handler Error");
						Log.d("DEBUG", arg0.toString());
						Log.d("DEBUG", arg1);
						loading.set(false);
						timeLinesListView.onRefreshComplete();
					}

					@Override
					protected void handleFailureMessage(Throwable arg0,
							String arg1) {
						Log.d("DEBUG", "Fail to POST");
						Log.d("DEBUG", arg0.toString());
						Log.d("DEBUG", arg1);
						loading.set(false);
						timeLinesListView.onRefreshComplete();
					}
					
					@Override
					public void onSuccess(JSONArray jsonArray) {
						Log.d("DEBUG", jsonArray.toString());
						// tweetsAdapter.addAll(Tweet.fromJSONArray(jsonArray));
						for (int i = jsonArray.length() - 1; i >= 0; i--) {
							JSONObject jsonObject;
							try {
								jsonObject = jsonArray.getJSONObject(i);
								Log.d("DEBUG", jsonObject.toString());
								Tweet tweet = Tweet.fromJSON(jsonObject);
								Log.d("DEBUG",
										"Final tweet is " + tweet.toString());
								tweetAdapter.insert(tweet, 0);
							} catch (JSONException e) {
								Log.d("DEBUG", "Fail to get json Object!");
								e.printStackTrace();
							}
						}
						since = tweetAdapter.getItem(0).getUid();
						loading.set(false);
						timeLinesListView.onRefreshComplete();	
					}
				});
	}

}
