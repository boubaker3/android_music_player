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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.holder> {
    private Context mContext;
    private  ArrayList<MusicFiles> albumFiles;
    View view;
    public AlbumAdapter(Context mContext,ArrayList<MusicFiles> albumFiles){
        this.mContext=mContext;
        this.albumFiles=albumFiles;

    }
    @NonNull
    @Override
    public AlbumAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(mContext).inflate(R.layout.album_items,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AlbumAdapter.holder holder, int position) {
        holder.album_name.setText (albumFiles.get(position).getAlbum());
        byte[] image=getAlbumArt(albumFiles.get(position).getPath());
        if (image!=null){
            Glide.with(mContext).asBitmap().load(image).into(holder.album_image);
        }
        else{
            Glide.with(mContext).asBitmap().load(R.drawable.musiks).into(holder.album_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(mContext,AlbumDetails.class);
                intent.putExtra("albumName",albumFiles.get(position).getAlbum());
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
            album_image=itemView.findViewById(R.id.album_img);
            album_name=itemView.findViewById(R.id.album_name);
        }
    }

    private byte[] getAlbumArt(String uri){
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        retriever.setDataSource(uri);

        byte[] art = retriever.getEmbeddedPicture();

    retriever.release();
    return art;

}

    }

