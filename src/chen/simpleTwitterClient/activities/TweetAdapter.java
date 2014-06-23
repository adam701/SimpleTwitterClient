package chen.simpleTwitterClient.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import chen.simpleTwitterClient.models.Tweet;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetAdapter extends ArrayAdapter<Tweet> {

	public TweetAdapter(Context context, List<Tweet> tweets) {
		super(context, R.layout.tweet_layout, tweets);
	}

	// Translate method. Get data and convert it to view.
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		Tweet tweet = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.tweet_layout, parent, false);
		}
		// Lookup view for data population
		TextView tvScreenName = (TextView) convertView
				.findViewById(R.id.tvScreenName);
		TextView tvCreateTime = (TextView) convertView
				.findViewById(R.id.tvCreateTime);
		TextView tvUserid = (TextView) convertView.findViewById(R.id.tvUserid);
		TextView tvText = (TextView) convertView.findViewById(R.id.tvText);
		ImageView ivProfile = (ImageView) convertView
				.findViewById(R.id.ivProfile);

		tvUserid.setText("@" + tweet.getUser().getScreenName());
		tvText.setText(tweet.getBody());
		tvScreenName.setText(tweet.getUser().getName());
		ImageLoader.getInstance().displayImage(
				tweet.getUser().getImageProfileUrl(), ivProfile);
		tvCreateTime.setText(getRelativeTimeAgo(tweet.getCreateAt()));
		// int height_in_pixels = tvText.getLineCount() *
		// tvText.getLineHeight(); //approx height text
		// Log.d("DEBUG", "Line is " + tvText.getLineCount());

		// tvText.setHeight(height_in_pixels);

		// tvCreateTime.setText(tweet.getCreateAt());

		// Return the completed view to render on screen
		return convertView;
	}

	private String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat,
				Locale.ENGLISH);
		sf.setLenient(true);

		String relativeDate = "";
		try {
			Log.d("DEBUG", "Parse " + rawJsonDate);
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return relativeDate;
	}
	
}
