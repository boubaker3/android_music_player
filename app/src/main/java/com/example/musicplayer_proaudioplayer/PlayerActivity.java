package com.example.musicplayer_proaudioplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.example.musicplayer_proaudioplayer.AlbumDetailsAdapter.albumFiles;
import static com.example.musicplayer_proaudioplayer.MainActivity.musicFiles;
import static com.example.musicplayer_proaudioplayer.MainActivity.repeatBoolean;
import static com.example.musicplayer_proaudioplayer.MainActivity.shuffleBoolean;

public class PlayerActivity extends AppCompatActivity implements
ActionPlaying, ServiceConnection {
ImageView song_cover,nextBtn,prevBtn,repeatBtn,shuffleBtn;
TextView durationPlayed,duration_Total,songName,songArtist,playing;
FloatingActionButton play_pauseBtn;
SeekBar seekBar;
int position=-1;
static ArrayList<MusicFiles> listSongs=new ArrayList<>();
static Uri uri;
//static MediaPlayer mediaPlayer;
private Handler handler=new Handler();
Thread playThread,nextThread,prevThread;
MusicService musicService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
      getIntentMethod();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              if (musicService!=null &&fromUser){
                  musicService.seekTo(progress*1000);
              }
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {

          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {

          }
      });
      PlayerActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
              if (musicService!=null){
                      int mCurrentPosition=musicService.getCurrentPosition()/1000;
                      seekBar.setProgress(mCurrentPosition);
                      durationPlayed.setText(formattedTime(mCurrentPosition));



              }
              handler.postDelayed(this,1000);
          }


      });

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean){
                    shuffleBoolean=false;
                    shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_24);
                }
                else{
                    shuffleBoolean=true;
                    shuffleBtn.setImageResource(R.drawable.shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatBoolean){
                    repeatBoolean=false;
                    repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_24);
                }
                else {
                    repeatBoolean=true;
                    repeatBtn.setImageResource(R.drawable.repeat_on);


                }

            }
        });
       
    }

    @Override
    protected void onResume() {

        Intent intent=new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        playBtnThread();
        nextBtnThread();
        prevBtnThread();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    unbindService(this);
    }

    private void prevBtnThread() {
        prevThread=new Thread(){
            @Override
            public void run() {
                super.run();
          prevBtn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  prevBtnOnClicked();
              }
          });
            }
        };
    prevThread.start();
    }

    public void prevBtnOnClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            if (shuffleBoolean &&!repeatBoolean){
                position=getRandom(listSongs.size()-1);

            }
            else if (!repeatBoolean&&!shuffleBoolean){
                position = ((position - 1)<0?  (listSongs.size()-1):(position-1));

            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            songArtist.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService!=null){
                        try{
                            int mCurrentPosition=musicService.getCurrentPosition()/1000;
                            seekBar.setProgress(mCurrentPosition);
                        }

catch (Exception e){
                            e.printStackTrace();
}


                    }
                    handler.postDelayed(this,1000);
                }


            });
            musicService.onCompleted();
            play_pauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            musicService.showNotification(R.drawable.ic_baseline_pause_24);

            musicService.start();

        }
        else{
            musicService.stop();
            musicService.release();
            if (shuffleBoolean &&!repeatBoolean){
                position=getRandom(listSongs.size()-1);

            }
            else if (!repeatBoolean&&!shuffleBoolean){
                position = ((position - 1)<0?  (listSongs.size()-1):(position-1));

            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            songArtist.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            playing.setText("Audio is stopped");

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);



                    }
                    handler.postDelayed(this,1000);
                }


            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_baseline_play_arrow_24);

            play_pauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);

        }
    }

    private void nextBtnThread() {
        nextThread =new Thread(){
            @Override
            public void run() {
                super.run();
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextBtnOnClicked();
                }
            });

            }
        };
nextThread.start();
    }

    public void nextBtnOnClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            if (shuffleBoolean &&!repeatBoolean){
                position=getRandom(listSongs.size() - 1);

            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1) % listSongs.size());

            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer( position);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            songArtist.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);



                    }
                    handler.postDelayed(this,1000);
                }


            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_baseline_pause_24);

            play_pauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            musicService.start();

        }
        else{
            musicService.stop();
            musicService.release();
            if (shuffleBoolean &&!repeatBoolean){
                position=getRandom(listSongs.size() - 1);

            }
            else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1) % listSongs.size());

            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            songName.setText(listSongs.get(position).getTitle());
            songArtist.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            playing.setText("Audio is stopped");
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);



                    }
                    handler.postDelayed(this,1000);
                }


            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_baseline_play_arrow_24);

            play_pauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);

        }
    }

    private int getRandom(int i) {
        Random random=new Random();
        return random.nextInt(0 + 1);
    }

    private void playBtnThread() {
        playThread=new Thread(){

            @Override
            public void run() {
                super.run();
                play_pauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        play_pauseBtnOnClicked();
                    }
                });
            }
        };
playThread.start();


    }
    

    public void play_pauseBtnOnClicked() {
        if (musicService.isPlaying()){
            play_pauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            musicService.showNotification(R.drawable.ic_baseline_play_arrow_24);

            musicService.pause();
            playing.setText("Audio is stopped");
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);



                    }
                    handler.postDelayed(this,1000);
                }


            });
        }
        else{
            musicService.showNotification(R.drawable.ic_baseline_pause_24);
            play_pauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);

            musicService.start();
            playing.setText("Now Playing ...");

            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService!=null){
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);



                    }
                    handler.postDelayed(this,1000);
                }


            });

        }
    }


    private String formattedTime(int mCurrentPosition) {
        String totalout="";
        String totalnew="";
        String seconds=String.valueOf(mCurrentPosition%60);
        String minutes=String.valueOf(mCurrentPosition/60);
        totalout=minutes+":"+seconds;
        totalnew=minutes+":"+"0"+seconds;
        if (seconds.length()==1){
            return totalnew;

        }
else{
    return totalout;
        }
    }

    private void getIntentMethod() {
        position=getIntent().getIntExtra("position",-1);
String sender=getIntent().getStringExtra("sender");
if(sender!=null&&sender.equals("albumDetails")){
    listSongs=albumFiles;
}else {
    listSongs = musicFiles;
}


    if (listSongs!=null){
            play_pauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            uri=Uri.parse(listSongs.get(position).getPath());

        }

        Intent intent=new Intent(this,MusicService.class);
    intent.putExtra("servicePosition",position);
    startService(intent);


    }

    private void initViews() {
        song_cover=findViewById(R.id.songImage);
        play_pauseBtn=findViewById(R.id.play);
        nextBtn=findViewById(R.id.next);
        prevBtn=findViewById(R.id.prev);
        repeatBtn=findViewById(R.id.repeat);
        shuffleBtn=findViewById(R.id.shuffle);
        durationPlayed=findViewById(R.id.txtstart);
        duration_Total=findViewById(R.id.end);
        songName=findViewById(R.id.songName);
        playing=findViewById(R.id.playing);
        seekBar=findViewById(R.id.seekBar);
        songArtist=findViewById(R.id.artistName);

    }
    private void metaData(Uri uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal=Integer.parseInt(listSongs.get(position).getDuration())/1000;
        duration_Total.setText(formattedTime(durationTotal));
         byte[] art= retriever.getEmbeddedPicture();
         if (art!=null){

             Glide.with(this).asBitmap().load(art).into(song_cover);



         }
else{             Glide.with(this).asBitmap().load(R.drawable.musiks).into(song_cover);


         }





    }



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
MusicService.MyBinder myBinder=(MusicService.MyBinder) service;
musicService =myBinder.getService();
musicService.setCallBack(this);
    seekBar.setMax(musicService.getDuration() / 1000);
    metaData(uri);
    songName.setText(listSongs.get(position).getTitle());
    songArtist.setText(listSongs.get(position).getArtist());
    musicService.onCompleted();
    musicService.showNotification(R.drawable.ic_baseline_pause_24);



    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
musicService=null;
    }

}