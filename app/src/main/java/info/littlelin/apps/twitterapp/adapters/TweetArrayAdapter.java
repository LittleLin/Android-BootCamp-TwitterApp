package info.littlelin.apps.twitterapp.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.littlelin.apps.twitterapp.R;
import info.littlelin.apps.twitterapp.models.Tweet;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvScreenName;
    private TextView tvBody;
    private TextView tvTimestamp;


    public TweetArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for position
        Tweet tweet = this.getItem(position);

        // Find or inflate  the template
        View view;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.tweet_item, parent, false);
        } else {
            view = convertView;
        }

        // Find the views within template
        this.setViews(view);

        // Populate views with tweet data
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.placeholder).build();
        imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImage, options);
        this.tvUserName.setText(tweet.getUser().getName());
        this.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        this.tvBody.setText(tweet.getBody());
        this.addLinkToTweetBody();
        this.tvTimestamp.setText(this.getRelativeTimestamp(tweet.getCreatedAt()));

        return view;
    }

    private void addLinkToTweetBody() {
        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return match.group();
            }
        };

        Pattern mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)");
        String mentionScheme = "http://www.twitter.com/";
        Linkify.addLinks(this.tvBody, mentionPattern, mentionScheme, null, filter);

        Pattern hashtagPattern = Pattern.compile("#([A-Za-z0-9_-]+)");
        String hashtagScheme = "http://www.twitter.com/search/";
        Linkify.addLinks(this.tvBody, hashtagPattern, hashtagScheme, null, filter);

        Pattern urlPattern = Patterns.WEB_URL;
        Linkify.addLinks(this.tvBody, urlPattern, null, null, filter);
    }

    private String getRelativeTimestamp(String createdAt) {
        Date date = new Date(createdAt);
        String relativeTimestamp = DateUtils.getRelativeTimeSpanString(date.getTime(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        relativeTimestamp = relativeTimestamp.replace(" ago", "");
        relativeTimestamp = relativeTimestamp.replace("以前", "");

        return relativeTimestamp;
    }

    private void setViews(View view) {
        this.ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        this.tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        this.tvScreenName = (TextView) view.findViewById(R.id.tvAccountScreenName);
        this.tvBody = (TextView) view.findViewById(R.id.tvBody);
        this.tvTimestamp = (TextView) view.findViewById(R.id.tvTimestamp);
    }
}
