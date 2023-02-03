package com.example.musicplayer_proaudioplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_CODE=1;
    static ArrayList<MusicFiles> albums=new ArrayList<>();
    static ArrayList<MusicFiles> musicFiles;

    static Boolean shuffleBoolean=false,repeatBoolean=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();


    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);

        }
        else {
            musicFiles=getAllAudio(this);
            initViewPager();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE==requestCode){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                musicFiles=getAllAudio(this);
                initViewPager();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                ,REQUEST_CODE);
            }
        }

    }

    private void initViewPager() {
        ViewPager viewP=(ViewPager)findViewById(R.id.viewP);
        TabLayout tabL=(TabLayout)findViewById(R.id.tabL);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongFragment() ,"Songs");
        viewPagerAdapter.addFragments(new AlbumFragment() ,"Albums");
        viewP.setAdapter(viewPagerAdapter);
        tabL.setupWithViewPager(viewP);
    }
    public class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;


  void addFragments(Fragment fragment,String title){
    fragments.add(fragment);
    titles.add(title);
}
        public ViewPagerAdapter(@NonNull  FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    public static ArrayList<MusicFiles> getAllAudio(Context context){
         ArrayList<String> duplicate=new ArrayList<>();
        ArrayList<MusicFiles> tempAudioList=new ArrayList<>();
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Media._ID};
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
        if (cursor!=null){
            while (cursor.moveToNext()){
                String artist= cursor.getString(0);
                String title= cursor.getString(1);
                String path= cursor.getString(2);
                String duration= cursor.getString(3);
                String album= cursor.getString(4);
                String id=cursor.getString(5);
                MusicFiles musicFiles=new MusicFiles(path,album,title,artist,duration,id);
                Log.e("path :"+path,"album :"+album);
                tempAudioList.add(musicFiles);
                if(!duplicate.contains(album)){
                    albums.add(musicFiles);
                    duplicate.add(album);
                }

            }
            cursor.close();

        }
return tempAudioList;
    }


}
