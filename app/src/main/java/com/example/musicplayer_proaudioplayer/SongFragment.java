package com.example.musicplayer_proaudioplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;

import static com.example.musicplayer_proaudioplayer.MainActivity.musicFiles;


public class SongFragment extends Fragment {
RecyclerView rec;
static MusicAdapter musicAdapter;

    public SongFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_song, container, false);
        rec=v.findViewById(R.id.rec);
rec.setHasFixedSize(true);
        if (!(musicFiles.size()<=1)) {
            musicAdapter = new MusicAdapter(getContext(), musicFiles);
            rec.setAdapter(musicAdapter);
            rec.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));


        }

        return v;
    }
}