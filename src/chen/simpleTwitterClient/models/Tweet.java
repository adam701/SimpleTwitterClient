package chen.simpleTwitterClient.models;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import android.util.Log;

@Table(name = "Tweets")
public class Tweet extends Model{
	
	@Column(name = "body")
	private String body;
	
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
	
	@Column(name = "createAt")
	private String createAt;
	
	@Column(name = "user")
	private User user;
	
	public Tweet(){
		super();
	}
	
	public static Tweet fromJSON(JSONObject jsonObject) {
		try {
			Tweet tweet = new Tweet();
			tweet.body = jsonObject.getString("text");
			Log.d("DEBUG", "Text Body is " + tweet.body);
			tweet.uid = jsonObject.getLong("id");
			Log.d("DEBUG", "id is " + tweet.uid);
			tweet.createAt = jsonObject.getString("created_at");
			Log.d("DEBUG", "Created is " + tweet.createAt);
			tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
			tweet.save();
			return tweet;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("DEBUG", "Fail to convert one json object to Tweet : " + jsonObject.toString());
			return null;
		}
	}

	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreateAt() {
		return createAt;
	}

	public User getUser() {
		return user;
	}
	
	@Override
	public String toString() {
		return "Tweet [body=" + body + ", id=" + uid + ", createAt=" + createAt
				+ ", user=" + user + "]";
	}

	public static List<Tweet> fromJSONArray(JSONArray jsonArray) {
		List<Tweet> list = new ArrayList<Tweet>(jsonArray.length());
		
		for(int i = 0; i < jsonArray.length(); i++){
			try {
				list.add(Tweet.fromJSON(jsonArray.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("DEBUG", "Fail to get JSON Object");
			}
		}
		return list;
	}
	
}
