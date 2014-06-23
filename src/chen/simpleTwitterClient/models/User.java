package chen.simpleTwitterClient.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.UserDataHandler;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import android.util.Log;

@Table(name = "Users")
public class User extends Model{

	@Column(name = "name")
	private String name;
	
	@Column(name = "uid")
	private long uid;
	
	@Column(name = "screenName")
	private String screenName;
	
	@Column(name = "imageProfileUrl")
	private String imageProfileUrl;
	
	public User(){
		super();
	}
	
	public static User fromJSON(JSONObject jsonObject){
		try {
			User user = new User();
			user.name = jsonObject.getString("name");
			Log.d("DEBUG", "User name is " + user.name);
			user.uid = jsonObject.getLong("id");
			Log.d("DEBUG", "User uid is " + user.uid);
			user.screenName = jsonObject.getString("screen_name");
			Log.d("DEBUG", "User screen name is " + user.screenName);
			user.imageProfileUrl = jsonObject.getString("profile_image_url");
			Log.d("DEBUG", "User imageProfileUrl is " + user.imageProfileUrl);
			user.save();
			return user;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("DEBUG", "Fail to convert one json object to User : " + jsonObject.toString());
			return null;
		}
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getImageProfileUrl() {
		return imageProfileUrl;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", uid=" + uid + ", screenName="
				+ screenName + ", imageProfileUrl=" + imageProfileUrl + "]";
	}
	
}
