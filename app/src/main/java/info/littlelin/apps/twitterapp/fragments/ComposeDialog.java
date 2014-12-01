package info.littlelin.apps.twitterapp.fragments;

import android.app.DialogFragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import info.littlelin.apps.twitterapp.R;
import info.littlelin.apps.twitterapp.TwitterApplication;
import info.littlelin.apps.twitterapp.TwitterClient;
import info.littlelin.apps.twitterapp.activities.LoginActivity;

public class ComposeDialog extends DialogFragment {
    private TextView tvAccountName;
    private TextView tvAccountScreenName;
    private EditText etBody;
    private ImageView ivAccountProfileImage;
    private Button btnNewTweet;

    public ComposeDialog() {
        // Empty constructor required for DialogFragment
    }

    public static ComposeDialog newInstance(String title) {
        ComposeDialog frag = new ComposeDialog();
        Bundle args = new Bundle();

        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container, true);
        String title = getArguments().getString("title", "New tweet");
        getDialog().setTitle(title);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        this.setupViews();
        super.onActivityCreated(savedInstanceState);
    }

    private void setupViews() {
        this.tvAccountScreenName = (TextView) this.getView().findViewById(R.id.tvAccountScreenName);
        this.tvAccountScreenName.setText("@" + LoginActivity.AccountScreenName);

        this.ivAccountProfileImage = (ImageView) this.getView().findViewById(R.id.ivAccountProfileImage);
        this.tvAccountName = (TextView) this.getView().findViewById(R.id.tvAccountName);

        this.btnNewTweet = (Button) this.getView().findViewById(R.id.btnNewTweet);
        this.etBody = (EditText) this.getView().findViewById(R.id.etBody);

        this.btnNewTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTweet(v);
            }
        });
        if (LoginActivity.AccountName == null || "".equals(LoginActivity.AccountName)) {
            TwitterClient client = TwitterApplication.getRestClient();
            client.getAccountInformation(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject json) {
                    try {
                        LoginActivity.AccountName = json.getString("name");
                        LoginActivity.AccountProfileImageUrl = json.getString("profile_image_url");

                        tvAccountName.setText(LoginActivity.AccountName);

                        ImageLoader imageLoader = ImageLoader.getInstance();
                        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.placeholder).build();
                        imageLoader.displayImage(LoginActivity.AccountProfileImageUrl, ivAccountProfileImage, options);
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
        } else {
            tvAccountName.setText(LoginActivity.AccountName);
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.placeholder).build();
            imageLoader.displayImage(LoginActivity.AccountProfileImageUrl, ivAccountProfileImage, options);
        }
    }

    public void postTweet(View view) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.postTweet(this.etBody.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                try {
                    long id = json.getLong("id");

                    Log.d("debug", "new id: " + String.valueOf(id));
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

        // Finish current dialog
        this.dismiss();
    }
}
