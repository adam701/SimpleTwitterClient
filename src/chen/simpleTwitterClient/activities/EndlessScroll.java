package chen.simpleTwitterClient.activities;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import eu.erikw.PullToRefreshListView.OnRefreshListener;
import android.R.integer;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class EndlessScroll implements OnScrollListener {

	private int visibleThreshold = 6;
	private boolean loading = false;
	private long since;
	private long max;
	private final long COUNT = 12;
	private AtomicBoolean isInitialized = new AtomicBoolean(false);
	private TweetAdapter tweetsAdapter;
	
	public EndlessScroll(long since, long max) {
		this.since = since;
		this.max = max;
		isInitialized.set(true);
	}
	
	public EndlessScroll(final TweetAdapter tweetsAdapter){
		this.tweetsAdapter = tweetsAdapter;
	}
	
	public EndlessScroll(){
	}

	public EndlessScroll(int visibleThreshold) {
		this.visibleThreshold = visibleThreshold;
	}

	// This happens many times a second during a scroll, so be wary of the code
	// you place here.
	// We are given a few useful parameters to help us work out if we need to
	// load some more data,
	// but first we check if we are waiting for the previous load to finish.
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		// If the total item count is zero and the previous isn't, assume the
		// list is invalidated and should be reset back to initial state
		if(!isInitialized.get()){
			return;
		}
		if(tweetsAdapter.getCount() <= 0){
			if(loading){
				return;
			}else{
				loading = true;
				isInitialized.set(false);
				resetAndLoad();
				return;
			}
		}
		long currentMaxId = tweetsAdapter.getItem(tweetsAdapter.getCount() - 1).getUid();
		Log.d("DEBUG", "Current Max id is" + currentMaxId);
		Log.d("DEBUG", "Previous Max id is" + max);
		if (currentMaxId > max) {
			loading = true;
			isInitialized.set(false);
			tweetsAdapter.clear();
			resetAndLoad();
			return;
		}
		if (loading && (currentMaxId < max)) {
			loading = false;
			max = currentMaxId;
		}
		Log.d("DEBUG", "Laoding status is " + loading);
		if (!loading
				&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
			Log.d("DEBUG", "Begin to load ");
			appendMore(max, this.COUNT);
			loading = true;
		}
	}

	// Defines the process for actually loading more data based on page
	public abstract void appendMore(long max, long count);
	
	public abstract void resetAndLoad();

	public void setLoadingStatus(boolean status){
		this.loading = status;
	}
	
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// Don't take any action on changed
	}
	
	public void resetSinceAndMax(long since, long max){
		this.since = since;
		this.max = max;
		isInitialized.set(true);
	}

	public boolean isLoading(){
		return loading;
	}
	
	public void setInitializedStatus(boolean status) {
		this.isInitialized.set(status);
	}

	
}