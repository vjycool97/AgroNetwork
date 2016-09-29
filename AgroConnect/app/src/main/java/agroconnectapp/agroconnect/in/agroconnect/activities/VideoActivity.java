package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

/**
 * Created by sumanta on 22/6/16.
 */
public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    // YouTube player view
    private YouTubePlayerView youTubeView;
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        String videoURI = getIntent().getExtras().getString("videoID", "");
        videoId = videoURI.replace("https://youtu.be/", "");
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        // Initializing video player with developer key
        youTubeView.initialize(KeyIDS.DEVELOPER_KEY, this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = getResources().getString(R.string.error_player);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.loadVideo(videoId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(KeyIDS.DEVELOPER_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

}