package info.littlelin.apps.twitterapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;

import java.util.ArrayList;

import info.littlelin.apps.twitterapp.R;
import info.littlelin.apps.twitterapp.TwitterApplication;
import info.littlelin.apps.twitterapp.TwitterClient;
import info.littlelin.apps.twitterapp.adapters.TweetArrayAdapter;
import info.littlelin.apps.twitterapp.fragments.ComposeDialog;
import info.littlelin.apps.twitterapp.models.Tweet;

public class TimelineActivity extends Activity {
    public static final int PAGE_SIZE = 5;

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private ArrayAdapter<Tweet> aTweets;
    private ListView lvTweets;

    private Long currentMaxId = 1l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        this.setViews();
        this.client = TwitterApplication.getRestClient();

        this.tweets = new ArrayList<Tweet>();
        this.aTweets = new TweetArrayAdapter(this, tweets);
        this.lvTweets.setAdapter(this.aTweets);

        this.lvTweets.setOnScrollListener(new EndlessScrollListener(PAGE_SIZE - 1, 0) {
            private long lastScrollingMaxId1;

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (page > 8) {
                    return;
                }

                this.loadMoreTweets(page);
            }

            private void loadMoreTweets(int page) {
                populateTime(currentMaxId - 1);
            }
        });

        this.populateTime(0);
    }

    private void setViews() {
        this.lvTweets = (ListView) this.findViewById(R.id.lvTweets);

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            android.app.FragmentManager fm = getFragmentManager();
            ComposeDialog composeDialog = ComposeDialog.newInstance("New tweet");
            composeDialog.show(fm, "fragment_compose");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateTime(long maxId) {
        this.client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray json) {
                ArrayList<Tweet> freshTweets = Tweet.fromJson(json);
                currentMaxId = freshTweets.get(freshTweets.size() - 1).getId();
                aTweets.addAll(freshTweets);
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Log.d("debug", e.toString());
                Log.d("debug", s);
            }
        }, maxId);
    }
}
