package info.littlelin.apps.twitterapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.oauth.OAuthLoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import info.littlelin.apps.twitterapp.R;
import info.littlelin.apps.twitterapp.TwitterApplication;
import info.littlelin.apps.twitterapp.TwitterClient;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {
    public static String AccountName = "";
    public static String AccountScreenName = "";
    public static String AccountProfileImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        this.getAccountInformation();

        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
    }

    private void getAccountInformation() {
        TwitterClient client = TwitterApplication.getRestClient();
        client.getAccountScreenName(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                try {
                    LoginActivity.AccountScreenName = json.getString("screen_name");

                    TwitterClient client = TwitterApplication.getRestClient();
                    client.getAccountInformation(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(JSONObject json) {
                            try {
                                LoginActivity.AccountName = json.getString("name");
                                LoginActivity.AccountProfileImageUrl = json.getString("profile_image_url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("debug", e.toString());
                            }
                        }

                        @Override
                        public void onFailure(Throwable e, String s) {
                            Log.d("debug", e.toString());
                            Log.d("debug", s);
                        }
                    }, LoginActivity.AccountScreenName);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("debug", e.toString());
                }
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("debug", e.toString());
                Log.d("debug", s);
            }
        });
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
        getClient().connect();
    }
}
