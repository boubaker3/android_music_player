package com.example.musicplayer_proaudioplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.holder> {
    private Context mContext;
    static ArrayList<MusicFiles> albumFiles;
    View view;
    public AlbumDetailsAdapter(Context mContext,ArrayList<MusicFiles> albumFiles){
        this.mContext=mContext;
        this.albumFiles=albumFiles;

    }
    @NonNull
    @Override
    public AlbumDetailsAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(mContext).inflate(R.layout.musicd_items,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AlbumDetailsAdapter.holder holder, int position) {
        holder.album_name.setText(albumFiles.get(position).getTitle());
        byte[] image=getAlbumArt(albumFiles.get(position).getPath());
        if (image!=null){
            Glide.with(mContext).asBitmap().load(image).into(holder.album_image);
        }
        else{
            Glide.with(mContext).asBitmap().load(R.drawable.music_note_24).into(holder.album_image);
        }
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(mContext,PlayerActivity.class);
        intent.putExtra("sender","albumDetails");
        intent.putExtra("position",position);
        mContext.startActivity(intent);
    }
});
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }
    public class holder extends RecyclerView.ViewHolder{
        ImageView album_image;
        TextView album_name;

        public holder(@NonNull  View itemView) {
            super(itemView);
            album_image=itemView.findViewById(R.id.album_art);
            album_name=itemView.findViewById(R.id.file_name);
        }
    }
    private byte[] getAlbumArt(String uri){

        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;


    }
}
