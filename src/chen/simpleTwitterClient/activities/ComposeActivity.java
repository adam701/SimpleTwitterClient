package chen.simpleTwitterClient.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import chen.simpleTwitterClient.models.Tweet;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeActivity extends Activity {

	private EditText edCompose;
	MenuItem vWordLeft;
	private final int MAX_WORDS_NUM = 140;
	private ImageView ivProfie;
	private TextView tvScreenName;
	private TextView tvUserName;
	private static String imageProfileString;
	private static String screenNameString;
	private static String useridString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tweet, menu);
		this.vWordLeft = menu.findItem(R.id.tvWordsLeft);
		return true;
	}

	public void init() {
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(
				Color.parseColor("#33CCFF"));
		ab.setBackgroundDrawable(colorDrawable);
		this.edCompose = (EditText) findViewById(R.id.etCompose);
		this.ivProfie = (ImageView) findViewById(R.id.ivProfile1);
		this.tvScreenName = (TextView) findViewById(R.id.tvScreenName1);
		this.tvUserName = (TextView) findViewById(R.id.tvUserid1);
		getPersonalInfo();
		edCompose.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				vWordLeft.setTitle(String.valueOf(MAX_WORDS_NUM - s.length()));
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void getPersonalInfo() {
		if (imageProfileString == null || screenNameString == null
				|| useridString == null) {
			TwitterClientApp.getRestClient().getCurrentUserInfo(
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
							Log.d("DEBUG", "Json Handler Error");
							Log.d("DEBUG", arg0.toString());
							Log.d("DEBUG", arg1);
						}
						
						@Override
						public void onSuccess(JSONObject jsonObj) {
							Log.d("DEBUG", jsonObj.toString());
							/*
							 * Toast.makeText(getApplicationContext(),
							 * jsonObj.toString(), Toast.LENGTH_SHORT) .show();
							 */
							// tweetsAdapter.addAll(Tweet.fromJSONArray(jsonArray));
							try {
								imageProfileString = jsonObj
										.getString("profile_image_url");
								screenNameString = "@"
										+ jsonObj.getString("screen_name");
								useridString = jsonObj.getString("name");
								ImageLoader.getInstance().displayImage(
										imageProfileString, ivProfie);
								tvScreenName.setText(screenNameString);
								tvUserName.setText(useridString);
							} catch (JSONException e) {
								e.printStackTrace();
								Log.d("DEBUG",
										"Fail to parsejson"
												+ jsonObj.toString());
							}
						}
					});
		} else{
			ImageLoader.getInstance().displayImage(
					imageProfileString, ivProfie);
			tvScreenName.setText(screenNameString);
			tvUserName.setText(useridString);
		}
	}

	public void onTweet(MenuItem v) {
		Intent intent = new Intent();
		Log.d("DEBUG", this.edCompose.getText().toString());
		intent.putExtra("body", this.edCompose.getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}

}
