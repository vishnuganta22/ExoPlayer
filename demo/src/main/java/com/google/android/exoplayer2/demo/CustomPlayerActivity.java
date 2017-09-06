package com.google.android.exoplayer2.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.ArrayList;

import static com.google.android.exoplayer2.demo.PlayerActivity.ACTION_VIEW_LIST;
import static com.google.android.exoplayer2.demo.PlayerActivity.URI_LIST_EXTRA;

/**
 * Created by vishnu on 6/9/17.
 */

public class CustomPlayerActivity extends Activity implements Player.EventListener{
    private SimpleExoPlayerView simpleExoPlayerView, simpleExoPlayerView1;
    private LinearLayout customLayout;
    ArrayList<Uri> urisList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_player_activity);

        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView1 = (SimpleExoPlayerView) findViewById(R.id.player_view1);
        customLayout = (LinearLayout) findViewById(R.id.custom_layout);

        initializeData();
        startPlayback();
    }

    private void startPlayback() {
        int size = urisList.size();
        if(size <= 0) return;
        switch (size){
            case 1:
                simpleExoPlayerView.setVisibility(View.INVISIBLE);
                customLayout.setVisibility(View.VISIBLE);
                playVideoOn(simpleExoPlayerView1);
                break;
            case 2:
                customLayout.setVisibility(View.INVISIBLE);
                simpleExoPlayerView.setVisibility(View.VISIBLE);
                playVideoOn(simpleExoPlayerView);
                break;
        }
    }

    private void playVideoOn(SimpleExoPlayerView simpleExoPlayerView) {
        SimpleExoPlayer player = getNewExoPlayerInstance(this);
        player.setPlayWhenReady(true);
        player.addListener(this);
        setDataSourceForExoPlayer(player);
        simpleExoPlayerView.setPlayer(player);
    }

    private SimpleExoPlayer getNewExoPlayerInstance(Context context) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        return ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    }

    private void setDataSourceForExoPlayer(ExoPlayer player){
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                "DS", bandwidthMeter);

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource videoSource = new ExtractorMediaSource(urisList.get(0),
                dataSourceFactory, extractorsFactory, null, null);

            player.prepare(videoSource);
    }

    private void initializeData() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (ACTION_VIEW_LIST.equals(action)) {
            String[] uriStrings = intent.getStringArrayExtra(URI_LIST_EXTRA);
            Uri[] uris = new Uri[uriStrings.length];
            for (int i = 0; i < uriStrings.length; i++) {
                uris[i] = Uri.parse(uriStrings[i]);
                urisList.add(uris[i]);
            }
        } else {
            showToast(getString(R.string.unexpected_intent_action, action));
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        releasePlayers();
    }

    private void releasePlayers() {
        releaseOnView(simpleExoPlayerView);
        releaseOnView(simpleExoPlayerView1);
    }

    private void releaseOnView(@NonNull SimpleExoPlayerView simpleExoPlayerView){
        ExoPlayer player = simpleExoPlayerView.getPlayer();
        if(player != null){
            player.stop();
            player.release();
        }
        simpleExoPlayerView.setPlayer(null);
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if(playbackState == Player.STATE_ENDED){
            releasePlayers();
            urisList.remove(0);
            startPlayback();
        }
    }

    @Override
    public void onRepeatModeChanged(@Player.RepeatMode int repeatMode) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}
