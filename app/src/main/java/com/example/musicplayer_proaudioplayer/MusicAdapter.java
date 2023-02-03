package com.example.musicplayer_proaudioplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
Context mContext;
ArrayList<MusicFiles> mFiles;

    public MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mContext = mContext;
        this.mFiles = mFiles;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MusicAdapter.MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
byte[] image=getAlbumArt(mFiles.get(position).getPath());
if (image!=null){
    Glide.with(mContext).asBitmap().load(image).into(holder.album_art);

}
else {
    Glide.with(mContext).asBitmap().load(R.drawable.music_note_24).into(holder.album_art);
}
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(mContext,PlayerActivity.class);
            intent.putExtra("position",position);
            mContext.startActivity(intent);
        }
    });

holder.deleteImg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        PopupMenu popupMenu=new PopupMenu(mContext,v);
        popupMenu.getMenuInflater().inflate(R.menu.pop_up,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete:
                        deleteFile(position,v);

                        break;
                }

                return true;
            }
        });
    }
});

    }

    private void deleteFile(int position, View v) {
        Uri contentUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,Long.parseLong(mFiles.get(position).getId()));
        File file=new File(mFiles.get(position).getPath());
        boolean deleted=file.delete();
        if (deleted) {
            mContext.getContentResolver().delete(contentUri,null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(position,mFiles);
        }


    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView album_art,deleteImg;
        TextView file_name;


        public MyViewHolder(@NonNull  View itemView) {
            super(itemView);
            album_art=itemView.findViewById(R.id.album_art);
            file_name=itemView.findViewById(R.id.file_name);
            deleteImg=itemView.findViewById(R.id.deleteImg);
        }
    }
    private byte[] getAlbumArt(String uri) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            byte[] art = retriever.getEmbeddedPicture();
            retriever.release();
            return art;
        } }




