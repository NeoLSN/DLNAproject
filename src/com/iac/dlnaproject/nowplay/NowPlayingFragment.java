
package com.iac.dlnaproject.nowplay;

import com.iac.dlnaproject.ControllerProxy;
import com.iac.dlnaproject.R;
import com.iac.dlnaproject.fragment.BaseFragment;
import com.iac.dlnaproject.nowplay.Player.ChangeListener;
import com.iac.dlnaproject.util.XmlParser;

import org.cybergarage.upnp.std.av.renderer.AVTransport;
import org.cybergarage.xml.Node;
import org.cybergarage.xml.ParserException;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class NowPlayingFragment extends BaseFragment implements OnClickListener,
OnSeekBarChangeListener, ChangeListener {

    private TextView artist;
    private TextView musicTitle;
    private ImageView albumCover;
    private TextView progressTime;
    private SeekBar progressBar;
    private TextView durationTime;
    private ImageButton prevButton;
    private ImageButton playButton;
    private ImageButton stopButton;
    private ImageButton nextButton;
    private ImageButton volume;
    private SeekBar volumeBar;

    private ControllerProxy mCtrlProxy;
    private Player mPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now_playing, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup root = (ViewGroup)getView();

        artist = (TextView)root.findViewById(R.id.artist_name);
        musicTitle = (TextView)root.findViewById(R.id.music_name);
        albumCover = (ImageView)root.findViewById(R.id.album_cover);
        progressTime = (TextView)root.findViewById(R.id.progress_time);
        progressBar = (SeekBar)root.findViewById(android.R.id.progress);
        durationTime = (TextView)root.findViewById(R.id.duration_time);
        prevButton = (ImageButton)root.findViewById(R.id.audio_prev);
        playButton = (ImageButton)root.findViewById(R.id.audio_play);
        stopButton = (ImageButton)root.findViewById(R.id.audio_stop);
        nextButton = (ImageButton)root.findViewById(R.id.audio_next);
        volume = (ImageButton)root.findViewById(R.id.volume);
        volumeBar = (SeekBar)root.findViewById(R.id.volume_bar);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        volume.setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(this);
        volumeBar.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCtrlProxy = ControllerProxy.getInstance();
        mPlayer = mCtrlProxy.getPreferedPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayer.removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayer.addListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.volume_bar:
                if (mPlayer!= null)
                    mPlayer.setVolume(seekBar.getProgress());
                break;
            case android.R.id.progress:
                if (mPlayer!= null)
                    mPlayer.setPosition(seekBar.getProgress());
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.audio_prev:
                if (mPlayer!= null)
                    mPlayer.prev();
                break;
            case R.id.audio_play:
                if (mPlayer!= null)
                    mPlayer.toggleState();
                break;
            case R.id.audio_stop:
                if (mPlayer!= null)
                    mPlayer.stop();
                break;
            case R.id.audio_next:
                if (mPlayer!= null)
                    mPlayer.next();
                break;
            case R.id.volume:
                if (mPlayer!= null)
                    mPlayer.toggleMute();
                break;
        }

    }

    @Override
    public void playerChanged(Player player) {
        updateNowPlaying(player);
    }

    private void updateNowPlaying(final Player player) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                PlayerInfo playerInfo = player.getPlayerInfo();
                String currentMediaMetadata = playerInfo.getProperties().get(
                        AVTransport.CURRENTURIMETADATA);
                XmlParser p = new XmlParser();
                Node node;
                try {
                    node = p.parse(currentMediaMetadata);
                    if (node != null && node.getName().equals("item")) {
                        MediaItem item = new MediaItem(node);
                        musicTitle.setText(item.getTitle());
                        artist.setText(item.getArtist());
                    }
                } catch (ParserException e) {
                    e.printStackTrace();
                }
                progressTime.setText(player.getPosition());
                progressBar.setMax(player.getDurationSeconds());
                progressBar.setProgress(player.getPositionSeconds());
                durationTime.setText(player.getDuration());
                if (player.isMute()) {
                    volume.setImageResource(R.drawable.ic_action_volume_muted);
                } else {
                    volume.setImageResource(R.drawable.ic_action_volume_on);
                }
                volumeBar.setMax(player.getVolumeMax());
                volumeBar.setKeyProgressIncrement(player.getVolumeStep());
                volumeBar.setProgress(player.getVolume());
                if (player.isPlaying()) {
                    playButton.setImageResource(R.drawable.icon_play_pause);
                } else {
                    playButton.setImageResource(R.drawable.icon_play);
                }
            }
        };

        getActivity().runOnUiThread(r);
    }

}
